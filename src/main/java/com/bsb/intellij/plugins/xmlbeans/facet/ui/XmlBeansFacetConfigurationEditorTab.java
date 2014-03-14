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
package com.bsb.intellij.plugins.xmlbeans.facet.ui;

import com.bsb.intellij.plugins.xmlbeans.facet.XmlBeansFacetConfiguration;
import com.bsb.intellij.plugins.xmlbeans.facet.ui.validation.ExistingPathValidator;
import com.bsb.intellij.plugins.xmlbeans.facet.ui.validation.MandatoryFieldValidator;
import com.bsb.intellij.plugins.xmlbeans.facet.ui.validation.ValidJvmMemoryParameterValidator;
import com.bsb.intellij.plugins.xmlbeans.facet.ui.validation.ValidRelativePathValidator;
import com.bsb.intellij.plugins.xmlbeans.utils.JavaSourceLevel;
import com.bsb.intellij.plugins.xmlbeans.utils.SwingUtils;
import com.intellij.facet.Facet;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class XmlBeansFacetConfigurationEditorTab extends FacetEditorTab {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private fields - Business

  private XmlBeansFacetConfiguration configuration;
  private FacetValidatorsManager manager;
  private Module module;
  private boolean dirty = false;

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private fields - GUI

  private JPanel configPanel;
  private JPanel productionPanel;
  private JBLabel sourcesLabel;
  private JTextField sources;
  private JBLabel xsdConfigLabel;
  private JTextField xsdConfig;
  private JBLabel generatedSourcesLabel;
  private JTextField generatedSources;
  private JBLabel generatedClassesLabel;
  private JTextField generatedClasses;
  private JPanel testPanel;
  private JBLabel testSourcesLabel;
  private JTextField testSources;
  private JBLabel testXsdConfigLabel;
  private JTextField testXsdConfig;
  private JBLabel testGeneratedSourcesLabel;
  private JTextField testGeneratedSources;
  private JBLabel testGeneratedClassesLabel;
  private JTextField testGeneratedClasses;
  private JCheckBox enableTestConfiguration;
  private JComboBox sourceLevel;
  private JComboBox testSourceLevel;
  private JBLabel sourceLevelLabel;
  private JBLabel testSourceLevelLabel;
  private JBLabel maxMemorySizeLabel;
  private JBLabel testMaxMemorySizeLabel;
  private JTextField maxMemorySize;
  private JTextField testMaxMemorySize;

  private JTextField[] fields =
    new JTextField[]{sources, xsdConfig, generatedSources, generatedClasses, testSources, testXsdConfig, testGeneratedSources,
      testGeneratedClasses};

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  public XmlBeansFacetConfigurationEditorTab(XmlBeansFacetConfiguration configuration, FacetValidatorsManager manager, Module module) {
    this.configuration = configuration;
    this.manager = manager;
    this.module = module;
    this.setData();
    this.setUpValidation();
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Public API

  @Override
  public void apply() throws ConfigurationException {
    if (isModified()) {
      manager.validate();
      this.getData();
      this.dirty = false;
      configuration.setDirty(true);
    }
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "XMLBeans";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    testPanel.setVisible(enableTestConfiguration.isSelected());
    enableTestConfiguration.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        testPanel.setVisible(enableTestConfiguration.isSelected());
        manager.validate();
      }
    });

    KeyListener keyListener = new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        manager.validate();
      }
    };

    for (JTextField field : fields) {
      field.addKeyListener(keyListener);
    }

    return configPanel;
  }

  @Override
  public void reset() {
  }

  @Override
  public void disposeUIResources() {
  }

  @Override
  public void onFacetInitialized(@NotNull Facet facet) {
    super.onFacetInitialized(facet);
    configuration.initConfigsIfValid();
  }

  @Override
  public boolean isModified() {
    return dirty ||
           SwingUtils.isTextFieldModified(sources, configuration.getProductionConfig().getSourcesDirectory()) ||
           SwingUtils.isTextFieldModified(xsdConfig, configuration.getProductionConfig().getXsdConfigDirectory()) ||
           SwingUtils.isTextFieldModified(generatedSources, configuration.getProductionConfig().getGeneratedSourcesDirectory()) ||
           SwingUtils.isTextFieldModified(generatedClasses, configuration.getProductionConfig().getGeneratedClassesDirectory()) ||
           SwingUtils.isTextFieldModified(maxMemorySize, configuration.getProductionConfig().getMaxMemorySize()) ||
           (configuration.getProductionConfig().getSourceLevel() != null &&
            SwingUtils.IsComboBoxModified(sourceLevel, configuration.getProductionConfig().getSourceLevel().getRepresentation())) ||

           SwingUtils.isTextFieldModified(testSources, configuration.getTestConfig().getSourcesDirectory()) ||
           SwingUtils.isTextFieldModified(testXsdConfig, configuration.getTestConfig().getXsdConfigDirectory()) ||
           SwingUtils.isTextFieldModified(testGeneratedSources, configuration.getTestConfig().getGeneratedSourcesDirectory()) ||
           SwingUtils.isTextFieldModified(testGeneratedClasses, configuration.getTestConfig().getGeneratedClassesDirectory()) ||
           SwingUtils.isTextFieldModified(testMaxMemorySize, configuration.getTestConfig().getMaxMemorySize()) ||
           (configuration.getTestConfig().getSourceLevel() != null &&
            SwingUtils.IsComboBoxModified(testSourceLevel, configuration.getTestConfig().getSourceLevel().getRepresentation())) ||

           SwingUtils.isCheckBoxModified(enableTestConfiguration, configuration.isTestConfigEnabled());
  }

  public final void setData() {
    sources.setText(configuration.getProductionConfig().getSourcesDirectory());
    xsdConfig.setText(configuration.getProductionConfig().getXsdConfigDirectory());
    generatedSources.setText(configuration.getProductionConfig().getGeneratedSourcesDirectory());
    generatedClasses.setText(configuration.getProductionConfig().getGeneratedClassesDirectory());
    sourceLevel.setSelectedItem(configuration.getProductionConfig().getSourceLevel().getRepresentation());
    maxMemorySize.setText(configuration.getProductionConfig().getMaxMemorySize());

    testSources.setText(configuration.getTestConfig().getSourcesDirectory());
    testXsdConfig.setText(configuration.getTestConfig().getXsdConfigDirectory());
    testGeneratedSources.setText(configuration.getTestConfig().getGeneratedSourcesDirectory());
    testGeneratedClasses.setText(configuration.getTestConfig().getGeneratedClassesDirectory());
    testSourceLevel.setSelectedItem(configuration.getTestConfig().getSourceLevel().getRepresentation());
    testMaxMemorySize.setText(configuration.getTestConfig().getMaxMemorySize());

    enableTestConfiguration.setSelected(configuration.isTestConfigEnabled());
  }

  public void getData() {
    configuration.getProductionConfig().setSourcesDirectory(sources.getText());
    configuration.getProductionConfig().setXsdConfigDirectory(xsdConfig.getText());
    configuration.getProductionConfig().setGeneratedSourcesDirectory(generatedSources.getText());
    configuration.getProductionConfig().setGeneratedClassesDirectory(generatedClasses.getText());
    configuration.getProductionConfig().setSourceLevel(JavaSourceLevel.fromRepresentation((String)sourceLevel.getSelectedItem()));
    configuration.getProductionConfig().setMaxMemorySize(maxMemorySize.getText());

    configuration.getTestConfig().setSourcesDirectory(testSources.getText());
    configuration.getTestConfig().setXsdConfigDirectory(testXsdConfig.getText());
    configuration.getTestConfig().setGeneratedSourcesDirectory(testGeneratedSources.getText());
    configuration.getTestConfig().setGeneratedClassesDirectory(testGeneratedClasses.getText());
    configuration.getTestConfig().setSourceLevel(JavaSourceLevel.fromRepresentation((String)testSourceLevel.getSelectedItem()));
    configuration.getTestConfig().setMaxMemorySize(testMaxMemorySize.getText());

    configuration.setTestConfigEnabled(enableTestConfiguration.isSelected());
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private void setUpValidation() {
    XmlBeansFacetConfiguration defaultConfig = new XmlBeansFacetConfiguration();
    manager.registerValidator(
      new ValidRelativePathValidator(sources, sourcesLabel, defaultConfig.getProductionConfig().getSourcesDirectory(), manager));
    manager.registerValidator(
      new ValidRelativePathValidator(xsdConfig, xsdConfigLabel, defaultConfig.getProductionConfig().getXsdConfigDirectory(), manager));
    manager.registerValidator(new ValidRelativePathValidator(generatedSources, generatedSourcesLabel,
                                                             defaultConfig.getProductionConfig().getGeneratedSourcesDirectory(), manager));
    manager.registerValidator(new ValidRelativePathValidator(generatedClasses, generatedClassesLabel,
                                                             defaultConfig.getProductionConfig().getGeneratedClassesDirectory(), manager));

    manager.registerValidator(
      new MandatoryFieldValidator(sources, sourcesLabel, defaultConfig.getProductionConfig().getSourcesDirectory(), manager));
    manager.registerValidator(new MandatoryFieldValidator(generatedSources, generatedSourcesLabel,
                                                          defaultConfig.getProductionConfig().getGeneratedSourcesDirectory(), manager));
    manager.registerValidator(new MandatoryFieldValidator(generatedClasses, generatedClassesLabel,
                                                          defaultConfig.getProductionConfig().getGeneratedClassesDirectory(), manager));

    manager.registerValidator(
      new ValidRelativePathValidator(testSources, testSourcesLabel, defaultConfig.getTestConfig().getSourcesDirectory(), manager));
    manager.registerValidator(
      new ValidRelativePathValidator(testXsdConfig, testXsdConfigLabel, defaultConfig.getTestConfig().getXsdConfigDirectory(), manager));
    manager.registerValidator(new ValidRelativePathValidator(testGeneratedSources, testGeneratedSourcesLabel,
                                                             defaultConfig.getTestConfig().getGeneratedSourcesDirectory(), manager));
    manager.registerValidator(new ValidRelativePathValidator(testGeneratedClasses, testGeneratedClassesLabel,
                                                             defaultConfig.getTestConfig().getGeneratedClassesDirectory(), manager));


    manager.registerValidator(
      new MandatoryFieldValidator(testSources, testSourcesLabel, defaultConfig.getTestConfig().getXsdConfigDirectory(), manager));
    manager.registerValidator(new MandatoryFieldValidator(testGeneratedSources, testGeneratedSourcesLabel,
                                                          defaultConfig.getTestConfig().getGeneratedSourcesDirectory(), manager));
    manager.registerValidator(new MandatoryFieldValidator(testGeneratedClasses, testGeneratedClassesLabel,
                                                          defaultConfig.getTestConfig().getGeneratedClassesDirectory(), manager));

    Runnable setDirty = new Runnable() {
      @Override
      public void run() {
        dirty = true;
      }
    };

    manager.registerValidator(new ExistingPathValidator(sources, module, manager, setDirty));
    manager.registerValidator(new ExistingPathValidator(xsdConfig, module, manager, setDirty));
    manager.registerValidator(new ExistingPathValidator(testSources, module, manager, setDirty));
    manager.registerValidator(new ExistingPathValidator(testXsdConfig, module, manager, setDirty));

    manager.registerValidator(
      new ValidJvmMemoryParameterValidator(maxMemorySize, maxMemorySizeLabel, defaultConfig.getProductionConfig().getMaxMemorySize(),
                                           manager));
    manager.registerValidator(
      new ValidJvmMemoryParameterValidator(testMaxMemorySize, testMaxMemorySizeLabel, defaultConfig.getTestConfig().getMaxMemorySize(),
                                           manager));
  }
}
