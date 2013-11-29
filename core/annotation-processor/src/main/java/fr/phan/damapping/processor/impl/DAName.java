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

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * DAName -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAName implements CharSequence, Comparable<DAName> {
    @Nonnull
    private final String name;

    public DAName(@Nonnull String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(DAName o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return name.equals(((DAName) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
