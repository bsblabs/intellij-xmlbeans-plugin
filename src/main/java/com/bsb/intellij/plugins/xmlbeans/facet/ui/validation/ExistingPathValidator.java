package com.bsb.intellij.plugins.xmlbeans.facet.ui.validation;

import com.bsb.intellij.plugins.xmlbeans.utils.SwingUtils;
import com.bsb.intellij.plugins.xmlbeans.utils.ValidationUtils;
import com.intellij.facet.ui.FacetConfigurationQuickFix;
import com.intellij.facet.ui.FacetEditorValidator;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.module.Module;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import javax.swing.*;
import java.io.File;

/**
 * @author gja
 * @version $Revision: 2256 $ $Date: 2013-07-12 16:44:35 +0200 (ven., 12 juil. 2013) $
 */
public class ExistingPathValidator extends FacetEditorValidator {

  private JTextField field;
  private Module module;
  private FacetValidatorsManager manager;
  private Runnable quickFixCallback;

  public ExistingPathValidator(JTextField field, Module module, FacetValidatorsManager manager, Runnable quickFixCallback) {
    this.field = field;
    this.module = module;
    this.manager = manager;
    this.quickFixCallback = quickFixCallback;
  }

  @Override
  public ValidationResult check() {
    String errorMessage = null;
    FacetConfigurationQuickFix quickFix = null;
    String moduleDir = PathMacroUtil.getModuleDir(module.getModuleFilePath());
    String path = field.getText();
    if (SwingUtils.isVisible(field) && StringUtils.isNotBlank(path) && !ValidationUtils.isPathExists(path, module)) {
      errorMessage = "Directory " + path + " does not exists.";
      quickFix = new CreateDirectoryQuickFix(new File(moduleDir, path), manager, quickFixCallback);
    }
    return new ValidationResult(errorMessage, quickFix);
  }
}
