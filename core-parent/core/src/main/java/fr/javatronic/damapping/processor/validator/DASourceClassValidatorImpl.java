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

import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Preconditions;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * DASourceClassValidator -
 * <p>
 * TODO add unit test coverage for DASourceClassValidatorImpl
 *
 * @author Sébastien Lesaint
 */
public class DASourceClassValidatorImpl implements DASourceClassValidator {
  private final List<ValidationStep> steps;

  public DASourceClassValidatorImpl() {
    this.steps = Lists.of(
            new MapperAnnotationValidationStep(),
            new ClassModifiersValidationStep(),
            new MapperMethodsValidationStep(),
            new MapperFactoryMethodsValidationStep(),
            new JSR330InPathValidationStep(),
            new ConstructorValidationStep(),
            new EnumValidationStep()
        );
  }

  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    Preconditions.checkNotNull(sourceClass);

    for (ValidationStep step : steps) {
      step.validate(sourceClass);
    }

    // retrieve instantiation type from @Mapper annotation
    //  - CONSTRUCTOR : validate public/protected default constructor exists sinon erreur de compilation
    //  - SINGLETON_ENUM : validate @Mapper class is an enum + validate there is only one value sinon erreur de
    // compilation
    validateInstantiationTypeRequirements(sourceClass);
  }

  @Override
  public void validateInstantiationTypeRequirements(DASourceClass daSourceClass) throws ValidationError {
    new ConstructorValidationStep().validate(daSourceClass);
    new EnumValidationStep().validate(daSourceClass);
  }

}
