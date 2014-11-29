/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.test;

import fr.javatronic.damapping.annotation.Mapper;

/**
 * PrimitiveSupport -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class PrimitiveSupport {
  private final boolean flag;
  private final int[] intArray;

  public PrimitiveSupport(boolean flag, int[] intArray) {
    this.flag = flag;
    this.intArray = intArray;
  }

  public float[] map(double[] doubles) {
    return null; // content does not matter
  }
}
