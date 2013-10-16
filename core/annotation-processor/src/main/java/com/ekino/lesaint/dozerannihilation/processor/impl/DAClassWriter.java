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
    private Set<Modifier> modifiers = Collections.emptySet();
    private List<DAType> annotations = Collections.emptyList();
    private List<DAType> implemented = Collections.emptyList();
    private DAType extended;

    DAClassWriter(String name, BufferedWriter bw, int indent, T parent) {
        super(bw, parent, indent);
        this.name = name;
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
        appendModifiers(modifiers);
        bw.append("class ").append(name).append(" ");
        appendExtended();
        appendImplemented();
        bw.append("{");
        bw.newLine();
        bw.newLine();
        return this;
    }

    DAMethodWriter<DAClassWriter<T>> newMethod(String name, DAType returnType) {
        return new DAMethodWriter<DAClassWriter<T>>(name, returnType, bw, indent + 1, this);
    }

    T end() throws IOException {
        appendIndent();
        bw.append("}");
        bw.newLine();
        return parent;
    }

    private void appendExtended() throws IOException {
        if (extended == null) {
            return;
        }

        bw.append("extends ");
        appendType(extended);
        bw.append(" ");
    }

    private void appendImplemented() throws IOException {
        if (implemented.isEmpty()) {
            return;
        }

        bw.append("implements ");
        Iterator<DAType> it = implemented.iterator();
        while (it.hasNext()) {
            appendType(it.next());
            if (it.hasNext()) {
                bw.append(",");
            }
            bw.append(" ");
        }
    }
}
