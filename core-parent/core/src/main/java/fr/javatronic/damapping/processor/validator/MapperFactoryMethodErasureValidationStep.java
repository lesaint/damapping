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
import fr.javatronic.damapping.processor.model.function.DAParameterFunctions;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAParameterPredicates;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperFactoryMethodErasureValidationStep - Validates that no two methods/constructor annotated with
 * {@link fr.javatronic.damapping.annotation.MapperFactory} will both generate the same method in the
 * MapperFactory interface, causing a compilation error "method is already defined ...".
 * <p>
 * This can happen when two methods/constructor defines the same parameters (in the same order) not annotated with
 * {@link fr.javatronic.damapping.annotation.MapperDependency}.
 * </p>
 * <p>
 * Example, the following two methods will generate methods with the same signature in the MapperFactory interface!<br/>
 * <pre>
 * {@literal @}Mapper
 * public class InconsistentMapperDependenciesOnConstructor {
 *   {@literal @}MapperFactory
 *   public InconsistentMapperDependenciesOnConstructor(@MapperDependency Boolean top, String someParam) {
 *     // content does not matter
 *   }
 *
 *   {@literal @}MapperFactory
 *   public InconsistentMapperDependenciesOnConstructor(String otherParam, @MapperDependency Boolean top) {
 *     // content does not matter
 *   }
 *
 *   public String map(Integer input) {
 *     return null;
 *   }
 * }
 * </pre>
 * </p>
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryMethodErasureValidationStep implements ValidationStep {

  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    Set<DAMethodSignature> mapperDependencies = new HashSet<>();

    for (DAMethod daMethod : from(sourceClass.getMethods()).filter(DAMethodPredicates.isMapperFactoryMethod())) {
      DAMethodSignature DAMethodSignature = DAMethodToSIgnatureWithoutMapperDependencyParameters.INSTANCE.apply(
          daMethod
      );

      if (mapperDependencies.contains(DAMethodSignature)) {
        throw new ValidationError(
            "Can not have multiple methods/constructors annotated with @MapperFactory which have the same signature " +
                "when parameters annotated with @MapperDependency are ignored.",
            sourceClass,
            daMethod,
            extractMapperFactoryAnnotation(daMethod)
        );
      }
      else {
        mapperDependencies.add(DAMethodSignature);
      }
    }
  }

  private static DAAnnotation extractMapperFactoryAnnotation(DAMethod daMethod) {
    return from(daMethod.getAnnotations()).filter(DAAnnotationPredicates.isMapperFactoryMethod()).first().get();
  }

  private static enum DAMethodToSIgnatureWithoutMapperDependencyParameters
      implements Function<DAMethod, DAMethodSignature> {
    INSTANCE;

    private static final Predicate<DAParameter> FILTER_OUT_MAPPER_DEPENDENCY_PARAMETERS = Predicates.not(
        DAParameterPredicates.hasMapperDependencyAnnotation()
    );

    @Nullable
    @Override
    public DAMethodSignature apply(@Nullable DAMethod daMethod) {
      if (daMethod == null) {
        return null;
      }
      return new DAMethodSignature(
          daMethod.getReturnType(),
          daMethod.getName().getName(),
          from(daMethod.getParameters())
              .filter(FILTER_OUT_MAPPER_DEPENDENCY_PARAMETERS)
              .transform(DAParameterFunctions.toType())
              .toList()
      );
    }
  }
}
