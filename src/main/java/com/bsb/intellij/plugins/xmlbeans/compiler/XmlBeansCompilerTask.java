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
import com.intellij.openapi.compiler.CompileContext;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class XmlBeansCompilerTask extends AbstractXmlBeansCompileTask {

  public XmlBeansCompilerTask(XmlBeansFacet facet) {
    super(facet);
  }

  @Override
  public boolean doExecute(CompileContext context, XmlBeansConfiguration configuration) {
    return new XmlBeansCompiler(configuration, getModule(), context).compile();
  }

  @Override
  public String getTaskDescription() {
    return "XSDs Compilation";
  }
}
