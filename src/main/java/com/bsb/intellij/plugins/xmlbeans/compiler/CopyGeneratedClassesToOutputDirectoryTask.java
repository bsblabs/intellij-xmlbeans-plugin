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
import com.bsb.intellij.plugins.xmlbeans.utils.CompilerUtils;
import com.bsb.intellij.plugins.xmlbeans.utils.XmlBeansConfiguration;
import com.bsb.intellij.plugins.xmlbeans.utils.file.FileUtils;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class CopyGeneratedClassesToOutputDirectoryTask extends AbstractXmlBeansCompileTask {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  public CopyGeneratedClassesToOutputDirectoryTask(XmlBeansFacet facet) {
    super(facet);
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Abstract method implementation

  @Override
  public boolean doExecute(CompileContext context, XmlBeansConfiguration configuration) {
    File generatedClassesDirectory = getGeneratedClassesDirectory(configuration);
    File outputDirectory = getOutputDirectory(configuration);
    if (generatedClassesDirectory == null) {
      return true; // Nothing to compile
    }
    if (outputDirectory == null) {
      addErrorMessage(context);
      return false; // No output directory to copy to
    }
    return copyQuietly(context, generatedClassesDirectory, outputDirectory);
  }

  @Override
  public String getTaskDescription() {
    return "copy of generated classes to output directory";
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private boolean copyQuietly(CompileContext context, File generatedClassesDirectory, File outputDirectory) {
    try {
      FileUtil.copyDirContent(generatedClassesDirectory, outputDirectory);
      return true;
    }
    catch (IOException e) {
      addErrorMessage(context);
      return false;
    }
  }

  @Nullable
  private File getGeneratedClassesDirectory(XmlBeansConfiguration config) {
    File generatedClassesDirectory = FileUtils.getModuleFile(getModule(), config.getGeneratedClassesDirectory());
    return generatedClassesDirectory.exists() ? generatedClassesDirectory : null;
  }

  @Nullable
  private File getOutputDirectory(XmlBeansConfiguration config) {
    VirtualFile compilerOutputPath = CompilerUtils.getCompilerOutputFileAndCreateItIfNeeded(getModule(), !config.getProduction());
    return compilerOutputPath == null ? null : new File(compilerOutputPath.getCanonicalPath());
  }

  private void addErrorMessage(CompileContext context) {
    context.addMessage(CompilerMessageCategory.ERROR, "Could not copy XMLBeans generated classes to output folder.", null, -1, -1);
  }
}
