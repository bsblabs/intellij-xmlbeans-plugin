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
package com.bsb.intellij.plugins.xmlbeans.facet.ui.validation;

import com.intellij.facet.ui.FacetConfigurationQuickFix;
import com.intellij.facet.ui.FacetEditorValidator;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.ValidationResult;
import com.intellij.ui.components.JBLabel;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author gja
 * @version $Revision: 2240 $ $Date: 2013-07-03 17:53:25 +0200 (mer., 03 juil. 2013) $
 */
public abstract class AbstractFieldValidatorWithDefaultValueQuickFix extends FacetEditorValidator {

  private FacetConfigurationQuickFix quickFix;
  private JTextField textField;
  private JBLabel label;

  public AbstractFieldValidatorWithDefaultValueQuickFix(JTextField textField,
                                                        JBLabel label,
                                                        String defaultValue,
                                                        FacetValidatorsManager manager) {
    this.textField = textField;
    this.label = label;
    if (StringUtils.isNotBlank(defaultValue)) {
      this.quickFix = new DefaultValueQuickFix(textField, defaultValue, manager);
    }
  }

  @Override
  public final ValidationResult check() {
    return new ValidationResult(getErrorMessage(textField, label), quickFix);
  }

  @Nullable
  public abstract String getErrorMessage(JTextField textField, JBLabel label);
}
