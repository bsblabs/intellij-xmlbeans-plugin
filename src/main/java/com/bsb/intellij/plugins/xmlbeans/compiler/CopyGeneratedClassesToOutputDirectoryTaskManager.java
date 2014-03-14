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
import com.bsb.intellij.plugins.xmlbeans.utils.facetcompiletask.AbstractFacetCompileTaskManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class CopyGeneratedClassesToOutputDirectoryTaskManager
  extends AbstractFacetCompileTaskManager<XmlBeansFacet, CopyGeneratedClassesToOutputDirectoryTask> {

  public static CopyGeneratedClassesToOutputDirectoryTaskManager getInstance(Project project) {
    return ServiceManager.getService(project, CopyGeneratedClassesToOutputDirectoryTaskManager.class);
  }

  public CopyGeneratedClassesToOutputDirectoryTaskManager(Project project) {
    super(project, ExecutionMoment.AFTER);
  }

  @Override
  public boolean execute(CompileContext context) {
    if (context.isRebuild()) {
      return super.execute(context);
    }
    return true;
  }
}
