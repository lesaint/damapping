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
 * DAInterfaceWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAInterfaceWriter<T extends DAWriter> extends AbstractDAWriter<T> {
    private final String name;
    private List<DAType> annotations = Collections.emptyList();
    private Set<Modifier> modifiers = Collections.emptySet();
    private List<DAType> extended = Collections.emptyList();

    DAInterfaceWriter(String name, BufferedWriter bw, T parent, int indent) {
        super(bw, parent, indent);
        this.name = name;
    }

    DAInterfaceWriter<T> withAnnotations(List<DAType> annotations) {
        this.annotations = annotations == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(annotations);
        return this;
    }

    DAInterfaceWriter<T> withModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(modifiers);
        return this;
    }

    DAInterfaceWriter<T> withExtended(List<DAType> extended) {
        this.extended = extended == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(extended);
        return this;
    }

    DAInterfaceWriter<T> start() throws IOException {
        appendAnnotations(annotations);
        appendIndent();
        appendModifiers(bw, modifiers);
        bw.append("interface ").append(name).append(" ");
        appendExtended();
        bw.append("{");
        bw.newLine();
        bw.newLine();
        return this;
    }

    private void appendExtended() throws IOException {
        if (extended.isEmpty()) {
            return;
        }

        bw.append("extends ");
        Iterator<DAType> it = extended.iterator();
        while (it.hasNext()) {
            appendType(bw, it.next());
            if (it.hasNext()) {
                bw.append(",");
            }
            bw.append(" ");
        }
    }

    DAInterfaceMethodWriter<DAInterfaceWriter<T>> newMethod(String name, DAType returnType) {
        return new DAInterfaceMethodWriter<DAInterfaceWriter<T>>(name, returnType, bw, indent + 1, this);
    }

    T end() throws IOException {
        appendIndent();
        bw.append("}");
        bw.newLine();
        return parent;
    }
}