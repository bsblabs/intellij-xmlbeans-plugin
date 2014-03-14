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

import java.io.File;

/**
 * @author GJA
 * @version $Revision: 2553 $ $Date: 2014-02-19 13:55:08 +0100 (mer., 19 févr. 2014) $
 */
public interface FileVisitor {

  void visit(File file);
}
