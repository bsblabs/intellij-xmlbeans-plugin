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

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author gja
 * @version $Revision: 2309 $ $Date: 2013-08-19 16:01:42 +0200 (lun., 19 août 2013) $
 */
public final class CompilerUtils {

  private CompilerUtils() {
    throw new UnsupportedOperationException();
  }

  private static final Logger logger = Logger.getInstance(CompilerUtils.class);

  public static boolean isAffected(Module module, CompileContext context) {
    for (Module compiledModule : context.getCompileScope().getAffectedModules()) {
      if (compiledModule.getName().equals(module.getName())) {
        return true;
      }
    }
    return false;
  }

  @Nullable
  public static VirtualFile getCompilerOutputFileAndCreateItIfNeeded(Module module, boolean test) {
    try {
      return getCompilerOutputPathAndCreateItIfNeededQuietly(module, test);
    }
    catch (IOException e) {
      logger.warn("Could not get" + (test ? " test " : " ") + "output path for module " + module.getName());
      return null;
    }
  }

  @Nullable
  public static VirtualFile getCompilerOutputPathAndCreateItIfNeededQuietly(Module module, boolean test) throws IOException {
    CompilerModuleExtension instance = CompilerModuleExtension.getInstance(module);
    if (instance != null) {
      VirtualFile compilerOutputPath = test ? instance.getCompilerOutputPathForTests() : instance.getCompilerOutputPath();
      if (compilerOutputPath == null) {
        String compilerOutputPathUrl = test ? instance.getCompilerOutputUrlForTests() : instance.getCompilerOutputUrl();
        if (StringUtils.isNotBlank(compilerOutputPathUrl)) {
          assert compilerOutputPathUrl != null;
          compilerOutputPath = VfsUtil.createDirectories(compilerOutputPathUrl.replace("file://", ""));
        }
      }
      return compilerOutputPath;
    }
    return null;
  }
}
