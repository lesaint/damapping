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
package fr.phan.damapping.processor.impl.writer;

import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * DAStatementWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAStatementWriter<T extends DAWriter> extends AbstractDAWriter<T> {

    DAStatementWriter(BufferedWriter bw, T parent, int indentOffset) {
        super(bw, parent, indentOffset);
    }

    public DAStatementWriter<T> start() throws IOException {
        commons.appendIndent();
        return this;
    }

    public DAStatementWriter<T> append(CharSequence s) throws IOException {
        commons.append(s);
        return this;
    }

    public DAStatementWriter<T> append(char c) throws IOException {
        commons.append(c);
        return this;
    }

    public DAStatementWriter<T> appendType(DAType type) throws IOException {
        commons.appendType(type);
        return this;
    }

    public DAStatementWriter<T> appendTypeArgs(List<DAType> typeArgs) throws IOException {
        commons.appendTypeArgs(typeArgs);
        return this;
    }

    public DAStatementWriter<T> appendParamValues(List<DAParameter> params) throws IOException {
        if (params.isEmpty()) {
            commons.append("()");
            return this;
        }

        commons.append("(");
        Iterator<DAParameter> it = params.iterator();
        while (it.hasNext()) {
            DAParameter parameter = it.next();
            commons.append(parameter.getName());
            if (it.hasNext()) {
                commons.append(", ");
            }
        }
        commons.append(")");
        return this;
    }

    public T end() throws IOException {
        commons.append(";");
        commons.newLine();
        return parent;
    }
}
