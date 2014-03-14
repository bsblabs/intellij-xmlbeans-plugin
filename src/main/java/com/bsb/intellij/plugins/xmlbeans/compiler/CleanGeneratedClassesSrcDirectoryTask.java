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

import com.bsb.intellij.plugins.xmlbeans.facet.XmlBeansFacet;
import com.bsb.intellij.plugins.xmlbeans.utils.XmlBeansConfiguration;
import com.bsb.intellij.plugins.xmlbeans.utils.file.FileUtils;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class CleanGeneratedClassesSrcDirectoryTask extends AbstractXmlBeansCompileTask {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constants

  private static final String XMLBEANS_GENERATED_SOURCES_SRC_PATH = "/schemaorg_apache_xmlbeans/src";

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  public CleanGeneratedClassesSrcDirectoryTask(XmlBeansFacet facet) {
    super(facet);
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Abstract methods implementation

  @Override
  public boolean doExecute(CompileContext context, XmlBeansConfiguration configuration) {
    File generatedClassesSrcDirectory = getGeneratedClassesSrcDirectory(configuration);
    if (generatedClassesSrcDirectory.exists() && !FileUtil.delete(generatedClassesSrcDirectory)) {
      context
        .addMessage(CompilerMessageCategory.ERROR, "Could not delete file : " + generatedClassesSrcDirectory.getAbsolutePath(), null, -1,
                    -1);
      return false;
    }
    return true;
  }

  @Override
  public String getTaskDescription() {
    return "cleaning of target directories";
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private File getGeneratedClassesSrcDirectory(XmlBeansConfiguration configuration) {
    String generatedClassesSrcDirectoryPath = configuration.getGeneratedClassesDirectory() + XMLBEANS_GENERATED_SOURCES_SRC_PATH;
    return FileUtils.getModuleFile(getModule(), generatedClassesSrcDirectoryPath);
  }
}
