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
package fr.phan.damapping.processor.model;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.lang.model.element.Modifier;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
* DAParameter - Représente un paramètre de méthode avec son nom, son type et ses éventuels
* modifieurs ("final" en particulier).
*
* @author Sébastien Lesaint
*/
@Immutable
public class DAParameter {
    /*nom du paramètre*/
    @Nonnull
    private final DAName name;
    @Nonnull
    private final DAType type;
    @Nonnull
    private final Set<Modifier> modifiers;

    private DAParameter(Builder builder) {
        name = builder.name;
        type = builder.type;
        modifiers = builder.modifiers == null ? ImmutableSet.<Modifier>of() : ImmutableSet.copyOf(builder.modifiers);
    }

    @Nonnull
    public static Builder builder(@Nonnull DAName name, @Nonnull DAType type) {
        return new Builder(name, type);
    }

    @Nonnull
    public DAName getName() {
        return name;
    }

    @Nonnull
    public DAType getType() {
        return type;
    }

    @Nonnull
    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public static class Builder {
        @Nonnull
        private final DAName name;
        @Nonnull
        private final DAType type;
        @Nullable
        private Set<Modifier> modifiers;

        public Builder(@Nonnull DAName name, @Nonnull DAType type) {
            this.name = checkNotNull(name);
            this.type = checkNotNull(type);
        }

        public Builder withModifiers(@Nullable Set<Modifier> modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public DAParameter build() {
            return new DAParameter(this);
        }
    }
}
