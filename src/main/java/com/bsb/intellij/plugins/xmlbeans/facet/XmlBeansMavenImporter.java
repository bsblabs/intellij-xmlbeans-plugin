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
package com.bsb.intellij.plugins.xmlbeans.facet;

import com.bsb.intellij.plugins.xmlbeans.utils.JavaSourceLevel;
import com.bsb.intellij.plugins.xmlbeans.utils.XmlBeansConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.LocalFileSystem;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.importing.FacetImporter;
import org.jetbrains.idea.maven.importing.MavenModifiableModelsProvider;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class XmlBeansMavenImporter extends FacetImporter<XmlBeansFacet, XmlBeansFacetConfiguration, XmlBeansFacetType> {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constants

  public static final String XMLBEANS_PLUGIN_GROUP_ID = "org.codehaus.mojo";
  public static final String XMLBEANS_PLUGIN_ARTIFACT_ID = "xmlbeans-maven-plugin";
  public static final String XMLBEANS_PRODUCTION_GOAL = "xmlbeans";
  public static final String XMLBEANS_TEST_GOAL = "xmlbeans-test";
  public static final String XML_ELEMENT_SCHEMA_DIRECTORY = "schemaDirectory";
  public static final String XML_ELEMENT_SOURCE_GENERATION_DIRECTORY = "sourceGenerationDirectory";
  public static final String XML_ELEMENT_CLASS_GENERATION_DIRECTORY = "classGenerationDirectory";
  public static final String XML_ELEMENT_DEFAULT_XML_CONFIG_DIR = "defaultXmlConfigDir";
  public static final String XML_ELEMENT_JAVA_SOURCE = "javaSource";
  public static final String XML_ELEMENT_MAX_MEMORY_SIZE = "memoryMaximumSize";

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  protected XmlBeansMavenImporter() {
    super(XMLBEANS_PLUGIN_GROUP_ID, XMLBEANS_PLUGIN_ARTIFACT_ID, XmlBeansFacetType.getFacetType());
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Public API

  @Override
  protected void setupFacet(XmlBeansFacet facet, MavenProject mavenProject) {
    configureXmlBeansFacet(facet, mavenProject, facet.getModule());
  }

  @Override
  public boolean isApplicable(MavenProject mavenProject) {
    MavenPlugin plugin = mavenProject.findPlugin(XMLBEANS_PLUGIN_GROUP_ID, XMLBEANS_PLUGIN_ARTIFACT_ID);
    return plugin != null && plugin.getExecutions() != null && !plugin.getExecutions().isEmpty();
  }

  @Override
  protected void reimportFacet(MavenModifiableModelsProvider modelsProvider,
                               Module module,
                               MavenRootModelAdapter rootModel,
                               XmlBeansFacet facet,
                               MavenProjectsTree mavenTree,
                               MavenProject mavenProject,
                               MavenProjectChanges changes,
                               Map<MavenProject, String> mavenProjectToModuleName,
                               List<MavenProjectsProcessorTask> postTasks) {
    configureXmlBeansFacet(facet, mavenProject, module);
  }

  @Override
  public void collectSourceFolders(MavenProject project, List<String> result) {
    super.collectSourceFolders(project, result);
    XmlBeansConfiguration productionConfig = getProductionConfig(project);
    createAndCollectSourceFolderIfAny(project, result, productionConfig);
  }

  @Override
  public void collectTestFolders(MavenProject project, List<String> result) {
    super.collectTestFolders(project, result);
    XmlBeansConfiguration testConfig = getTestConfig(project);
    createAndCollectSourceFolderIfAny(project, result, testConfig);
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private void createAndCollectSourceFolderIfAny(MavenProject project, List<String> result, XmlBeansConfiguration config) {
    if (config != null) {
      createAndAddGeneratedSourcesDirIfNeeded(project, result, config);
      addSourceDirectoriesIfExist(project, result, config);
    }
  }

  private void createAndAddGeneratedSourcesDirIfNeeded(MavenProject project, List<String> result, XmlBeansConfiguration config) {
    File generatedSourcesDir = createGeneratedSourcesDir(config, project);
    result.add(generatedSourcesDir.getAbsolutePath());
  }

  private File createGeneratedSourcesDir(XmlBeansConfiguration config, MavenProject project) {
    File file = new File(project.getDirectory(), config.getGeneratedSourcesDirectory());
    if (!file.exists() && file.mkdirs()) {
      LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
    }
    return file;
  }

  private void addSourceDirectoriesIfExist(MavenProject project, List<String> result, XmlBeansConfiguration config) {
    addFileIfExists(project, result, config.getSourcesDirectory());
    addFileIfExists(project, result, config.getXsdConfigDirectory());
  }

  private void addFileIfExists(MavenProject project, List<String> result, String relativePath) {
    File xsdSources = new File(project.getDirectory(), relativePath);
    if (xsdSources.exists()) {
      result.add(relativePath);
    }
  }

  private void configureXmlBeansFacet(XmlBeansFacet facet, MavenProject mavenProject, Module module) {
    configureProductionExecutionIfAny(facet, mavenProject);
    configureTestExecutionIfAny(facet, mavenProject, module);
  }

  private void configureProductionExecutionIfAny(XmlBeansFacet facet, MavenProject project) {
    XmlBeansConfiguration productionConfig = getProductionConfig(project);
    if (productionConfig != null) {
      facet.getConfiguration().setProductionConfig(productionConfig);
    }
  }

  private void configureTestExecutionIfAny(XmlBeansFacet facet, MavenProject project, Module module) {
    XmlBeansConfiguration config = getTestConfig(project);
    if (config != null && config.isValid(module)) {
      facet.getConfiguration().setTestConfig(config);
      facet.getConfiguration().setTestConfigEnabled(true);
    }
    else {
      facet.getConfiguration().setTestConfigEnabled(false);
    }
  }

  private XmlBeansConfiguration getProductionConfig(MavenProject project) {
    return getConfig(project, XMLBEANS_PRODUCTION_GOAL, XmlBeansFacetConfiguration.DEFAULT_PRODUCTION_CONFIG);
  }

  private XmlBeansConfiguration getTestConfig(MavenProject project) {
    return getConfig(project, XMLBEANS_TEST_GOAL, XmlBeansFacetConfiguration.DEFAULT_TEST_CONFIG);
  }

  private XmlBeansConfiguration getConfig(MavenProject project, String goal, XmlBeansConfiguration defaultValues) {
    if (isGoalExecuted(project, goal)) {
      XmlBeansConfiguration config = defaultValues.cloneQuietly();
      configureGoalExecution(config, project, goal);
      return config;
    }
    return null;
  }

  private boolean isGoalExecuted(MavenProject project, String goal) {
    MavenPlugin plugin = project.findPlugin(XMLBEANS_PLUGIN_GROUP_ID, XMLBEANS_PLUGIN_ARTIFACT_ID);
    if (plugin != null) {
      for (MavenPlugin.Execution execution : plugin.getExecutions()) {
        if (execution.getGoals().contains(goal)) {
          return true;
        }
      }
    }
    return false;
  }

  private void configureGoalExecution(XmlBeansConfiguration config, MavenProject project, String goal) {
    configureSourcesDirectory(config, project, goal);
    configureGeneratedSourcesDirectory(config, project, goal);
    configureGeneratedClassesDirectory(config, project, goal);
    configureXsdSourceDirectory(config, project, goal);
    configureSourceLevel(config, project, goal);
    configureMaxMemorySize(config, project, goal);
  }

  private void configureSourcesDirectory(XmlBeansConfiguration config, MavenProject project, String goal) {
    String goalConfigValue = this.findGoalConfigValue(project, goal, XML_ELEMENT_SCHEMA_DIRECTORY);
    if (StringUtils.isNotBlank(goalConfigValue)) {
      config.setSourcesDirectory(goalConfigValue);
    }
  }

  private void configureGeneratedSourcesDirectory(XmlBeansConfiguration config, MavenProject project, String goal) {
    String goalConfigValue = this.findGoalConfigValue(project, goal, XML_ELEMENT_SOURCE_GENERATION_DIRECTORY);
    if (StringUtils.isNotBlank(goalConfigValue)) {
      config.setGeneratedSourcesDirectory(goalConfigValue);
    }
  }

  private void configureGeneratedClassesDirectory(XmlBeansConfiguration config, MavenProject project, String goal) {
    String goalConfigValue = this.findGoalConfigValue(project, goal, XML_ELEMENT_CLASS_GENERATION_DIRECTORY);
    if (StringUtils.isNotBlank(goalConfigValue)) {
      config.setGeneratedClassesDirectory(goalConfigValue);
    }
  }

  private void configureXsdSourceDirectory(XmlBeansConfiguration config, MavenProject project, String goal) {
    String goalConfigValue = this.findGoalConfigValue(project, goal, XML_ELEMENT_DEFAULT_XML_CONFIG_DIR);
    if (StringUtils.isNotBlank(goalConfigValue)) {
      config.setXsdConfigDirectory(goalConfigValue);
    }
  }

  private void configureSourceLevel(XmlBeansConfiguration config, MavenProject project, String goal) {
    String javaSource = this.findGoalConfigValue(project, goal, XML_ELEMENT_JAVA_SOURCE);
    if (StringUtils.isNotBlank(javaSource)) {
      config.setSourceLevel(JavaSourceLevel.fromRepresentation(javaSource));
    }
  }

  private void configureMaxMemorySize(XmlBeansConfiguration config, MavenProject project, String goal) {
    String maxMemorySize = this.findGoalConfigValue(project, goal, XML_ELEMENT_MAX_MEMORY_SIZE);
    if (StringUtils.isNotBlank(maxMemorySize)) {
      config.setMaxMemorySize(maxMemorySize);
    }
  }

  @Nullable
  @Override
  protected String findGoalConfigValue(MavenProject p, String goal, String path) {
    String goalConfigValue = super.findGoalConfigValue(p, goal, path);
    return StringUtils.isNotBlank(goalConfigValue) ? goalConfigValue : this.findConfigValue(p, path);
  }
}
