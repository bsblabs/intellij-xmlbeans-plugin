/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bsb.intellij.plugins.xmlbeans.compiler;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.util.Key;
import com.intellij.util.concurrency.Semaphore;

/**
 * @author gja
 * @version $Revision: 2308 $ $Date: 2013-08-19 15:47:35 +0200 (lun., 19 août 2013) $
 */
public class XmlBeansCompilerProcessListener extends ProcessAdapter {

  private final Semaphore targetDone;
  private final XmlBeansCompilerOutputParser parser;
  private boolean finished = false;
  private boolean success;

  public XmlBeansCompilerProcessListener(Semaphore targetDone, CompileContext context) {
    this.targetDone = targetDone;
    this.parser = new XmlBeansCompilerOutputParser(context);
  }

  @Override
  public void onTextAvailable(ProcessEvent event, Key outputType) {
    super.onTextAvailable(event, outputType);
    parser.parse(event.getText());
  }

  @Override
  public void processTerminated(ProcessEvent event) {
    finished = true;
    success = (event.getExitCode() == 0);
    targetDone.up();
  }

  public boolean isSuccess() {
    if (!finished) {
      throw new IllegalStateException("Process not finished yet!");
    }
    return success;
  }
}

