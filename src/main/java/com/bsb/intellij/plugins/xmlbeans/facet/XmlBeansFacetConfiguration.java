package com.bsb.intellij.plugins.xmlbeans.facet;

import com.bsb.intellij.plugins.xmlbeans.facet.ui.XmlBeansFacetConfigurationEditorTab;
import com.bsb.intellij.plugins.xmlbeans.utils.JavaSourceLevel;
import com.bsb.intellij.plugins.xmlbeans.utils.SourceFolderUtil;
import com.bsb.intellij.plugins.xmlbeans.utils.ValidationUtils;
import com.bsb.intellij.plugins.xmlbeans.utils.XmlBeansConfiguration;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class XmlBeansFacetConfiguration implements FacetConfiguration, PersistentStateComponent<XmlBeansFacetConfiguration> {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constants

  public static final JavaSourceLevel DEFAULT_SOURCE_LEVEL = JavaSourceLevel.FOUR;
  public static final String DEFAULT_MEMORY_SIZE = "256m";

  public static final String DEFAULT_SOURCE_DIRECTORY = "src/main/xsd";
  public static final String DEFAULT_CONFIG_DIRECTORY = "src/main/xsdconfig";
  public static final String DEFAULT_GENERATED_SOURCES_DIRECTORY = "target/generated-sources/xmlbeans";
  public static final String DEFAULT_GENERATED_CLASSES_DIRECTORY = "target/generated-classes/xmlbeans";
  public static final XmlBeansConfiguration DEFAULT_PRODUCTION_CONFIG =
    new XmlBeansConfiguration(DEFAULT_SOURCE_DIRECTORY, DEFAULT_CONFIG_DIRECTORY, DEFAULT_GENERATED_SOURCES_DIRECTORY,
                              DEFAULT_GENERATED_CLASSES_DIRECTORY, DEFAULT_SOURCE_LEVEL, DEFAULT_MEMORY_SIZE, true);

  public static final String DEFAULT_TEST_SOURCE_DIRECTORY = "src/test/xsd";
  public static final String DEFAULT_TEST_CONFIG_DIRECTORY = "src/test/xsdconfig";
  public static final String DEFAULT_TEST_GENERATED_SOURCES_DIRECTORY = "target/generated-sources/test-xmlbeans";
  public static final String DEFAULT_TEST_GENERATED_CLASSES_DIRECTORY = "target/generated-classes/test-xmlbeans";
  public static final XmlBeansConfiguration DEFAULT_TEST_CONFIG =
    new XmlBeansConfiguration(DEFAULT_TEST_SOURCE_DIRECTORY, DEFAULT_TEST_CONFIG_DIRECTORY, DEFAULT_TEST_GENERATED_SOURCES_DIRECTORY,
                              DEFAULT_TEST_GENERATED_CLASSES_DIRECTORY, DEFAULT_SOURCE_LEVEL, DEFAULT_MEMORY_SIZE, false);

  // ------------------------------------------------------------------------------------------------------------------------------------
  // FacetConfiguration implementation

  @Override
  public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
    XmlBeansFacetConfigurationEditorTab editorTab =
      new XmlBeansFacetConfigurationEditorTab(this, validatorsManager, editorContext.getModule());
    return new FacetEditorTab[]{editorTab};
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
    // Deprecated. See super.
  }

  @Override
  public void writeExternal(Element element) throws WriteExternalException {
    // Deprecated. See super.
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // PersistentStateComponent implementation

  @Nullable
  @Override
  public XmlBeansFacetConfiguration getState() {
    return this;
  }

  @Override
  public void loadState(XmlBeansFacetConfiguration state) {
    this.setProductionConfig(state.getProductionConfig());
    this.setTestConfig(state.getTestConfig());
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Business implementation

  private XmlBeansConfiguration productionConfig = DEFAULT_PRODUCTION_CONFIG.cloneQuietly();
  private XmlBeansConfiguration testConfig = DEFAULT_TEST_CONFIG.cloneQuietly();
  private boolean testConfigEnabled = true;
  private boolean dirty = true;

  public XmlBeansConfiguration getProductionConfig() {
    return productionConfig;
  }

  public void setProductionConfig(XmlBeansConfiguration productionConfig) {
    productionConfig.setProduction(true);
    this.productionConfig = productionConfig;
  }

  public XmlBeansConfiguration getTestConfig() {
    return testConfig;
  }

  public void setTestConfig(XmlBeansConfiguration testConfig) {
    testConfig.setProduction(false);
    this.testConfig = testConfig;
  }

  public boolean isTestConfigEnabled() {
    return testConfigEnabled;
  }

  public void setTestConfigEnabled(boolean testConfigEnabled) {
    this.testConfigEnabled = testConfigEnabled;
  }

  public boolean isDirty() {
    return dirty;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  private Module module;

  public void initialize(Module module) {
    this.module = module;
  }

  public void initConfigsIfValid() {
    initConfigsIfValid(productionConfig, false);
    if (isTestConfigEnabled()) {
      initConfigsIfValid(testConfig, true);
    }
  }

  public void initConfigsIfValid(XmlBeansConfiguration config, boolean test) {
    if (ValidationUtils.isValidRelativePath(config.getGeneratedSourcesDirectory())) {
      addSourceFolder(config.getGeneratedSourcesDirectory(), test);
    }
    addSourceFolderIfValidAndExists(config.getSourcesDirectory(), test);
    addSourceFolderIfValidAndExists(config.getXsdConfigDirectory(), test);
  }

  private void addSourceFolderIfValidAndExists(String sourcesDirectory, boolean test) {
    if (ValidationUtils.isValidRelativePath(sourcesDirectory) && ValidationUtils.isPathExists(sourcesDirectory, module)) {
      addSourceFolder(sourcesDirectory, test);
    }
  }

  private void addSourceFolder(String relativePath, boolean test) {
    if (test) {
      SourceFolderUtil.addTestSourceFolder(relativePath, module);
    }
    else {
      SourceFolderUtil.addSourceFolder(relativePath, module);
    }
  }
}
