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
import fr.javatronic.damapping.util.Lists;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

/**
 * DAPropertyWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAPropertyWriter<T extends DAWriter> extends AbstractDAWriter<T> {
  private final String name;
  private final DAType type;
  private List<DAAnnotation> annotations = Collections.emptyList();
  @Nullable
  private DAModifier[] modifiers;

  DAPropertyWriter(String name, DAType type, FileContext fileContext, T parent, int indentOffset) {
    super(fileContext, parent, indentOffset);
    this.name = name;
    this.type = type;
  }

  public DAPropertyWriter<T> withAnnotations(@Nullable List<DAAnnotation> annotations) {
    this.annotations = annotations == null ? Collections.<DAAnnotation>emptyList() : Lists.copyOf(annotations);
    return this;
  }

  public DAPropertyWriter<T> withModifiers(@Nullable DAModifier... modifiers) {
    this.modifiers = modifiers;
    return this;
  }

  public T write() throws IOException {
    commons.appendAnnotations(annotations);
    commons.appendIndent();
    commons.appendModifiers(modifiers);
    commons.appendType(type);
    commons.append(" ").append(name).append(";");
    commons.newLine();
    return parent;
  }
}
