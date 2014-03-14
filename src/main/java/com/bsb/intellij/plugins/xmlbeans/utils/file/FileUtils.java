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
package com.bsb.intellij.plugins.xmlbeans.utils.file;

import com.intellij.openapi.module.Module;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gja
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public final class FileUtils {

  private FileUtils() {
    throw new UnsupportedOperationException();
  }

  public static File getModuleFile(Module module, String relativePath) {
    return new File(PathMacroUtil.getModuleDir(module.getModuleFilePath()), relativePath);
  }

  public static List<File> getChildren(File parent) {
    File[] files = parent.listFiles();
    return files != null ? Arrays.asList(files) : new ArrayList<File>();
  }

  public static long getLastModifiedTimeStamp(List<File> roots) {
    long lastModifiedTimeStamp = -1l;
    for (File root : roots) {
      long timeStamp = getLastModifiedTimeStamp(root);
      if (timeStamp > lastModifiedTimeStamp) {
        lastModifiedTimeStamp = timeStamp;
      }
    }
    return lastModifiedTimeStamp;
  }

  public static long getLastModifiedTimeStamp(File root) {
    return getLastModified(root).lastModified();
  }

  public static File getLastModified(File root) {
    LastModifiedFileVisitor visitor = new LastModifiedFileVisitor();
    traverse(root, visitor);
    return visitor.getLastModified();
  }

  /**
   * Traverse file system tree rooted at root
   * Children are visited first
   *
   * @param root
   * @param visitor
   */
  public static void traverse(File root, FileVisitor visitor) {
    visitChildren(root, visitor);
    visitor.visit(root);
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private static void visitChildren(File root, FileVisitor visitor) {
    for (File child : getChildren(root)) {
      traverse(child, visitor);
    }
  }
}
