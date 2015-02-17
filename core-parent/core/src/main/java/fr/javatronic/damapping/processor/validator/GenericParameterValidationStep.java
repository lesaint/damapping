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
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;

import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * GenericParameterValidationStep - Makes sure the dedicated class does not have any generic parameter.
 *
 * @author Sébastien Lesaint
 */
public class GenericParameterValidationStep implements ValidationStep {

  public static final String DEDICATED_CLASS_CAN_NOT_HAVE_GENERIC_PARAMETER_ERROR_MSG = "Class annotated with " +
      "@Mapper can not have generic parameters";

  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    if (!sourceClass.getType().getTypeArgs().isEmpty()) {
      throw new ValidationError(
          DEDICATED_CLASS_CAN_NOT_HAVE_GENERIC_PARAMETER_ERROR_MSG,
          sourceClass, null, getMapperAnnotation(sourceClass)
      );
    }
  }

  private static DAAnnotation getMapperAnnotation(DASourceClass sourceClass) {
    return from(sourceClass.getAnnotations()).filter(DAAnnotationPredicates.isMapper()).first().get();
  }
}
