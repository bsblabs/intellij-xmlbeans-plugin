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
package com.bsb.intellij.plugins.xmlbeans.utils.silentrun;

import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.openapi.project.Project;

/**
 * @author gja
 * @version $Revision: 2241 $ $Date: 2013-07-04 14:26:36 +0200 (jeu., 04 juil. 2013) $
 */
public class SilentApplicationConfiguration extends ApplicationConfiguration {

  // Used to register SilentJavaRunner. Using this configuration will automatically use SilentJavaRunner to run.
  public SilentApplicationConfiguration(String name, Project project, ApplicationConfigurationType applicationConfigurationType) {
    super(name, project, applicationConfigurationType);
  }
}
