package com.bsb.intellij.plugins.xmlbeans.utils;

import com.intellij.openapi.module.Module;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class XmlBeansConfiguration implements Serializable, Cloneable {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private fields

  private String sourcesDirectory = "";
  private String xsdConfigDirectory = "";
  private String generatedSourcesDirectory = "";
  private String generatedClassesDirectory = "";
  private JavaSourceLevel sourceLevel = JavaSourceLevel.FOUR;
  private String maxMemorySize = "";
  private Boolean production;

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructors

  public XmlBeansConfiguration() {
    super();
    // DO NOT REMOVE - used by IntelliiJ for XML marshalling/unmarshalling
  }

  public XmlBeansConfiguration(String sourcesDirectory,
                               String xsdConfigDirectory,
                               String generatedSourcesDirectory,
                               String generatedClassesDirectory,
                               JavaSourceLevel sourceLevel,
                               String maxMemorySize,
                               Boolean production) {
    this.sourcesDirectory = sourcesDirectory;
    this.xsdConfigDirectory = xsdConfigDirectory;
    this.generatedSourcesDirectory = generatedSourcesDirectory;
    this.generatedClassesDirectory = generatedClassesDirectory;
    this.sourceLevel = sourceLevel;
    this.maxMemorySize = maxMemorySize;
    this.production = production;
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Check that configuration is valid

  public boolean isValid(Module module) {
    if (StringUtils.isBlank(this.getSourcesDirectory())) return false;
    if (StringUtils.isBlank(this.getGeneratedSourcesDirectory())) return false;
    if (StringUtils.isBlank(this.getGeneratedClassesDirectory())) return false;
    if (!ValidationUtils.isValidRelativePath(this.getSourcesDirectory())) return false;
    if (StringUtils.isNotBlank(this.getXsdConfigDirectory()) && !ValidationUtils.isValidRelativePath(this.getXsdConfigDirectory())) {
      return false;
    }
    if (!ValidationUtils.isValidRelativePath(this.getGeneratedSourcesDirectory())) return false;
    if (!ValidationUtils.isValidRelativePath(this.getGeneratedClassesDirectory())) return false;
    if (!ValidationUtils.isPathExists(this.getSourcesDirectory(), module)) return false;
    if (sourceLevel == null) return false;
    if (!ValidationUtils.isValidJvmMemoryParameter(maxMemorySize)) return false;
    return true;
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Getter & setters

  public String getSourcesDirectory() {
    return sourcesDirectory;
  }

  public void setSourcesDirectory(String sourcesDirectory) {
    this.sourcesDirectory = sourcesDirectory;
  }

  public String getXsdConfigDirectory() {
    return xsdConfigDirectory;
  }

  public void setXsdConfigDirectory(String xsdConfigDirectory) {
    this.xsdConfigDirectory = xsdConfigDirectory;
  }

  public String getGeneratedSourcesDirectory() {
    return generatedSourcesDirectory;
  }

  public void setGeneratedSourcesDirectory(String generatedSourcesDirectory) {
    this.generatedSourcesDirectory = generatedSourcesDirectory;
  }

  public String getGeneratedClassesDirectory() {
    return generatedClassesDirectory;
  }

  public void setGeneratedClassesDirectory(String generatedClassesDirectory) {
    this.generatedClassesDirectory = generatedClassesDirectory;
  }

  public JavaSourceLevel getSourceLevel() {
    return sourceLevel;
  }

  public void setSourceLevel(JavaSourceLevel sourceLevel) {
    this.sourceLevel = sourceLevel;
  }

  public String getMaxMemorySize() {
    return maxMemorySize;
  }

  public void setMaxMemorySize(String maxMemorySize) {
    this.maxMemorySize = maxMemorySize;
  }

  public Boolean getProduction() {
    return production;
  }

  public void setProduction(Boolean production) {
    this.production = production;
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Generated toString, equals and hashCode

  @Override
  public String toString() {
    return "XmlBeansConfiguration{" +
           "sourcesDirectory='" + sourcesDirectory + '\'' +
           ", xsdConfigDirectory='" + xsdConfigDirectory + '\'' +
           ", generatedSourcesDirectory='" + generatedSourcesDirectory + '\'' +
           ", generatedClassesDirectory='" + generatedClassesDirectory + '\'' +
           ", sourceLevel=" + sourceLevel +
           ", maxMemorySize=" + maxMemorySize +
           '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    XmlBeansConfiguration that = (XmlBeansConfiguration)o;

    if (!generatedClassesDirectory.equals(that.generatedClassesDirectory)) return false;
    if (!generatedSourcesDirectory.equals(that.generatedSourcesDirectory)) return false;
    if (maxMemorySize != null ? !maxMemorySize.equals(that.maxMemorySize) : that.maxMemorySize != null) return false;
    if (sourceLevel != that.sourceLevel) return false;
    if (!sourcesDirectory.equals(that.sourcesDirectory)) return false;
    if (xsdConfigDirectory != null ? !xsdConfigDirectory.equals(that.xsdConfigDirectory) : that.xsdConfigDirectory != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = sourcesDirectory.hashCode();
    result = 31 * result + (xsdConfigDirectory != null ? xsdConfigDirectory.hashCode() : 0);
    result = 31 * result + generatedSourcesDirectory.hashCode();
    result = 31 * result + generatedClassesDirectory.hashCode();
    result = 31 * result + sourceLevel.hashCode();
    result = 31 * result + (maxMemorySize != null ? maxMemorySize.hashCode() : 0);
    return result;
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Cloneable implementation

  public XmlBeansConfiguration clone() throws CloneNotSupportedException {
    return (XmlBeansConfiguration)super.clone();
  }

  public XmlBeansConfiguration cloneQuietly() {
    try {
      return clone();
    }
    catch (CloneNotSupportedException e) {
      throw new IllegalStateException("Should not happen", e);
    }
  }
}
