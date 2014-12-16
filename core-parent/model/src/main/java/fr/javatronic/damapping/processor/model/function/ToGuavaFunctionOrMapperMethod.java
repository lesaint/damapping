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
package fr.javatronic.damapping.processor.model.function;

import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Optional;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.processor.model.predicate.DAInterfacePredicates.isGuavaFunction;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isApplyWithSingleParam;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isNotConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isNotMapperFactoryMethod;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isNotStatic;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isPublic;
import static fr.javatronic.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * ToGuavaFunctionOrMapperMethod - This function allows to transform a collection of DAMethod into a Collection of
 * DAMethod which properties {@link DAMethod#isMapperMethod()} and {@link DAMethod#isGuavaFunctionApplyMethod()} are
 * set to be used to build a {@link fr.javatronic.damapping.processor.model.DASourceClass}.
 * <p>
 * This function takes the List of interfaces implemented by the source class as a constructor in order to identify
 * whether the source class implements Guava's Function.
 * </p>
 * <p>
 * This class is an abstract class that defines two abtract methods intended to be implemented by subclass to create
 * a DAMethod with either the {@code mapperMethod} or the {@code guavaFunctionApplyMethod} flag.
 * </p>
 *
 * @author Sébastien Lesaint
 */
public abstract class ToGuavaFunctionOrMapperMethod<T extends DAMethod> implements Function<T, DAMethod> {
  @Nonnull
  private final Optional<DAInterface> guavaFunctionInterface;

  public ToGuavaFunctionOrMapperMethod(@Nullable List<DAInterface> interfaces) {
    this.guavaFunctionInterface = from(nonNullFrom(interfaces))
        .filter(isGuavaFunction())
        .first();
  }

  @Nullable
  @Override
  public DAMethod apply(@Nullable T daMethod) {
    if (daMethod == null) {
      return null;
    }
    if (isGuavaFunctionMethod(daMethod)) {
      return toGuavaFunction(daMethod);
    }
    if (isMapperMethod(daMethod)) {
      return toMapperMethod(daMethod);
    }
    return daMethod;
  }

  /**
   * Creates a DAMethod from the specified one which is identical in every aspect to the argument except that its
   * {@link DAMethod#isMapperMethod()} method must returns {@code true}.
   *
   * @param daMethod an instance of type {@code T}
   *
   * @return a {@link DAMethod}
   */
  @Nonnull
  protected abstract DAMethod toMapperMethod(@Nonnull T daMethod);


  /**
   * Creates a DAMethod from the specified one which is identical in every aspect to the argument except that its
   * {@link DAMethod#isGuavaFunctionApplyMethod()} method must returns {@code true}.
   *
   * @param daMethod an instance of type {@code T}
   *
   * @return a {@link DAMethod}
   */
  @Nonnull
  protected abstract DAMethod toGuavaFunction(@Nonnull T daMethod);

  private boolean isGuavaFunctionMethod(DAMethod daMethod) {
    return guavaFunctionInterface.isPresent()
        && isApplyWithSingleParam().apply(daMethod)
        && checkTypes(daMethod, guavaFunctionInterface.get());
  }

  private static boolean checkTypes(DAMethod applyMethod, DAInterface guavaFunctionInterface) {
    // applyMethod must have a return type
    DAType returnType = applyMethod.getReturnType();
    if (returnType == null) {
      return false;
    }

    DAType parameterType = applyMethod.getParameters().iterator().next().getType();
    List<DAType> typeArgs = guavaFunctionInterface.getType().getTypeArgs();

    return Objects.equals(parameterType.getQualifiedName(), typeArgs.get(0).getQualifiedName())
        && Objects.equals(returnType.getQualifiedName(), typeArgs.get(1).getQualifiedName());
  }

  private static boolean isMapperMethod(DAMethod daMethod) {
    return isNotStatic().apply(daMethod)
        && isNotConstructor().apply(daMethod)
        && isNotMapperFactoryMethod().apply(daMethod)
        && isPublic().apply(daMethod);
  }
}
