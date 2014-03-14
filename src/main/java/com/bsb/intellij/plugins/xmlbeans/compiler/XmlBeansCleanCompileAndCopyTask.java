/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bsb.intellij.plugins.xmlbeans.compiler;

import com.bsb.intellij.plugins.xmlbeans.facet.XmlBeansFacet;
import com.bsb.intellij.plugins.xmlbeans.facet.XmlBeansFacetConfiguration;
import com.bsb.intellij.plugins.xmlbeans.utils.XmlBeansConfiguration;
import com.bsb.intellij.plugins.xmlbeans.utils.facetcompiletask.CompositeFacetCompileTask;
import com.bsb.intellij.plugins.xmlbeans.utils.facetcompiletask.FacetCompileTask;
import com.bsb.intellij.plugins.xmlbeans.utils.file.FileUtils;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.module.Module;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class XmlBeansCleanCompileAndCopyTask extends CompositeFacetCompileTask<XmlBeansFacet> implements FacetCompileTask<XmlBeansFacet> {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private fields

  private Module module;
  private boolean lastSuccess = false;
  private XmlBeansFacetConfiguration configuration;

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  public XmlBeansCleanCompileAndCopyTask(XmlBeansFacet facet) {
    super(facet, new CleanGeneratedClassesSrcDirectoryTask(facet), new XmlBeansCompilerTask(facet),
          new CopyGeneratedClassesToOutputDirectoryTask(facet));
    module = facet.getModule();
    configuration = facet.getConfiguration();
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Public API

  @Override
  public boolean doExecute(CompileContext context) {
    if (shouldExecute(context)) {
      configuration.setDirty(false);
      lastSuccess = super.doExecute(context);
    }
    return lastSuccess;
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private boolean shouldExecute(CompileContext context) {
    return context.isRebuild() ||
           !lastSuccess ||
           configuration.isDirty() ||
           isOutOfDate(configuration.getProductionConfig()) ||
           (configuration.isTestConfigEnabled() && isOutOfDate(configuration.getTestConfig()));
  }

  private boolean isOutOfDate(XmlBeansConfiguration configuration) {
    long sourcesLastTimeStamp = FileUtils.getLastModifiedTimeStamp(getSourceDirectories(configuration));
    long targetLastTimeStamp = FileUtils.getLastModifiedTimeStamp(getTargetDirectories(configuration));
    return targetLastTimeStamp < sourcesLastTimeStamp;
  }

  private List<File> getSourceDirectories(XmlBeansConfiguration config) {
    List<File> sourceDirectories = new ArrayList<File>();
    sourceDirectories.add(FileUtils.getModuleFile(module, config.getSourcesDirectory()));
    addIfExists(sourceDirectories, FileUtils.getModuleFile(module, config.getXsdConfigDirectory()));
    return sourceDirectories;
  }

  private List<File> getTargetDirectories(XmlBeansConfiguration config) {
    ArrayList<File> targetDirectories = new ArrayList<File>();
    addIfExists(targetDirectories, FileUtils.getModuleFile(module, config.getGeneratedClassesDirectory()));
    addIfExists(targetDirectories, FileUtils.getModuleFile(module, config.getGeneratedSourcesDirectory()));
    return targetDirectories;
  }

  private void addIfExists(List<File> directories, File directory) {
    if (directory.exists()) {
      directories.add(directory);
    }
  }
}
