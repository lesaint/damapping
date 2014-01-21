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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DAType;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * DAConstructorWriter - Writer pour les constructeurs d'une classe
 *
 * TODO améliorations de DAConstructorWriter
 * <ul>
 *     <li>contrôle sur les modifiers : seulement public, private ou protected</li>
 *     <li>ajouter convenience method pour les invocations de super</li>
 *     <li>ajout de vérification d'état, pas possible d'appeller super si un statement a été créé</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAConstructorWriter<T extends DAWriter> extends AbstractDAWriter<T> {
    private final String name;
    private Set<Modifier> modifiers = Collections.<Modifier>emptySet();
    private List<DAParameter> params = Collections.<DAParameter>emptyList();

    public DAConstructorWriter(DAType constructedType, BufferedWriter bw, T parent, int indentOffset) {
        super(bw, parent, indentOffset);
        this.name = constructedType.getSimpleName().getName();
    }

    public DAConstructorWriter<T> withModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(modifiers);
        return this;
    }

    public DAConstructorWriter<T> withParams(List<DAParameter> params) {
        this.params = params == null ? Collections.<DAParameter>emptyList() : ImmutableList.copyOf(params);
        return this;
    }

    public DAConstructorWriter<T> start() throws IOException {
        commons.appendIndent();
        commons.appendModifiers(modifiers);
        commons.append(name);
        appendParams(params);
        commons.append(" {");
        commons.newLine();
        return this;
    }

    /**
     * Ajoute les parenthèses et les paramètres du constructeur, les paramètres étant représentés, dans l'ordre
     * par la liste de DAType en argument.
     */
    private void appendParams(List<DAParameter> params) throws IOException {
        if (params.isEmpty()) {
            commons.append("()");
            return;
        }

        commons.append("(");
        Iterator<DAParameter> it = params.iterator();
        while (it.hasNext()) {
            DAParameter parameter = it.next();
            commons.appendType(parameter.getType());
            commons.append(" ").append(parameter.getName());
            if (it.hasNext()) {
                commons.append(", ");
            }
        }
        commons.append(")");
    }

    public DAStatementWriter<DAConstructorWriter<T>> newStatement() {
        return new DAStatementWriter<DAConstructorWriter<T>>(commons.getBufferedWriter(), this, commons.getIndentOffset() + 1);
    }

    public T end() throws IOException {
        commons.appendIndent();
        commons.append("}");
        commons.newLine();
        commons.newLine();
        return parent;
    }
}
