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

import com.bsb.intellij.plugins.xmlbeans.compiler.CopyGeneratedClassesToOutputDirectoryTask;
import com.bsb.intellij.plugins.xmlbeans.compiler.CopyGeneratedClassesToOutputDirectoryTaskManager;
import com.bsb.intellij.plugins.xmlbeans.compiler.XmlBeansCleanCompileAndCopyTask;
import com.bsb.intellij.plugins.xmlbeans.compiler.XmlBeansCleanCompileAndCopyTaskManager;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class XmlBeansFacet extends Facet<XmlBeansFacetConfiguration> {

  public XmlBeansFacet(@NotNull FacetType facetType,
                       @NotNull Module module,
                       @NotNull String name,
                       @NotNull XmlBeansFacetConfiguration configuration) {
    super(facetType, module, name, configuration, null);
    this.getConfiguration().initialize(this.getModule());
  }

  @Override
  public void initFacet() {
    super.initFacet();
    Project project = this.getModule().getProject();
    XmlBeansCleanCompileAndCopyTaskManager.getInstance(project).register(new XmlBeansCleanCompileAndCopyTask(this));
    CopyGeneratedClassesToOutputDirectoryTaskManager.getInstance(project).register(new CopyGeneratedClassesToOutputDirectoryTask(this));
  }

  @Override
  public void disposeFacet() {
    super.disposeFacet();
    Project project = this.getModule().getProject();
    XmlBeansCleanCompileAndCopyTaskManager.getInstance(project).unRegister(this);
    CopyGeneratedClassesToOutputDirectoryTaskManager.getInstance(project).unRegister(this);
  }
}
