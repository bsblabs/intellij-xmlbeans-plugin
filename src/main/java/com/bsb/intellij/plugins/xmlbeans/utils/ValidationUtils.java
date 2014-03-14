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

import com.intellij.openapi.module.Module;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import java.io.File;

/**
 * @author gja
 * @version $Revision: 2247 $ $Date: 2013-07-05 14:22:20 +0200 (ven., 05 juil. 2013) $
 */
public final class ValidationUtils {

  private ValidationUtils() {
    throw new UnsupportedOperationException();
  }

  public static final String RELATIVE_PATH_REGEX = "^(?!-)[a-z0-9-]+(?<!-)((/|\\\\)(?!-)[a-z0-9-]+(?<!-))*((/|\\\\)(?!-\\.)[a-z0-9-\\.]+(?<!-\\.))?$";
  public static final String JVM_MEMORY_PARAM_REGEX = "^\\d+(k|K|m|M|g|G)$";

  public static boolean isValidRelativePath(String path) {
    return StringUtils.isNotBlank(path) && path.matches(RELATIVE_PATH_REGEX);
  }

  public static boolean isPathExists(String relativePath, Module module) {
    String moduleDir = PathMacroUtil.getModuleDir(module.getModuleFilePath());
    return StringUtils.isNotBlank(relativePath) && new File(moduleDir, relativePath).exists();
  }

  public static boolean isValidJvmMemoryParameter(String jvmMemoryParameter) {
    return StringUtils.isNotBlank(jvmMemoryParameter) && jvmMemoryParameter.matches(JVM_MEMORY_PARAM_REGEX);
  }
}
