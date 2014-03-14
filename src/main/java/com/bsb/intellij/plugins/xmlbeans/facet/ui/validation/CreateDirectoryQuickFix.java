package com.bsb.intellij.plugins.xmlbeans.facet.ui.validation;

import com.intellij.facet.ui.FacetConfigurationQuickFix;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.vfs.VfsUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * @author gja
 * @version $Revision: 2308 $ $Date: 2013-08-19 15:47:35 +0200 (lun., 19 août 2013) $
 */
public class CreateDirectoryQuickFix extends FacetConfigurationQuickFix {

  private final File file;
  private final FacetValidatorsManager manager;
  private final Runnable quickFixCallback;

  public CreateDirectoryQuickFix(File file, FacetValidatorsManager manager, Runnable quickFixCallback) {
    super("Create directory");
    this.file = file;
    this.manager = manager;
    this.quickFixCallback = quickFixCallback;
  }

  @Override
  public void run(JComponent place) {
    try {
      VfsUtil.createDirectories(file.getAbsolutePath());
      this.quickFixCallback.run();
      manager.validate();
    }
    catch (IOException e) {
      throw new IllegalStateException("Could not create directory : " + file.getAbsolutePath(), e);
    }
  }
}
