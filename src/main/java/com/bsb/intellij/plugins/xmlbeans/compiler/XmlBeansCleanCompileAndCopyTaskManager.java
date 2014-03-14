package com.bsb.intellij.plugins.xmlbeans.compiler;

import com.bsb.intellij.plugins.xmlbeans.facet.XmlBeansFacet;
import com.bsb.intellij.plugins.xmlbeans.utils.facetcompiletask.AbstractFacetCompileTaskManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

/**
 * @author gja
 * @version $Revision: 2308 $ $Date: 2013-08-19 15:47:35 +0200 (lun., 19 août 2013) $
 */
public class XmlBeansCleanCompileAndCopyTaskManager extends AbstractFacetCompileTaskManager<XmlBeansFacet, XmlBeansCleanCompileAndCopyTask> {

  public static XmlBeansCleanCompileAndCopyTaskManager getInstance(Project project) {
    return ServiceManager.getService(project, XmlBeansCleanCompileAndCopyTaskManager.class);
  }

  public XmlBeansCleanCompileAndCopyTaskManager(Project project) {
    super(project, ExecutionMoment.BEFORE);
  }
}
