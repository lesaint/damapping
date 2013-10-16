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
 * DAClassWriter -
 *
 * améliorations à réaliser de DAClassWriter
 * <ul>
 *     <li>ajouter un contrôle sur les modifiers autorisés pour une classe</li>
 *     <li>ajouter un paramètre "boolean classOfJavaSource" pour refuser le modifier PUBLIC si ce paramètre est false</li>
 *     <li>ajouter une vérification d'état, si start() a été appelé, withModifiers, withAnnotations, withImplemented, withExtented échouent</li>
 *     <li>ajouter vérification d'état : end() must be called after start()</li>
 *     <li>ajouter vérification d'état : plus de call sur aucune méthode si end() a été appelé</li>
 *     <li>ajouter vérification des paramètres de withModifiers, withAnnotations, withImplemented, withExtented</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAClassWriter<T extends DAWriter> extends DAWriter {
    private final BufferedWriter bw;
    private final int indent;
    private final String name;
    private final T parent;
    private Set<Modifier> modifiers = Collections.emptySet();
    private List<DAType> annotations = Collections.emptyList();
    private List<DAType> implemented = Collections.emptyList();
    private DAType extended;

    DAClassWriter(String name, BufferedWriter bw, int indent, T parent) {
        this.name = name;
        this.bw = bw;
        this.indent = indent;
        this.parent = parent;
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
        appendAnnotations();
        appendIndent(bw, indent);
        appendModifiers();
        bw.append("class ").append(name).append(" ");
        appendExtended();
        appendImplemented();
        bw.append("{");
        bw.newLine();
        return this;
    }

    T end() throws IOException {
        appendIndent(bw, indent);
        bw.append("}");
        return parent;
    }

    private void appendAnnotations() throws IOException {
        if (annotations.isEmpty()) {
            return;
        }

        for (DAType annotation : annotations) {
            appendIndent(bw, indent);
            bw.append("@").append(annotation.simpleName);
            bw.newLine();
        }
    }

    private void appendModifiers() throws IOException {
        if (modifiers.isEmpty()) {
            return;
        }
        Iterator<Modifier> it = modifiers.iterator();
        while (it.hasNext()) {
            bw.append(it.next().toString()).append(" ");
        }
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
