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

import java.util.Arrays;

public enum JavaSourceLevel {

  FOUR(Constants.JAVA_FOUR_REPRESENTATION), FIVE(Constants.JAVA_FIVE_REPRESENTATION);

  private final String representation;

  JavaSourceLevel(String representation) {
    this.representation = representation;
  }

  public String getRepresentation() {
    return representation;
  }

  public static JavaSourceLevel fromRepresentation(String representation) {
    if (Constants.JAVA_FOUR_REPRESENTATION.equals(representation)) {
      return FOUR;
    }
    if (Constants.JAVA_FIVE_REPRESENTATION.equals(representation)) {
      return FIVE;
    }
    throw new IllegalArgumentException("Argument 'representation' should be one of " + Arrays.toString(Constants.JAVA_SOURCE_LEVELS));
  }

  private static class Constants {
    public static final String JAVA_FOUR_REPRESENTATION = "1.4";
    public static final String JAVA_FIVE_REPRESENTATION = "1.5";
    public static final String[] JAVA_SOURCE_LEVELS = new String[]{JAVA_FOUR_REPRESENTATION, JAVA_FIVE_REPRESENTATION};
  }
}