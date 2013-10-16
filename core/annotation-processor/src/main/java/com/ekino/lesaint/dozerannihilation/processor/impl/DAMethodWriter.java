package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * DAMethodWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAMethodWriter<T extends DAWriter> extends AbstractDAWriter<T> {
    private final String name;
    private final DAType returnType;
    private Set<Modifier> modifiers = Collections.<Modifier>emptySet();
    private List<DAParameter> params = Collections.<DAParameter>emptyList();

    public DAMethodWriter(String name, DAType returnType, BufferedWriter bw, int indent, T parent) {
        super(bw, parent, indent);
        this.name = name;
        this.returnType = returnType;
    }

    public DAMethodWriter<T> withModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(modifiers);
        return this;
    }

    public DAMethodWriter<T> withParams(List<DAParameter> params) {
        this.params = params == null ? Collections.<DAParameter>emptyList() : ImmutableList.copyOf(params);
        return this;
    }

    public DAMethodWriter<T> start() throws IOException {
        appendIndent();
        appendModifiers(modifiers);
        appendReturnType();
        bw.append(name);
        bw.append("(");
        appendParams();
        bw.append(") {");
        bw.newLine();
        return this;
    }

    private void appendReturnType() throws IOException {
        appendType(bw, returnType);
        bw.append(" ");
    }

    private void appendParams() throws IOException {
        if (params.isEmpty()) {
            return;
        }

        Iterator<DAParameter> it = params.iterator();
        while (it.hasNext()) {
            DAParameter parameter = it.next();
            appendType(bw, parameter.type);
            bw.append(" ").append(parameter.name);
            if (it.hasNext()) {
                bw.append(", ");
            }
        }
    }

    DAStatementWriter<DAMethodWriter<T>> newStatement() {
        return new DAStatementWriter<DAMethodWriter<T>>(bw, this, indent + 1);
    }

    public T end() throws IOException {
        appendIndent();
        bw.append("}");
        bw.newLine();
        bw.newLine();
        return parent;
    }
}
