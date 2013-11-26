package fr.phan.damapping.processor.impl;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
            bw.append("@").append(annotation.simpleName);
            bw.newLine();
        }
    }

    void appendType(BufferedWriter bw, DAType type) throws IOException {
        if (type.extendsBound != null) {
            bw.append("? extends ");
            appendType(bw, type.extendsBound);
        }
        else if (type.superBound!= null) {
            bw.append("? super ");
            appendType(bw, type.extendsBound);
        }
        else {
            bw.append(type.simpleName);
        }
        appendTypeArgs(bw, type.typeArgs);
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
