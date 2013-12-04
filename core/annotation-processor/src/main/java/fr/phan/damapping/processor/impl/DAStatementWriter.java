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
package fr.phan.damapping.processor.impl;

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

    DAStatementWriter(BufferedWriter bw, T parent, int indent) {
        super(bw, parent, indent);
    }

    DAStatementWriter<T> start() throws IOException {
        appendIndent();
        return this;
    }

    DAStatementWriter<T> append(CharSequence s) throws IOException {
        bw.append(s);
        return this;
    }

    DAStatementWriter<T> append(char c) throws IOException {
        bw.append(c);
        return this;
    }

    DAStatementWriter<T> appendType(DAType type) throws IOException {
        appendType(bw, type);
        return this;
    }

    DAStatementWriter<T> appendTypeArgs(List<DAType> typeArgs) throws IOException {
        appendTypeArgs(bw, typeArgs);
        return this;
    }

    DAStatementWriter<T> appendParamValues(List<DAParameter> params) throws IOException {
        if (params.isEmpty()) {
            bw.append("()");
            return this;
        }

        bw.append("(");
        Iterator<DAParameter> it = params.iterator();
        while (it.hasNext()) {
            DAParameter parameter = it.next();
            bw.append(parameter.getName());
            if (it.hasNext()) {
                bw.append(", ");
            }
        }
        bw.append(")");
        return this;
    }

    T end() throws IOException {
        bw.append(";");
        bw.newLine();
        return parent;
    }
}
