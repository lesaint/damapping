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
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAParameterPredicates;

import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperDependencyOnMapperFactoryValidationStep - Validates that the
 * {@link fr.javatronic.damapping.annotation.MapperDependency} annotation is only used on parameters of a method
 * annotated with {@link fr.javatronic.damapping.annotation.MapperFactory}.
 *
 * @author Sébastien Lesaint
 */
public class MapperDependencyOnMapperFactoryValidationStep implements ValidationStep {
  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    for (DAMethod daMethod : from(sourceClass.getMethods()).filter(DAMethodPredicates.hasMapperDependencyParameters())) {
      if (!daMethod.isMapperFactoryMethod()) {
        DAParameter mapperDependencyParameter = extractMapperDependencyParameter(daMethod.getParameters());
        throw new ValidationError(
            "Only parameters of a method annotated with @MapperFactory can be annotated with @MapperDependency",
            sourceClass,
            mapperDependencyParameter,
            extractMapperFactoryAnnotation(mapperDependencyParameter)
        );
      }
    }
  }

  private static DAParameter extractMapperDependencyParameter(List<DAParameter> parameters) {
    return from(parameters).filter(DAParameterPredicates.hasMapperDependencyAnnotation()).first().get();
  }

  private static DAAnnotation extractMapperFactoryAnnotation(DAParameter parameter) {
    return from(parameter.getAnnotations()).filter(DAAnnotationPredicates.isMapperDependency()).first().get();
  }
}
