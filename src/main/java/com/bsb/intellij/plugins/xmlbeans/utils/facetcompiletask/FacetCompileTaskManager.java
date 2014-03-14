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
package com.bsb.intellij.plugins.xmlbeans.utils.facetcompiletask;

import com.intellij.facet.Facet;
import com.intellij.openapi.compiler.CompileTask;

/**
 * @author gja
 * @version $Revision: 2239 $ $Date: 2013-07-03 08:47:25 +0200 (mer., 03 juil. 2013) $
 */
public interface FacetCompileTaskManager<F extends Facet, C extends FacetCompileTask<F>> extends CompileTask {

  enum ExecutionMoment {
    BEFORE, AFTER, BOTH
  }

  void register(C compileTask);

  void unRegister(F facet);
}
