/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.phan.damapping.processor.sourcegenerator.writer;

import java.io.BufferedWriter;

/**
 * AbstractDAWriter -
 *
 * @author Sébastien Lesaint
 */
public class AbstractDAWriter<T extends DAWriter> implements DAWriter {
  protected final CommonMethods commons;
  protected final T parent;

  AbstractDAWriter(BufferedWriter bw, T parent, int indentOffset) {
    this(new CommonMethodsImpl(bw, indentOffset), parent);
  }

  AbstractDAWriter(CommonMethods commonMethods, T parent) {
    this.commons = commonMethods;
    this.parent = parent;
  }

}
