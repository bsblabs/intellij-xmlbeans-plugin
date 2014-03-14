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
package com.bsb.intellij.plugins.xmlbeans.facet.ui.validation;

import com.intellij.facet.ui.FacetConfigurationQuickFix;
import com.intellij.facet.ui.FacetValidatorsManager;

import javax.swing.*;

/**
 * @author gja
 * @version $Revision: 2239 $ $Date: 2013-07-03 08:47:25 +0200 (mer., 03 juil. 2013) $
 */
public class DefaultValueQuickFix extends FacetConfigurationQuickFix {

  private final JTextField field;
  private final String defaultValue;
  private final FacetValidatorsManager manager;


  public DefaultValueQuickFix(JTextField field, String defaultValue, FacetValidatorsManager manager) {
    super("Set default value");
    this.field = field;
    this.defaultValue = defaultValue;
    this.manager = manager;
  }

  @Override
  public void run(JComponent place) {
    field.setText(defaultValue);
    manager.validate();
  }
}
