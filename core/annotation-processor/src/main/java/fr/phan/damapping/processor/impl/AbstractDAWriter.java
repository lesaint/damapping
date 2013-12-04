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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

/**
 * AbstractDAWriter -
 *
 * @author Sébastien Lesaint
 */
public class AbstractDAWriter<T extends DAWriter> implements DAWriter {
    protected static final String INDENT = "    ";

    protected final BufferedWriter bw;
    protected final T parent;
    protected final int indent;

    AbstractDAWriter(BufferedWriter bw, T parent, int indent) {
        this.bw = bw;
        this.indent = indent;
        this.parent = parent;
    }

    void appendIndent() throws IOException {
        for (int i = 0; i < indent; i++) {
            bw.append(INDENT);
        }
    }

    void appendModifiers(BufferedWriter bw, Set<Modifier> modifiers) throws IOException {
        // TODO add sorting of Modifiers according to best practice
        if (modifiers.isEmpty()) {
            return;
        }
        Iterator<Modifier> it = modifiers.iterator();
        while (it.hasNext()) {
            bw.append(it.next().toString()).append(" ");
        }
    }

    void appendAnnotations(Collection<DAType> annotations) throws IOException {
        if (annotations.isEmpty()) {
            return;
        }

        for (DAType annotation : annotations) {
            appendIndent();
            bw.append("@").append(annotation.getSimpleName());
            bw.newLine();
        }
    }

    void appendType(BufferedWriter bw, DAType type) throws IOException {
        bw.append(type.getSimpleName());
        if (type.getExtendsBound() != null) {
            bw.append(" extends ");
            appendType(bw, type.getExtendsBound());
        }
        else if (type.getSuperBound() != null) {
            bw.append(" super ");
            appendType(bw, type.getExtendsBound());
        }
        appendTypeArgs(bw, type.getTypeArgs());
        if (type.isArray()) {
            bw.append("[]");
        }
    }

    void appendTypeArgs(BufferedWriter bw, List<DAType> typeArgs) throws IOException {
        if (!typeArgs.isEmpty()) {
            Iterator<DAType> iterator = typeArgs.iterator();
            bw.append("<");
            while (iterator.hasNext()) {
                DAType arg = iterator.next();
                appendType(bw, arg);
                if (iterator.hasNext()) {
                    bw.append(", ");
                }
            }
            bw.append(">");
        }
    }
}
