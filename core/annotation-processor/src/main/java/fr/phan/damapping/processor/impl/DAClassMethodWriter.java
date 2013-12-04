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
 * DAClassMethodWriter - Writer pour les méthodes d'une classe ou enum
 *
 * @author Sébastien Lesaint
 */
public class DAClassMethodWriter<T extends DAWriter> extends AbstractDAWriter<T> {
    private final String name;
    private final DAType returnType;
    private Set<Modifier> modifiers = Collections.<Modifier>emptySet();
    private List<DAType> annotations = Collections.emptyList();
    private List<DAParameter> params = Collections.<DAParameter>emptyList();

    public DAClassMethodWriter(String name, DAType returnType, BufferedWriter bw, int indent, T parent) {
        super(bw, parent, indent);
        this.name = name;
        this.returnType = returnType;
    }

    public DAClassMethodWriter<T> withModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(modifiers);
        return this;
    }

    public DAClassMethodWriter<T> withAnnotations(List<DAType> annotations) {
        this.annotations = annotations == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(annotations);
        return this;
    }

    public DAClassMethodWriter<T> withParams(List<DAParameter> params) {
        this.params = params == null ? Collections.<DAParameter>emptyList() : ImmutableList.copyOf(params);
        return this;
    }

    public DAClassMethodWriter<T> start() throws IOException {
        appendAnnotations(annotations);
        appendIndent();
        appendModifiers(bw, modifiers);
        appendReturnType();
        bw.append(name);
        appendParams(bw, params);
        bw.append(" {");
        bw.newLine();
        return this;
    }

    private void appendReturnType() throws IOException {
        appendType(bw, returnType);
        bw.append(" ");
    }

    /**
     * Ajoute les parenthèses et les paramètres d'une méthode, les paramètres étant représentés, dans l'ordre
     * par la liste de DAType en argument.
     */
    private void appendParams(BufferedWriter bw, List<DAParameter> params) throws IOException {
        if (params.isEmpty()) {
            bw.append("()");
            return;
        }

        bw.append("(");
        Iterator<DAParameter> it = params.iterator();
        while (it.hasNext()) {
            DAParameter parameter = it.next();
            appendType(bw, parameter.getType());
            bw.append(" ").append(parameter.getName());
            if (it.hasNext()) {
                bw.append(", ");
            }
        }
        bw.append(")");
    }

    public DAStatementWriter<DAClassMethodWriter<T>> newStatement() {
        return new DAStatementWriter<DAClassMethodWriter<T>>(bw, this, indent + 1);
    }

    public T end() throws IOException {
        appendIndent();
        bw.append("}");
        bw.newLine();
        bw.newLine();
        return parent;
    }
}
