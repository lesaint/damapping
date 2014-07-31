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
package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.util.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
      return daMethod != null && daMethod.isConstructor();
    }
  }

  public static Predicate<DAMethod> isNotConstructor() {
    return NotConstructorPredicate.INSTANCE;
  }

  private static enum NotConstructorPredicate implements Predicate<DAMethod> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAMethod daMethod) {
      return daMethod != null && !daMethod.isConstructor();
    }
  }

  public static Predicate<DAMethod> isGuavaFunctionApply() {
    return GuavaFunctionApplyMethod.INSTANCE;
  }

  private static enum GuavaFunctionApplyMethod implements Predicate<DAMethod> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAMethod daMethod) {
      return daMethod != null && daMethod.isGuavaFunctionApplyMethod();
    }
  }

  public static Predicate<DAMethod> isStatic() {
    return StaticPredicate.INSTANCE;
  }

  private static enum StaticPredicate implements Predicate<DAMethod> {
    INSTANCE;

    @Override
    public boolean apply(@Nonnull DAMethod daMethod) {
      return daMethod.getModifiers().contains(DAModifier.STATIC);
    }
  }

  public static Predicate<DAMethod> isNotPrivate() {
    return NotPrivatePredicate.INSTANCE;
  }

  private static enum NotPrivatePredicate implements Predicate<DAMethod> {
    INSTANCE;

    @Override
    public boolean apply(@Nonnull DAMethod daMethod) {
      return !daMethod.getModifiers().contains(DAModifier.PRIVATE);
    }

  }

  public static Predicate<DAMethod> isMapperMethod() {
    return MapperMethod.INSTANCE;
  }

  private static enum MapperMethod implements Predicate<DAMethod> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAMethod daMethod) {
      return daMethod != null && daMethod.isMapperMethod();
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

  public static Predicate<DAMethod> isApplyWithSingleParam() {
    return ApplyWithSingleParam.INSTANCE;
  }

  private static enum ApplyWithSingleParam implements Predicate<DAMethod> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAMethod daMethod) {
      // TOIMPROVE, check more specific info in the model, can we know if method override from an interface ? we
      // should check the parameter type and the return type
      return daMethod != null
          && !daMethod.isConstructor()
          && daMethod.getName() != null && "apply".equals(daMethod.getName().getName())
          && daMethod.getParameters().size() == 1;
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

  public static Predicate<DAMethod> isImpliciteMapperMethod() {
    return ImpliciteMapperMethod.INSTANCE;
  }

  private static enum ImpliciteMapperMethod implements Predicate<DAMethod> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAMethod daMethod) {
      return daMethod != null && daMethod.isImplicitMapperMethod();
    }
  }
}
