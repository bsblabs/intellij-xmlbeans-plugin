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
