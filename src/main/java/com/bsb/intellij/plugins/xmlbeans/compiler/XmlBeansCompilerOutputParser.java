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
package com.bsb.intellij.plugins.xmlbeans.compiler;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.pom.Navigatable;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gja
 * @version $Revision: 2247 $ $Date: 2013-07-05 14:22:20 +0200 (ven., 05 juil. 2013) $
 */
public class XmlBeansCompilerOutputParser {

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constants

  // Pattern : file_path:line:column: error|warning: message
  public static final String OUTPUT_MESSAGE_REGEX = "(^.+):(\\d+):(\\d+):\\s(error|warning):\\s(.+)$";
  public static final Pattern OUTPUT_MESSAGE_PATTERN = Pattern.compile(OUTPUT_MESSAGE_REGEX);
  public static final String ERROR_TOKEN = "error";
  public static final String FILE_URL_PREFIX = "file://";

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private fields

  private final CompileContext context;

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Constructor

  public XmlBeansCompilerOutputParser(CompileContext context) {
    this.context = context;
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Public API

  public void parse(String output) {
    if (StringUtils.isNotBlank(output)) {
      doParse(output.trim());
    }
  }

  // ------------------------------------------------------------------------------------------------------------------------------------
  // Private implementation

  private void doParse(String output) {
    Matcher matcher = OUTPUT_MESSAGE_PATTERN.matcher(output);
    if (matcher.matches()) {
      MessageDescriptor descriptor = new MessageDescriptor(matcher, context.getProject());
      context
        .addMessage(descriptor.category, descriptor.message, descriptor.url, descriptor.line, descriptor.column, descriptor.navigatable);
    }
  }

  private static final class MessageDescriptor {

    private final CompilerMessageCategory category;
    private final String message;
    private final String url;
    private final int line;
    private final int column;
    private final Navigatable navigatable;

    private MessageDescriptor(Matcher matcher, Project project) {
      category = ERROR_TOKEN.equals(matcher.group(4)) ? CompilerMessageCategory.ERROR : CompilerMessageCategory.WARNING;
      message = matcher.group(5);
      url = FILE_URL_PREFIX + matcher.group(1);
      line = Integer.parseInt(matcher.group(2)) - 1;
      column = Integer.parseInt(matcher.group(3)) - 1;
      navigatable = getNavigatable(url, line, column, project);
    }

    private Navigatable getNavigatable(String url, int line, int column, Project project) {
      VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
      if (file != null && line > 0 && column > 0) {
        return new OpenFileDescriptor(project, file, line, column);
      }
      return null;
    }
  }
}
