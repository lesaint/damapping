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
package fr.javatronic.damapping.processor.validator;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isDefaultConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperFactoryMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperFactoryMethodsValidationStep - Validates methods annotated with @MapperFactory
 * <ul>
 *   <li>each one must either be a public constructor or a public static method</li>
 *   <li>static methods must return the type of the DASourceClass</li>
 *   <li>constructor must have at least one parameter</li>
 *   <li>DASOurceCLass must either have constructor MapperFactory or static methods MapperFactory, not both</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryMethodsValidationStep implements ValidationStep {
  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    boolean hasConstructor = false;
    boolean hasStaticMethod = false;
    for (DAMethod daMethod : from(sourceClass.getMethods()).filter(isMapperFactoryMethod())) {
      validateSignature(sourceClass, daMethod);
      hasConstructor |= daMethod.isConstructor();
      hasStaticMethod |= !daMethod.isConstructor();
    }

    validateUsesEitherConstructorOrMethod(sourceClass, hasConstructor, hasStaticMethod);

    validateConstructorHasAtLeastOneArgument(sourceClass);
  }

  private static void validateSignature(DASourceClass sourceClass, DAMethod daMethod) throws ValidationError {
    if (!isValidMapperFactoryMethodKindAndQualifiers(daMethod)) {
      throw new ValidationError(
          "Method annotated with @MapperFactory must either be a public constructor or a public static method",
          sourceClass, daMethod, extractMapperFactoryAnnotation(daMethod.getAnnotations())
      );
    }
    if (!isValidMapperFactoryReturnType(sourceClass, daMethod.getReturnType())) {
      throw new ValidationError("Method annotated with @MapperFactory must return type of the class annotated with " +
          "@Mapper",
          sourceClass, daMethod, extractMapperFactoryAnnotation(daMethod.getAnnotations())
      );
    }
  }

  private static boolean isValidMapperFactoryMethodKindAndQualifiers(@Nonnull DAMethod daMethod) {
    return daMethod.getModifiers().contains(DAModifier.PUBLIC) &&
        (daMethod.isConstructor() || daMethod.getModifiers().contains(DAModifier.STATIC));
  }

  private static DAAnnotation extractMapperFactoryAnnotation(List<DAAnnotation> annotations) {
    return from(annotations).filter(DAAnnotationPredicates.isMapperFactoryMethod()).first().get();
  }

  private static boolean isValidMapperFactoryReturnType(@Nonnull DASourceClass sourceClass,
                                                        @Nullable DAType returnType) {
    return returnType != null
        && Objects.equals(returnType.getQualifiedName(), sourceClass.getType().getQualifiedName());
  }

  private static void validateUsesEitherConstructorOrMethod(DASourceClass sourceClass, boolean hasConstructor,
                                                            boolean hasStaticMethod) throws ValidationError {
    if (hasConstructor && hasStaticMethod) {
      throw new ValidationError(
          "Dedicated class can have both constructor(s) and static method(s) annotated with @MapperFactory",
          sourceClass, null, null
      );
    }
  }

  private static void validateConstructorHasAtLeastOneArgument(DASourceClass sourceClass) throws ValidationError {
    List<DAMethod> mapperFactoryConstructors = from(sourceClass.getMethods())
        .filter(isMapperFactoryMethod())
        .filter(isConstructor())
        .toList();
    if (mapperFactoryConstructors.size() == 1) {
      DAMethod mapperFactoryConstructor = mapperFactoryConstructors.iterator().next();
      if (isDefaultConstructor().apply(mapperFactoryConstructor)) {
        throw new ValidationError(
            "@MapperFactory can not be used for default constructor when there is no other constructor defined. "
                + "If you do not need another constructor, just remove the @MapperFactory",
            sourceClass,
            mapperFactoryConstructor,
            extractMapperFactoryAnnotation(mapperFactoryConstructor.getAnnotations())
        );
      }
    }
  }
}
