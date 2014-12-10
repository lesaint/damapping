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
import fr.javatronic.damapping.processor.model.constants.Jsr330Constants;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.util.Predicates;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isGuavaFunctionApply;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isImpliciteMapperMethod;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperFactoryMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * DASourceClassValidator -
 * <p/>
 * TODO add unit test coverage for DASourceClassValidatorImpl
 *
 * @author Sébastien Lesaint
 */
public class DASourceClassValidatorImpl implements DASourceClassValidator {
  @Override
  public void validate(DASourceClass sourceClass) throws ValidationError {
    validateAnnotations(sourceClass.getAnnotations());
    validateModifiers(sourceClass.getModifiers());
    validateMethods(sourceClass);
    validateJSR330InPath(sourceClass);

    // retrieve instantiation type from @Mapper annotation
    //  - CONSTRUCTOR : validate public/protected default constructor exists sinon erreur de compilation
    //  - SINGLETON_ENUM : validate @Mapper class is an enum + validate there is only one value sinon erreur de
    // compilation
    validateInstantiationTypeRequirements(sourceClass);
  }

  private void validateAnnotations(List<DAAnnotation> annotations) throws ValidationError {
    List<DAAnnotation> mapperAnnotations = from(annotations).filter(DAAnnotationPredicates.isMapper()).toList();
    if (mapperAnnotations.size() > 1) {
      throw new ValidationError("Mapper with more than one @Mapper annotation is not supported");
    }
    if (mapperAnnotations.isEmpty()) {
      throw new ValidationError("Mapper without @Mapper annotation is not supported");
    }
  }

  @Override
  public void validateModifiers(Set<DAModifier> modifiers) throws ValidationError {
    // retrieve qualifiers of the class with @Mapper + make validate : must be public or protected sinon erreur de
    // compilation
    if (modifiers.contains(DAModifier.PRIVATE)) {
      throw new ValidationError("Class annoted with @Mapper can not be private");
    }
  }

  private void hasAccessibleConstructor(DASourceClass sourceClass) throws ValidationError {
    if (sourceClass.getAccessibleConstructors().isEmpty()) {
      throw new ValidationError("Classe does not exposed an accessible default constructor");
    }
  }

  private void hasOnlyOneEnumValue(DASourceClass daSourceClass) throws ValidationError {
    if (daSourceClass.getEnumValues().size() != 1) {
      throw new ValidationError("Enum annoted wih @Mapper must have one value");
    }
  }

  public void validateMethods(DASourceClass sourceClass) throws ValidationError {
    List<DAMethod> methods = sourceClass.getMethods();
    // rechercher une ou plusieurs méthodes annotées avec @MapperFunction
    // si classe @Mapper implémente Function, la rechercher en commençant par les méthodes annotées avec @MapperFunction
    // si aucune méthode trouvée => erreur  de compilation
    // TOIMPROVE : la récupération et les contrôles sur la méthode apply sont faibles
    if (methods.isEmpty()) {
      throw new ValidationError("Class annoted with @Mapper must have at least one method");
    }

    int mapperMethodCount = from(methods)
        .filter(Predicates.or(isGuavaFunctionApply(), isImpliciteMapperMethod()))
        .size();
    // until we support @MapperMethod, this first case can not happen because of how the DAMethod flags are set
    if (mapperMethodCount > 1) {
      throw new ValidationError("Mapper having more than one method qualifying as mapper method is not supported");
    }
    if (mapperMethodCount == 0) {
      throw new ValidationError("Mapper must have one and only one method qualifying as mapper method (either implemente Guava's Function interface or define a single non private method)");
    }

    for (DAMethod daMethod : from(methods).filter(isMapperFactoryMethod())) {
      if (!isValidMapperFactoryMethodKindAndQualifiers(daMethod)) {
        throw new ValidationError("Method annotated with @MapperFactory must either be a public constructor or a public static method");
      }
      if (!isValidMapperFactoryReturnType(sourceClass, daMethod.getReturnType())) {
        throw new ValidationError("Method annotated with @MapperFactory must return type of the class annotated with @Mapper");
      }
    }
  }

  private static boolean isValidMapperFactoryMethodKindAndQualifiers(@Nonnull DAMethod daMethod) {
    return daMethod.getModifiers().contains(DAModifier.PUBLIC) &&
        (daMethod.isConstructor() || daMethod.getModifiers().contains(DAModifier.STATIC));
  }

  private static boolean isValidMapperFactoryReturnType(@Nonnull DASourceClass sourceClass,
                                                        @Nullable DAType returnType) {
    return returnType != null
        && returnType.getQualifiedName() != null
        && returnType.getQualifiedName().equals(sourceClass.getType().getQualifiedName());
  }

  private void validateJSR330InPath(DASourceClass sourceClass) throws ValidationError {
    if (sourceClass.getInjectableAnnotation().isPresent() &&  !Jsr330Constants.isJSR330Present()) {
      throw new ValidationError("Class annotated with @Mapper and @Injectable requires JSR 330's annotations (@Named, @Inject, ...) to be available in classpath");
    }
  }

  @Override
  public void validateInstantiationTypeRequirements(DASourceClass daSourceClass) throws ValidationError {
    // TODO vérifier qu'il n'y a pas d'usage illegal de @MapperFactory (ie. sur méthode non statique)
    switch (daSourceClass.getInstantiationType()) {
      case CONSTRUCTOR:
        hasAccessibleConstructor(daSourceClass);
        break;
      case SINGLETON_ENUM:
        hasOnlyOneEnumValue(daSourceClass);
        break;
      case CONSTRUCTOR_FACTORY:
        // TODO ajouter checks pour InstantiationType.CONSTRUCTOR_FACTORY (vérifier que pas d'autre méthode annotée
        // avec @MapperFactory)
        break;
      case STATIC_FACTORY:
        // TODO ajouter checks pour InstantiationType.public_FACTORY (vérifier que pas de constructeur à paramètre)
        break;
      default:
        throw new IllegalArgumentException("Unsupported instantiationType " + daSourceClass.getInstantiationType());
    }
  }
}
