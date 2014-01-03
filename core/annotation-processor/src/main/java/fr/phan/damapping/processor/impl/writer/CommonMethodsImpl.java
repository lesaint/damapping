package fr.phan.damapping.processor.impl.writer;

import fr.phan.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

/**
 * CommonMethods -
 *
 * @author Sébastien Lesaint
 */
class CommonMethodsImpl implements CommonMethods {
    protected static final String INDENT = "    ";

    private final BufferedWriter bw;
    private final int indent;

    public CommonMethodsImpl(BufferedWriter bw, int indent) {
        this.bw = bw;
        this.indent = indent;
    }

    @Override
    public BufferedWriter getBufferedWriter() {
        return bw;
    }

    @Override
    public int getIndent() {
        return indent;
    }

    @Override
    public void appendIndent() throws IOException {
        for (int i = 0; i < indent; i++) {
            bw.append(INDENT);
        }
    }

    @Override
    public void appendModifiers(Set<Modifier> modifiers) throws IOException {
        // TODO add sorting of Modifiers according to best practice
        if (modifiers.isEmpty()) {
            return;
        }
        Iterator<Modifier> it = modifiers.iterator();
        while (it.hasNext()) {
            bw.append(it.next().toString()).append(" ");
        }
    }

    @Override
    public void appendAnnotations(Collection<DAType> annotations) throws IOException {
        if (annotations.isEmpty()) {
            return;
        }

        for (DAType annotation : annotations) {
            appendIndent();
            bw.append("@").append(annotation.getSimpleName());
            bw.newLine();
        }
    }

    @Override
    public void appendType(DAType type) throws IOException {
        bw.append(type.getSimpleName());
        if (type.getExtendsBound() != null) {
            bw.append(" extends ");
            appendType(type.getExtendsBound());
        }
        else if (type.getSuperBound() != null) {
            bw.append(" super ");
            appendType(type.getExtendsBound());
        }
        appendTypeArgs(type.getTypeArgs());
        if (type.isArray()) {
            bw.append("[]");
        }
    }

    @Override
    public void appendTypeArgs(List<DAType> typeArgs) throws IOException {
        if (!typeArgs.isEmpty()) {
            Iterator<DAType> iterator = typeArgs.iterator();
            bw.append("<");
            while (iterator.hasNext()) {
                DAType arg = iterator.next();
                appendType(arg);
                if (iterator.hasNext()) {
                    bw.append(", ");
                }
            }
            bw.append(">");
        }
    }

    @Override
    public void newLine() throws IOException {
        bw.newLine();
    }

    @Override
    public void flush() throws IOException {
        bw.flush();
    }

    @Override
    public void close() throws IOException {
        bw.close();
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        return bw.append(csq);
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        return bw.append(csq, start, end);
    }

    @Override
    public Writer append(char c) throws IOException {
        return bw.append(c);
    }
}
