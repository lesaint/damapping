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

import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.util.Optional;

import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isGuavaFunctionApply;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperMethodValidationStep - Validate mapper methods:
 * <ul>
 * <li>DASourceClass must have at least one method</li>
 * <li>DASourceClass must have at least one mapper method</li>
 * <li>DASourceClass can not implement Guava's Function interface AND define one or more mapper method</li>
 * <li>mapper procedure (ie. returning void) must have at least two arguments</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class MapperMethodsValidationStep implements ValidationStep {

  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    List<DAMethod> methods = sourceClass.getMethods();
    if (methods.isEmpty()) {
      throw new ValidationError("Class annoted with @Mapper must have at least one method", sourceClass, null, null);
    }

    Optional<DAMethod> guavaFunction = from(methods).filter(isGuavaFunctionApply()).first();
    Optional<DAMethod> mapperMethod = from(methods).filter(isMapperMethod()).first();

    validateHasAtLeastOneMapperMethod(sourceClass, guavaFunction, mapperMethod);

    validateCanNotUseBothConstructorAndMapperMethod(sourceClass, guavaFunction, mapperMethod);

    validateMapperMethodProcedures(sourceClass);
  }

  private static void validateHasAtLeastOneMapperMethod(DASourceClass sourceClass,
                                                        Optional<DAMethod> guavaFunction,
                                                        Optional<DAMethod> mapperMethod) throws ValidationError {
    if (!guavaFunction.or(mapperMethod).isPresent()) {
      throw new ValidationError(
          "Mapper must have one and only one method qualifying as mapper method (either implemente Guava's Function " +
              "interface or define a single non private method)",
          sourceClass, null, null
      );
    }
  }

  private static void validateCanNotUseBothConstructorAndMapperMethod(DASourceClass sourceClass,
                                                                      Optional<DAMethod> guavaFunction,
                                                                      Optional<DAMethod> mapperMethod)
      throws ValidationError {
    if (guavaFunction.isPresent() && mapperMethod.isPresent()) {
      throw new ValidationError(
          "Mapper must either implement Guava's Function interface or define public method(s), it can not do both",
          sourceClass, mapperMethod.get(), null
      );
    }
  }

  private static void validateMapperMethodProcedures(DASourceClass sourceClass)
      throws ValidationError {
    for (DAMethod daMethod : from(sourceClass.getMethods()).filter(isMapperMethod())) {
      if ((daMethod.getReturnType() == null || daMethod.getReturnType().getKind() == DATypeKind.VOID)
          && daMethod.getParameters().size() < 2) {
        throw new ValidationError(
            "A mapper method returning void must have at least two arguments",
            sourceClass, daMethod, null
        );
      }
    }
  }
}
