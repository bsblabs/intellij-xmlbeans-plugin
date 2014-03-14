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
package com.bsb.intellij.plugins.xmlbeans.utils;

import javax.swing.*;
import java.awt.*;

/**
 * @author gja
 * @version $Revision: 2247 $ $Date: 2013-07-05 14:22:20 +0200 (ven., 05 juil. 2013) $
 */
public final class SwingUtils {

  private SwingUtils() {
    throw new UnsupportedOperationException();
  }

  public static boolean isVisible(Container container) {
    Container currentComponent = container;
    while (currentComponent != null) {
      if (!currentComponent.isVisible()) {
        return false;
      }
      currentComponent = currentComponent.getParent();
    }
    return true;
  }

  public static boolean isTextFieldModified(JTextField textField, String originalValue) {
    return (textField.getText() != null ? !textField.getText().equals(originalValue) : originalValue != null);
  }

  public static boolean isCheckBoxModified(JCheckBox checkBox, boolean originalValue) {
    return checkBox.isSelected() != originalValue;
  }

  public static boolean IsComboBoxModified(JComboBox comboBox, Object originalValue) {
    return (comboBox.getSelectedItem() != null ? !comboBox.getSelectedItem().equals(originalValue) : originalValue != null);
  }
}
