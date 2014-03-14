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
package com.bsb.intellij.plugins.xmlbeans.utils.silentrun;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author gja
 * @version $Revision: 2507 $ $Date: 2013-12-19 16:46:49 +0100 (jeu., 19 déc. 2013) $
 */
public class SilentJavaRunner extends DefaultJavaProgramRunner {

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return profile instanceof SilentApplicationConfiguration;
  }

  @NotNull
  @Override
  public String getRunnerId() {
    return "SilentRun";
  }

  @Override
  protected RunContentDescriptor doExecute(final Project project,
                                           final RunProfileState state,
                                           final RunContentDescriptor contentToReuse,
                                           final ExecutionEnvironment env) throws ExecutionException {
    RunContentDescriptor runContentDescriptor = super.doExecute(project, state, contentToReuse, env);
    if (runContentDescriptor != null) {
      // Prevent "Run" tool window to pop up
      runContentDescriptor.setActivateToolWindowWhenAdded(false);
    }
    return runContentDescriptor;
  }
}
