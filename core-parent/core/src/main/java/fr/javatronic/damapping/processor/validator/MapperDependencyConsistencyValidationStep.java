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
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAParameterPredicates;

import java.util.Set;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperDependencyConsistencyValidationStep - Validates that if at least one method annotated with
 * {@link fr.javatronic.damapping.annotation.MapperFactory} has one or more parameters annotated with
 * {@link fr.javatronic.damapping.annotation.MapperDependency}, all methods annotated with
 * {@link fr.javatronic.damapping.annotation.MapperFactory} have the same parameters annotated with
 * {@link fr.javatronic.damapping.annotation.MapperDependency (same name, same type, order of parameters does not
 * matter}.
 *
 * @author Sébastien Lesaint
 */
public class MapperDependencyConsistencyValidationStep implements ValidationStep {
  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    Set<DAParameterIdentifier> mapperDependencies = null;
    for (DAMethod daMethod : from(sourceClass.getMethods()).filter(DAMethodPredicates.isMapperFactoryMethod())) {
      Set<DAParameterIdentifier> currentMapperDependencies = from(daMethod.getParameters())
          .filter(DAParameterPredicates.hasMapperDependencyAnnotation())
          .transform(DAParameterIdentifier.fromDAParameter())
          .toSet();

      if (mapperDependencies == null) {
        mapperDependencies = currentMapperDependencies;
      }
      else if (!mapperDependencies.equals(currentMapperDependencies)) {
        throw new ValidationError(
            "All methods annotated with @MapperFactory must have the same set of parameters annotated with" +
                "@MapperDependency (same name, same type, order does not matter)",
            sourceClass,
            daMethod,
            extractMapperFactoryAnnotation(daMethod)
        );
      }
    }
  }

  private static DAAnnotation extractMapperFactoryAnnotation(DAMethod daMethod) {
    return from(daMethod.getAnnotations()).filter(DAAnnotationPredicates.isMapperFactoryMethod()).first().get();
  }

}
