/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
import com.bsb.intellij.plugins.xmlbeans.utils.facetcompiletask.AbstractFacetCompileTask;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;

/**
 * @author GJA
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public abstract class AbstractXmlBeansCompileTask extends AbstractFacetCompileTask<XmlBeansFacet> {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Fields

  private final Module module;
  private final XmlBeansFacetConfiguration config;

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  public AbstractXmlBeansCompileTask(XmlBeansFacet facet) {
    super(facet);
    module = facet.getModule();
    config = facet.getConfiguration();
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Abstracts methods to implement

  public abstract boolean doExecute(CompileContext context, XmlBeansConfiguration configuration);

  public abstract String getTaskDescription();

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Public API

  @Override
  public final boolean doExecute(CompileContext context) {
    boolean result = executeIfExistsAndValid(context, config.getProductionConfig());
    if (config.isTestConfigEnabled()) {
      executeIfExistsAndValid(context, config.getTestConfig());
    }
    return result;
  }

  public Module getModule() {
    return module;
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private boolean executeIfExistsAndValid(CompileContext context, XmlBeansConfiguration configuration) {
    if (configuration != null && configuration.isValid(module)) {
      return doExecute(context, configuration);
    }
    else {
      context.addMessage(CompilerMessageCategory.WARNING, "Skipped " +
                                                          getTaskDescription() +
                                                          " for XMLBeans " +
                                                          (configuration.getProduction() ? "production" : "test") +
                                                          " execution on module " +
                                                          module.getName() +
                                                          " because configuration is invalid.", null, -1, -1);
      return true;
    }
  }
}
