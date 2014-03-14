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
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;

import java.util.*;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public abstract class AbstractFacetCompileTaskManager<F extends Facet, C extends FacetCompileTask<F>> implements FacetCompileTaskManager<F, C> {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private fields

  private final Map<Module, C> tasks = new HashMap<Module, C>();
  private final Project project;

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  public AbstractFacetCompileTaskManager(Project project, ExecutionMoment executionMoment) {
    this.project = project;
    registerManagerExecution(project, executionMoment);
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Public API

  @Override
  public void register(C compileTask) {
    tasks.put(compileTask.getFacet().getModule(), compileTask);
  }

  @Override
  public void unRegister(F facet) {
    tasks.remove(facet.getModule());
  }

  @Override
  public boolean execute(CompileContext context) {
    removeObsoleteTasks();
    boolean result = true;
    if (!tasks.isEmpty()) {
      List<Module> modules = new ArrayList<Module>(tasks.keySet());
      Collections.sort(modules, getModuleDependencyComparator());
      for (Module module : modules) {
        if (tasks.containsKey(module)) {
          result &= tasks.get(module).execute(context);
        }
      }
    }
    return result;
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private void removeObsoleteTasks() {
    for (Module removableModule : getModulesWithoutFacet()) {
      tasks.remove(removableModule);
    }
  }

  private List<Module> getModulesWithoutFacet() {
    List<Module> modulesWithoutFacet = new ArrayList<Module>();
    for (Map.Entry<Module, C> entry : tasks.entrySet()) {
      F facet = entry.getValue().getFacet();
      if (facet == null || facet.isDisposed()) {
        modulesWithoutFacet.add(entry.getKey());
      }
    }
    return modulesWithoutFacet;
  }

  private void registerManagerExecution(Project project, ExecutionMoment executionMoment) {
    CompilerManager compilerManager = CompilerManager.getInstance(project);
    switch (executionMoment) {
      case BEFORE:
        compilerManager.addBeforeTask(this);
        break;
      case AFTER:
        compilerManager.addAfterTask(this);
        break;
      case BOTH:
        compilerManager.addBeforeTask(this);
        compilerManager.addAfterTask(this);
        break;
      default:
        throw new IllegalStateException("Cannot be reached");
    }
  }

  private Comparator<Module> getModuleDependencyComparator() {
    return new ReadAction<Comparator<Module>>() {
      @Override
      protected void run(Result<Comparator<Module>> result) {
        result.setResult(ModuleManager.getInstance(project).moduleDependencyComparator());
      }
    }.execute().getResultObject();
  }
}
