package com.bsb.intellij.plugins.xmlbeans.utils;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author gja
 * @version $Revision: 2308 $ $Date: 2013-08-19 15:47:35 +0200 (lun., 19 août 2013) $
 */
public final class SourceFolderUtil {

  private SourceFolderUtil() {
    throw new UnsupportedOperationException();
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Public API

  public static void addSourceFolder(String relativePath, Module module) {
    addSourceFolderQuietly(relativePath, module, false);
  }

  public static void addTestSourceFolder(String relativePath, Module module) {
    addSourceFolderQuietly(relativePath, module, true);
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private static void addSourceFolderQuietly(String relativePath, Module module, boolean testSource) {
    try {
      addSourceFolder(relativePath, module, testSource);
    }
    catch (IOException e) {
      throw new IllegalStateException(
        "Could not add" + (testSource ? " test " : " ") + "source folder " + relativePath + " to module " + module.getName(), e);
    }
  }

  private static void addSourceFolder(String relativePath, Module module, boolean testSource) throws IOException {
    VirtualFile virtualFile = createDirectoryIfNeeded(module, relativePath);
    ModifiableRootModel rootModel = doGetRootModel(module);
    ContentEntry contentEntry = getContentRootFor(virtualFile, rootModel);
    if (contentEntry == null) {
      contentEntry = rootModel.addContentEntry(virtualFile);
    }
    if (!Arrays.asList(contentEntry.getSourceFolderFiles()).contains(virtualFile)) {
      contentEntry.addSourceFolder(virtualFile, testSource);
      doCommit(rootModel);
    }
  }

  private static VirtualFile createDirectoryIfNeeded(final Module module, final String relativePath) throws IOException {
    String directoryPath = PathMacroUtil.getModuleDir(module.getModuleFilePath());
    VirtualFile moduleDir = LocalFileSystem.getInstance().findFileByPath(directoryPath);
    return VfsUtil.createDirectoryIfMissing(moduleDir, relativePath);
  }

  private static void doCommit(final ModifiableRootModel rootModel) {
    new WriteAction<Void>() {
      @Override
      protected void run(Result<Void> result) {
        rootModel.commit();
      }
    }.execute();
  }

  private static ModifiableRootModel doGetRootModel(@NotNull final Module module) {
    return new ReadAction<ModifiableRootModel>() {
      protected void run(Result<ModifiableRootModel> result) {
        result.setResult(ModuleRootManager.getInstance(module).getModifiableModel());
      }
    }.execute().getResultObject();
  }

  private static ContentEntry getContentRootFor(VirtualFile url, ModifiableRootModel rootModel) {
    for (ContentEntry e : rootModel.getContentEntries()) {
      if (isEqualOrAncestor(e.getUrl(), url.getUrl())) {
        return e;
      }
    }
    return null;
  }

  private static boolean isEqualOrAncestor(String ancestor, String child) {
    return ancestor.equals(child) || StringUtil.startsWithConcatenationOf(child, ancestor, "/");
  }
}
