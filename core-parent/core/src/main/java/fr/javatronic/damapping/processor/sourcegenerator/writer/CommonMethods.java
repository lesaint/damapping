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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * CommonMethods -
 *
 * @author Sébastien Lesaint
 */
interface CommonMethods extends Appendable, Closeable, Flushable {
  BufferedWriter getBufferedWriter();

  /**
   * The indent offset.
   * Starts with 0.
   *
   * @return a int
   */
  int getIndentOffset();

  void appendIndent() throws IOException;

  void appendModifiers(Set<DAModifier> modifiers) throws IOException;

  void appendAnnotations(Collection<DAAnnotation> annotations) throws IOException;

  void appendInlineAnnotations(Collection<DAAnnotation> annotations) throws IOException;

  void appendType(DAType type) throws IOException;

  void appendTypeArgs(List<DAType> typeArgs) throws IOException;

  void newLine() throws IOException;
}
