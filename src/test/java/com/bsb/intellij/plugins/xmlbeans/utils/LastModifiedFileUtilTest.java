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

import com.bsb.intellij.plugins.xmlbeans.utils.file.FileUtils;
import com.bsb.intellij.plugins.xmlbeans.utils.file.FileVisitor;
import com.intellij.openapi.util.io.FileUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author GJA
 * @version $Revision: 2557 $ $Date: 2014-02-25 16:59:22 +0100 (mar., 25 févr. 2014) $
 */
public class LastModifiedFileUtilTest {

  private File root;
  private File a;
  private File b;
  private File c;
  private File d;
  private File e;

  @Before
  public void setUp() throws Exception {
    File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    root = new File(tempDirectory, "root");
    createDirectoryAndWait(root);
  }

  @Test
  public void testGetLastModified1() throws Exception {
    createTreeWithELast();
    File lastModified = FileUtils.getLastModified(root);
    Assert.assertTrue(FileUtil.filesEqual(e, lastModified));
    Assert.assertFalse(FileUtil.filesEqual(b, lastModified));
  }

  @Test
  public void testGetLastModified2() throws Exception {
    createTreeWithBLast();
    File lastModified = FileUtils.getLastModified(root);
    Assert.assertTrue(FileUtil.filesEqual(b, lastModified));
    Assert.assertFalse(FileUtil.filesEqual(e, lastModified));
  }

  @Test
  public void testAddFile() throws Exception {
    createTreeWithBLast();
    File f = new File(d, "f");
    createFileAndWait(f);
    File lastModified = FileUtils.getLastModified(root);
    Assert.assertTrue(FileUtil.filesEqual(f, lastModified));
    Assert.assertEquals(f.lastModified(), f.getParentFile().lastModified());
    Assert.assertFalse(FileUtil.filesEqual(b, lastModified));
  }

  @Test
  public void testRemoveFile() throws Exception {
    createTreeWithBLast();
    if (!c.delete()) {
      throw new IllegalStateException("Could not delete file" + c.getAbsolutePath());
    }
    File lastModified = FileUtils.getLastModified(root);
    Assert.assertTrue(FileUtil.filesEqual(a, lastModified));
    Assert.assertFalse(FileUtil.filesEqual(b, lastModified));
  }

  @Test
  public void testUpdateFile() throws Exception {
    createTreeWithBLast();
    BufferedWriter writer = new BufferedWriter(new FileWriter(c, true));
    try {
      writer.write("This is an update of the file");
      writer.flush();
    }
    finally {
      writer.close();
      File lastModified = FileUtils.getLastModified(root);
      Assert.assertTrue(FileUtil.filesEqual(c, lastModified));
      Assert.assertFalse(FileUtil.filesEqual(b, lastModified));
    }
  }

  @After
  public void tearDown() throws Exception {
    // recursively delete tree rooted at root
    FileUtils.traverse(root, new FileVisitor() {
      @Override
      public void visit(File file) {
        if (file.exists()) {
          if (!file.delete()) {
            throw new IllegalStateException("Could not delete file " + file.getAbsolutePath());
          }
        }
      }
    });
  }

  /**
   * root(1)
   * /  \
   * a(2) b(3)
   * / | \
   * /  |  \
   * c(4) d(5) e(6)
   */
  private void createTreeWithELast() throws IOException, InterruptedException {
    a = new File(root, "a");
    createDirectoryAndWait(a);
    b = new File(root, "b");
    createDirectoryAndWait(b);
    c = new File(a, "c");
    createFileAndWait(c);
    d = new File(a, "d");
    createDirectoryAndWait(d);
    e = new File(a, "e");
    createFileAndWait(e);
  }

  /**
   * root(1)
   * /  \
   * a(2) b(6)
   * / | \
   * /  |  \
   * c(3) d(4) e(5)
   */
  private void createTreeWithBLast() throws IOException, InterruptedException {
    a = new File(root, "a");
    createDirectoryAndWait(a);
    c = new File(a, "c");
    createFileAndWait(c);
    d = new File(a, "d");
    createDirectoryAndWait(d);
    e = new File(a, "e");
    createFileAndWait(e);
    b = new File(root, "b");
    createDirectoryAndWait(b);
  }

  private static void createFileAndWait(File file) throws InterruptedException, IOException {
    if (!file.createNewFile()) {
      throw new IllegalStateException("Could not create file : " + file.getAbsolutePath());
    }
    Thread.sleep(100);
  }

  private static void createDirectoryAndWait(File directory) throws InterruptedException, IOException {
    if (!directory.mkdir()) {
      throw new IllegalStateException("Could not create file : " + directory.getAbsolutePath());
    }
    Thread.sleep(100);
  }
}
