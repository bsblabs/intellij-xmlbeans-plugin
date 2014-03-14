/*
 * #%L
 * XMLBeans integration
 * %%
 * Copyright (C) 2013 - 2014 BSB S.A.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.bsb.intellij.plugins.xmlbeans.compiler;

import com.bsb.intellij.plugins.xmlbeans.utils.XmlBeansConfiguration;
import com.bsb.intellij.plugins.xmlbeans.utils.silentrun.SilentApplicationConfiguration;
import com.intellij.execution.*;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.util.concurrency.Semaphore;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * @author gja
 * @version $Revision: 2507 $ $Date: 2013-12-19 16:46:49 +0100 (jeu., 19 déc. 2013) $
 */
public class XmlBeansCompiler {

  private static final Logger logger = Logger.getInstance(XmlBeansCompiler.class);

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constants

  private static final String GENERATED_CLASSES_TOKEN = "${generatedClasses}";
  private static final String GENERATED_SOURCES_TOKEN = "${generatedSources}";
  private static final String JAVA_SOURCE_TOKEN = "${javaSource}";
  private static final String MAX_MEMORY_SIZE_TOKEN = "${maxMemorySize}";
  private static final String SOURCE_DIR_TOKEN = "${sourceDir}";
  private static final String XSD_CONFIG_DIR_TOKEN = "${xsdConfigDir}";
  // Compilation arguments : -d ${generatedClasses} -src ${generatedSources} -srconly -javasource ${javaSource} -mx ${maxMemorySize} ${sourceDir} ${xsdConfigDir}
  private static final String COMPILATION_ARGUMENTS = "-d " + GENERATED_CLASSES_TOKEN +
                                                      " -src " + GENERATED_SOURCES_TOKEN +
                                                      " -srconly -javasource " + JAVA_SOURCE_TOKEN +
                                                      " -mx " + MAX_MEMORY_SIZE_TOKEN +
                                                      " " + SOURCE_DIR_TOKEN +
                                                      " " + XSD_CONFIG_DIR_TOKEN;
  private static final String XMLBEANS_COMPILER_CLASS_FQN = "org.apache.xmlbeans.impl.tool.SchemaCompiler";

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private fields

  private final XmlBeansConfiguration configuration;
  private final Module module;
  private final String moduleRootDirectory;
  private final Semaphore targetDone;
  private final XmlBeansCompilerProcessListener listener;
  private final Executor executor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);
  private ProgramRunner runner;
  private ExecutionEnvironment environment;

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  public XmlBeansCompiler(XmlBeansConfiguration configuration, Module module, CompileContext context) {
    this.configuration = configuration;
    this.module = module;
    this.moduleRootDirectory = PathMacroUtil.getModuleDir(module.getModuleFilePath());
    this.targetDone = new Semaphore();
    this.listener = new XmlBeansCompilerProcessListener(targetDone, context);
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Public API

  public boolean compile() {
    prepareCompiler();
    doCompile();
    return listener.isSuccess();
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private void prepareCompiler() {
    ApplicationConfiguration applicationConfiguration = buildApplicationConfiguration();
    this.runner = RunnerRegistry.getInstance().getRunner(executor.getId(), applicationConfiguration);
    assert runner != null;
    RunManagerImpl runManager = (RunManagerImpl)RunManager.getInstance(module.getProject());
    runManager.setBeforeRunTasks(applicationConfiguration, new ArrayList<BeforeRunTask>(), false); // Hack to avoid triggering make.
    RunnerAndConfigurationSettingsImpl runnerSettings = new RunnerAndConfigurationSettingsImpl(runManager, applicationConfiguration, false);
    this.environment = new ExecutionEnvironment(executor, runner, runnerSettings, module.getProject());
  }

  private void doCompile() {
    long start = System.currentTimeMillis();
    logger.info("XMLBeans : starting compilation for : " + module.getName());
    targetDone.down();
    runCompiler();
    targetDone.waitFor();
    logger.info("XMLBeans : starting compilation for : " + module.getName() + " in " + (System.currentTimeMillis() - start) + " ms");
  }

  private void runCompiler() {
    ApplicationManager.getApplication().invokeAndWait(new Runnable() {
      @Override
      public void run() {
        try {
          runner.execute(environment, new ProgramRunner.Callback() {
            @Override
            public void processStarted(RunContentDescriptor descriptor) {
              ProcessHandler processHandler = descriptor.getProcessHandler();
              if (processHandler != null) {
                processHandler.addProcessListener(listener);
              }
            }
          });
        }
        catch (ExecutionException e) {
          throw new IllegalStateException("Could not compile XMLBeans sources.", e);
        }
      }
    }, ModalityState.NON_MODAL);
  }

  private ApplicationConfiguration buildApplicationConfiguration() {
    SilentApplicationConfiguration config =
      new SilentApplicationConfiguration(XMLBEANS_COMPILER_CLASS_FQN, module.getProject(), ApplicationConfigurationType.getInstance());
    config.setWorkingDirectory(resolvePath(configuration.getSourcesDirectory()));
    config.setMainClassName(XMLBEANS_COMPILER_CLASS_FQN);
    config.setModule(module);
    String programParameters = getProgramParameters();
    config.setProgramParameters(programParameters);
    return config;
  }

  private String getProgramParameters() {
    return COMPILATION_ARGUMENTS.replace(GENERATED_CLASSES_TOKEN, resolvePath(configuration.getGeneratedClassesDirectory()))
      .replace(GENERATED_SOURCES_TOKEN, resolvePath(configuration.getGeneratedSourcesDirectory()))
      .replace(JAVA_SOURCE_TOKEN, configuration.getSourceLevel().getRepresentation())
      .replace(MAX_MEMORY_SIZE_TOKEN, configuration.getMaxMemorySize())
      .replace(SOURCE_DIR_TOKEN, "./")
      .replace(XSD_CONFIG_DIR_TOKEN, getXsdConfigDirectoryPath()).trim();
  }

  private String getXsdConfigDirectoryPath() {
    return StringUtils.isNotBlank(configuration.getXsdConfigDirectory()) ? resolvePath(configuration.getXsdConfigDirectory()) : "";
  }

  private String resolvePath(String relativePath) {
    return moduleRootDirectory + File.separator + relativePath;
  }
}
