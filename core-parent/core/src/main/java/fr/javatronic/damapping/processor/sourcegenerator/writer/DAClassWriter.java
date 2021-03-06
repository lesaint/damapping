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
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

/**
 * DAClassWriter -
 * <p>
 * améliorations à réaliser de DAClassWriter
 * </p>
 * <ul>
 * <li>ajouter un contrôle sur les modifiers autorisés pour une classe</li>
 * <li>ajouter un paramètre "boolean classOfJavaSource" pour refuser le modifier PUBLIC si ce paramètre est false</li>
 * <li>ajouter une vérification d'état, si write() a été appelé, withModifiers, withAnnotations, withImplemented,
 * withExtented échouent</li>
 * <li>ajouter le tri automatique des modifiers</li>
 * <li>ajouter vérification d'état : end() must be called after write()</li>
 * <li>ajouter vérification d'état : plus de call sur aucune méthode si end() a été appelé</li>
 * <li>ajouter vérification des paramètres de withModifiers, withAnnotations, withImplemented, withExtented</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAClassWriter<T extends DAWriter> extends AbstractDAWriter<T> {
  protected final DAType classType;
  private final String name;
  @Nullable
  private DAModifier[] modifiers;
  private List<DAAnnotation> annotations = Collections.emptyList();
  private List<DAType> implemented = Collections.emptyList();
  private DAType extended;

  DAClassWriter(DAType classType, FileContext fileContext, T parent, int indentOffset) {
    super(fileContext, parent, indentOffset);
    this.name = classType.getSimpleName().getName();
    this.classType = classType;
  }

  public DAClassWriter<T> withModifiers(@Nullable DAModifier... modifiers) {
    this.modifiers = modifiers;
    return this;
  }

  public DAClassWriter<T> withAnnotations(List<DAAnnotation> annotations) {
    this.annotations = annotations == null ? Collections.<DAAnnotation>emptyList() : Lists.copyOf(annotations);
    return this;
  }

  public DAClassWriter<T> withImplemented(List<DAType> implemented) {
    this.implemented = implemented == null ? Collections.<DAType>emptyList() : Lists.copyOf(implemented);
    return this;
  }

  public DAClassWriter<T> withExtended(DAType extended) {
    this.extended = extended;
    return this;
  }

  public DAClassWriter<T>start() throws IOException {
    commons.appendAnnotations(annotations);
    commons.appendIndent();
    commons.appendModifiers(modifiers);
    commons.append("class ").append(name).append(" ");
    appendExtended();
    appendImplemented();
    commons.append("{");
    commons.newLine();
    return this;
  }

  public DAPropertyWriter<DAClassWriter<T>> newProperty(String name, DAType type) {
    return new DAPropertyWriter<DAClassWriter<T>>(name, type, commons.getFileContext(), this,
        commons.getIndentOffset() + 1
    );
  }

  public DAInitializedPropertyWriter<DAClassWriter<T>> newInitializedProperty(String name, DAType type) {
    return new DAInitializedPropertyWriter<DAClassWriter<T>>(name, type, commons.getFileContext(), this,
        commons.getIndentOffset() + 1
    );
  }

  public DAConstructorWriter<DAClassWriter<T>> newConstructor() throws IOException {
    commons.newLine();
    return new DAConstructorWriter<DAClassWriter<T>>(classType, commons.getFileContext(), this,
        commons.getIndentOffset() + 1
    );
  }

  public DAClassMethodWriter<DAClassWriter<T>> newMethod(String name, DAType returnType) throws IOException {
    commons.newLine();
    return new DAClassMethodWriter<DAClassWriter<T>>(name, returnType, commons.getFileContext(),
        commons.getIndentOffset() + 1, this
    );
  }

  public T end() throws IOException {
    commons.appendIndent();
    commons.append("}");
    commons.newLine();
    return parent;
  }

  public DAClassWriter<DAClassWriter<T>> newClass(DAType classType) throws IOException {
    commons.newLine();
    return new DAClassWriter<DAClassWriter<T>>(classType, commons.getFileContext(), this,
        commons.getIndentOffset() + 1
    );
  }

  private void appendExtended() throws IOException {
    if (extended == null) {
      return;
    }

    commons.append("extends ");
    commons.appendType(extended);
    commons.append(" ");
  }

  private void appendImplemented() throws IOException {
    if (implemented.isEmpty()) {
      return;
    }

    commons.append("implements ");
    Iterator<DAType> it = implemented.iterator();
    while (it.hasNext()) {
      commons.appendType(it.next());
      if (it.hasNext()) {
        commons.append(",");
      }
      commons.append(" ");
    }
  }
}
