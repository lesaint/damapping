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
import fr.phan.damapping.processor.model.DAType;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * DAPropertyWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAPropertyWriter<T extends DAWriter> extends AbstractDAWriter<T> {
    private final String name;
    private final DAType type;
    private List<DAType> annotations = Collections.<DAType>emptyList();
    private Set<Modifier> modifiers = Collections.<Modifier>emptySet();

    DAPropertyWriter(String name, DAType type, BufferedWriter bw, T parent, int indentOffset) {
        super(bw, parent, indentOffset);
        this.name = name;
        this.type = type;
    }

    public DAPropertyWriter<T> withAnnotations(List<DAType> annotations) {
        this.annotations = annotations == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(annotations);
        return this;
    }

    public DAPropertyWriter<T> withModifier(Set<Modifier> modifiers) {
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
        commons.newLine();
        return parent;
    }
}
