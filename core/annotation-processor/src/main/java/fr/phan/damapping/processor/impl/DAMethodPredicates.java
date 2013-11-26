package fr.phan.damapping.processor.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

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
        public boolean apply(@Nullable DAMethod daMethod) {
            return daMethod.isConstructor();
        }
    }

    public static Predicate<DAMethod> isStatic() {
        return StaticPredicate.INSTANCE;
    }

    private static enum  StaticPredicate implements Predicate<DAMethod> {
        INSTANCE;

        @Override
        public boolean apply(@Nullable DAMethod daMethod) {
            return daMethod.modifiers.contains(Modifier.STATIC);
        }
    }

    public static Predicate<DAMethod> notPrivate() {
        return NotPrivatePredicate.INSTANCE;
    }

    private static enum NotPrivatePredicate implements Predicate<DAMethod> {
        INSTANCE;
        @Override
        public boolean apply(@Nullable DAMethod daMethod) {
            return !FluentIterable.from(daMethod.modifiers).firstMatch(Predicates.equalTo(Modifier.PRIVATE)).isPresent();
        }

    }

    public static Predicate<DAMethod> isMapperFactoryMethod() {
        return MapperFactoryMethodPredicate.INSTANCE;
    }

    private static enum MapperFactoryMethodPredicate implements Predicate<DAMethod> {
        INSTANCE;

        @Override
        public boolean apply(@Nullable DAMethod daMethod) {
            return daMethod != null && daMethod.mapperFactoryMethod;
        }

    }

    public static Predicate<DAMethod> isGuavaFunction() {
        return GuavaFunction.INSTANCE;
    }

    private static enum GuavaFunction implements Predicate<DAMethod> {
        INSTANCE;

        @Override
        public boolean apply(@Nullable DAMethod daMethod) {
            return daMethod != null && daMethod.isGuavaFunction();
        }
    }
}
