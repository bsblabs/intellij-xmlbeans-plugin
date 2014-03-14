package com.bsb.intellij.plugins.xmlbeans.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author gja
 * @version $Revision: 2232 $ $Date: 2013-06-13 15:11:10 +0200 (jeu., 13 juin 2013) $
 */
public class XmlBeansFacetType extends FacetType<XmlBeansFacet, XmlBeansFacetConfiguration> {

  public static final String STRING_ID = "xmlBeans";
  public static final String PRESENTABLE_NAME = "XMLBeans";
  public static final FacetTypeId<XmlBeansFacet> FACET_TYPE_ID = new FacetTypeId<XmlBeansFacet>(STRING_ID);

  public static XmlBeansFacetType getFacetType() {
    return (XmlBeansFacetType)FacetTypeRegistry.getInstance().findFacetType(FACET_TYPE_ID);
  }

  public XmlBeansFacetType() {
    super(FACET_TYPE_ID, STRING_ID, PRESENTABLE_NAME);
  }

  @Override
  public XmlBeansFacetConfiguration createDefaultConfiguration() {
    return new XmlBeansFacetConfiguration();
  }

  @Override
  public XmlBeansFacet createFacet(@NotNull Module module,
                                   String name,
                                   @NotNull XmlBeansFacetConfiguration configuration,
                                   @Nullable Facet underlyingFacet) {
    return new XmlBeansFacet(this, module, name, configuration);
  }

  @Override
  public boolean isSuitableModuleType(ModuleType moduleType) {
    return moduleType instanceof JavaModuleType;
  }
}
