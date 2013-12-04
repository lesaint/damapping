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
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * DAClassWriter -
 *
 * améliorations à réaliser de DAClassWriter
 * <ul>
 *     <li>ajouter un contrôle sur les modifiers autorisés pour une classe</li>
 *     <li>ajouter un paramètre "boolean classOfJavaSource" pour refuser le modifier PUBLIC si ce paramètre est false</li>
 *     <li>ajouter une vérification d'état, si start() a été appelé, withModifiers, withAnnotations, withImplemented, withExtented échouent</li>
 *     <li>ajouter le tri automatique des modifiers</li>
 *     <li>ajouter vérification d'état : end() must be called after start()</li>
 *     <li>ajouter vérification d'état : plus de call sur aucune méthode si end() a été appelé</li>
 *     <li>ajouter vérification des paramètres de withModifiers, withAnnotations, withImplemented, withExtented</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAClassWriter<T extends DAWriter> extends AbstractDAWriter<T> {
    private final String name;
    private final DAType classType;
    private Set<Modifier> modifiers = Collections.emptySet();
    private List<DAType> annotations = Collections.emptyList();
    private List<DAType> implemented = Collections.emptyList();
    private DAType extended;

    DAClassWriter(DAType classType, BufferedWriter bw, T parent, int indent) {
        super(bw, parent, indent);
        this.name = classType.getSimpleName().getName();
        this.classType = classType;
    }

    DAClassWriter<T> withModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(modifiers);
        return this;
    }

    DAClassWriter<T> withAnnotations(List<DAType> annotations) {
        this.annotations = annotations == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(annotations);
        return this;
    }

    DAClassWriter<T> withImplemented(List<DAType> implemented) {
        this.implemented = implemented == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(implemented);
        return this;
    }

    DAClassWriter<T> withExtended(DAType extended) {
        this.extended = extended;
        return this;
    }

    DAClassWriter<T> start() throws IOException {
        appendAnnotations(annotations);
        appendIndent();
        appendModifiers(bw, modifiers);
        bw.append("class ").append(name).append(" ");
        appendExtended();
        appendImplemented();
        bw.append("{");
        bw.newLine();
        bw.newLine();
        return this;
    }

    DAPropertyWriter<DAClassWriter<T>> newProperty(String name, DAType type) {
        return new DAPropertyWriter<DAClassWriter<T>>(name, type, bw, this, indent + 1);
    }

    DAConstructorWriter<DAClassWriter<T>> newConstructor() {
        return new DAConstructorWriter<DAClassWriter<T>>(classType, bw, this, indent + 1);
    }

    DAClassMethodWriter<DAClassWriter<T>> newMethod(String name, DAType returnType) {
        return new DAClassMethodWriter<DAClassWriter<T>>(name, returnType, bw, indent + 1, this);
    }

    T end() throws IOException {
        appendIndent();
        bw.append("}");
        bw.newLine();
        return parent;
    }

    DAClassWriter<DAClassWriter<T>> newClass(DAType classType) {
        return new DAClassWriter<DAClassWriter<T>>(classType, bw, this, indent + 1);
    }

    private void appendExtended() throws IOException {
        if (extended == null) {
            return;
        }

        bw.append("extends ");
        appendType(bw, extended);
        bw.append(" ");
    }

    private void appendImplemented() throws IOException {
        if (implemented.isEmpty()) {
            return;
        }

        bw.append("implements ");
        Iterator<DAType> it = implemented.iterator();
        while (it.hasNext()) {
            appendType(bw, it.next());
            if (it.hasNext()) {
                bw.append(",");
            }
            bw.append(" ");
        }
    }
}
