package com.bsb.intellij.plugins.xmlbeans.facet;

import com.bsb.intellij.plugins.xmlbeans.compiler.CopyGeneratedClassesToOutputDirectoryTask;
import com.bsb.intellij.plugins.xmlbeans.compiler.CopyGeneratedClassesToOutputDirectoryTaskManager;
import com.bsb.intellij.plugins.xmlbeans.compiler.XmlBeansCleanCompileAndCopyTask;
import com.bsb.intellij.plugins.xmlbeans.compiler.XmlBeansCleanCompileAndCopyTaskManager;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class XmlBeansFacet extends Facet<XmlBeansFacetConfiguration> {

  public XmlBeansFacet(@NotNull FacetType facetType,
                       @NotNull Module module,
                       @NotNull String name,
                       @NotNull XmlBeansFacetConfiguration configuration) {
    super(facetType, module, name, configuration, null);
    this.getConfiguration().initialize(this.getModule());
  }

  @Override
  public void initFacet() {
    super.initFacet();
    Project project = this.getModule().getProject();
    XmlBeansCleanCompileAndCopyTaskManager.getInstance(project).register(new XmlBeansCleanCompileAndCopyTask(this));
    CopyGeneratedClassesToOutputDirectoryTaskManager.getInstance(project).register(new CopyGeneratedClassesToOutputDirectoryTask(this));
  }

  @Override
  public void disposeFacet() {
    super.disposeFacet();
    Project project = this.getModule().getProject();
    XmlBeansCleanCompileAndCopyTaskManager.getInstance(project).unRegister(this);
    CopyGeneratedClassesToOutputDirectoryTaskManager.getInstance(project).unRegister(this);
  }
}
