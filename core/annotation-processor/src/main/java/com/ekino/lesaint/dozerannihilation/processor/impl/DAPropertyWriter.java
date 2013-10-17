package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;

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

    DAPropertyWriter(String name, DAType type, BufferedWriter bw, T parent, int indent) {
        super(bw, parent, indent);
        this.name = name;
        this.type = type;
    }

    DAPropertyWriter<T> withAnnotations(List<DAType> annotations) {
        this.annotations = annotations == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(annotations);
        return this;
    }

    DAPropertyWriter<T> withModifier(Set<Modifier> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    T write() throws IOException {
        appendAnnotations(annotations);
        appendIndent();
        appendModifiers(modifiers);
        appendType(type);
        bw.append(" ").append(name).append(";");
        bw.newLine();
        bw.newLine();
        return parent;
    }
}
