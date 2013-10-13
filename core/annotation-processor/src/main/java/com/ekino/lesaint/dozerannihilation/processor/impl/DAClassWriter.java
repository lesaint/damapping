package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * DAClassWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAClassWriter extends DAWriter {
    private final BufferedWriter bw;
    private final int indent;
    private Set<Modifier> modifiers;
    private List<DAType> annotations;

    DAClassWriter(BufferedWriter bw, int indent) {
        this.bw = bw;
        this.indent = indent;
    }

    DAClassWriter withModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    DAClassWriter withAnnotations(List<DAType> annotations) {
        this.annotations = annotations;
        return this;
    }

    DAClassWriter startClass(String name) throws IOException {
        appendAnnotations();
        appendIndent(bw, indent);
        appendModifiers();
        bw.append(name);
        return this;
    }

    private void appendAnnotations() {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void appendModifiers() {
        //To change body of created methods use File | Settings | File Templates.
    }
}
