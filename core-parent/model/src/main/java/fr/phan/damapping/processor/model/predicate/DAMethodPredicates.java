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
package fr.phan.damapping.processor.model.predicate;

import fr.phan.damapping.processor.model.DAMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import fr.phan.damapping.processor.model.DAModifier;

/**
 * DAMethodPredicates -
 *
 * @author Sébastien Lesaint
 */
public class DAMethodPredicates {

    private DAMethodPredicates() {
        // prevents instantiation
    }

    public static Predicate<DAMethod> isConstructor() {
        return ConstructorPredicate.INSTANCE;
    }

    private static enum ConstructorPredicate implements Predicate<DAMethod> {
        INSTANCE;

        @Override
        public boolean apply(@Nonnull DAMethod daMethod) {
            return daMethod.getKind() == ElementKind.CONSTRUCTOR;
        }
    }

    public static Predicate<DAMethod> isStatic() {
        return StaticPredicate.INSTANCE;
    }

    private static enum  StaticPredicate implements Predicate<DAMethod> {
        INSTANCE;

        @Override
        public boolean apply(@Nonnull DAMethod daMethod) {
            return daMethod.getModifiers().contains(DAModifier.STATIC);
        }
    }

    public static Predicate<DAMethod> notPrivate() {
        return NotPrivatePredicate.INSTANCE;
    }

    private static enum NotPrivatePredicate implements Predicate<DAMethod> {
        INSTANCE;
        @Override
        public boolean apply(@Nonnull DAMethod daMethod) {
            return !FluentIterable.from(daMethod.getModifiers()).firstMatch(Predicates.equalTo(DAModifier.PRIVATE)).isPresent();
        }

    }

    public static Predicate<DAMethod> isMapperFactoryMethod() {
        return MapperFactoryMethodPredicate.INSTANCE;
    }

    private static enum MapperFactoryMethodPredicate implements Predicate<DAMethod> {
        INSTANCE;

        @Override
        public boolean apply(@Nonnull DAMethod daMethod) {
            return daMethod.isMapperFactoryMethod();
        }

    }

    public static Predicate<DAMethod> isGuavaFunction() {
        return GuavaFunction.INSTANCE;
    }

    private static enum GuavaFunction implements Predicate<DAMethod> {
        INSTANCE;

        @Override
        public boolean apply(@Nonnull DAMethod daMethod) {
            // TOIMPROVE, check more specific info in the model, can we know if method override from an interface ? we should check the parameter type and the return type
            return daMethod.getKind() == ElementKind.METHOD
                    && daMethod.getName() != null && "apply".equals(daMethod.getName().getName());
        }
    }

    public static Predicate<DAMethod> isDefaultConstructor() {
        return new Predicate<DAMethod>() {
            @Override
            public boolean apply(@Nullable DAMethod daMethod) {
                return isConstructor().apply(daMethod) && daMethod.getParameters().isEmpty();
            }
        };
    }

}
