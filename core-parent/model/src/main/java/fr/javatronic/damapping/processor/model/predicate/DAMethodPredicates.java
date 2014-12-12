/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.impl.DAMethodImpl;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.impl.DAParameterImpl;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.impl.DATypeImpl;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;
import fr.javatronic.damapping.util.Sets;

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

  public static Predicate<DAMethod> isNotStatic() {
    return NOT_STATIC;
  }

  private static final Predicate<DAMethod> NOT_STATIC = Predicates.not(StaticPredicate.INSTANCE);

  public static Predicate<DAMethod> isPublic() {
    return PublicPredicate.INSTANCE;
  }

  private static enum PublicPredicate implements Predicate<DAMethod> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAMethod daMethod) {
      return daMethod != null && daMethod.getModifiers().contains(DAModifier.PUBLIC);
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

  private static final Predicate<DAMethod> NOT_MAPPERFACTORY_METHOD = Predicates.not(MapperFactoryMethodPredicate.INSTANCE);

  public static Predicate<DAMethod> isNotMapperFactoryMethod() {
    return NOT_MAPPERFACTORY_METHOD;
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
          && "apply".equals(daMethod.getName().getName())
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

  public static Predicate<DAMethod> isCompilerGeneratedForEnum(DAType enumType) {
    return new CompilerGeneratedEnumMethods(enumType);
  }

  private static class CompilerGeneratedEnumMethods implements Predicate<DAMethod> {
    private final DAType enumType;
    private final DAMethod valuesMethod;
    private final DAMethod valueOfMethod;

    private CompilerGeneratedEnumMethods(DAType enumType) {
      this.enumType = enumType;
      this.valuesMethod = DAMethodImpl.methodBuilder()
                                  .withName(DANameFactory.from("values"))
                                  .withModifiers(Sets.of(DAModifier.PUBLIC, DAModifier.STATIC))
                                  .withReturnType(
                                      DATypeImpl.typeBuilder(DATypeKind.DECLARED, enumType.getSimpleName())
                                            .withQualifiedName(enumType.getQualifiedName())
                                            .build()
                                  ).build();
      this.valueOfMethod = DAMethodImpl.methodBuilder()
          .withName(DANameFactory.from("valueOf"))
          .withModifiers(Sets.of(DAModifier.PUBLIC, DAModifier.STATIC))
          .withReturnType(enumType)
          .withParameters(Lists.of(
              DAParameterImpl.builder(DANameFactory.from("name"), DATypeFactory.from(String.class)).build()))
          .build();
    }

    @Override
    public boolean apply(@Nullable DAMethod daMethod) {
      if (daMethod == null) {
        return false;
      }

      return this.valuesMethod.equals(daMethod) || this.valueOfMethod.equals(daMethod);
    }
  }
}
