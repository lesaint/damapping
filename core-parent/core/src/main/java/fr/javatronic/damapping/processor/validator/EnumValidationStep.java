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
import fr.javatronic.damapping.processor.model.InstantiationType;

import javax.annotation.Nonnull;

/**
 * EnumValidationStep - Makes sure enum DASourceClass has only one value.
 *
 * @author Sébastien Lesaint
 */
public class EnumValidationStep implements ValidationStep {
  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    if (sourceClass.getInstantiationType() == InstantiationType.SINGLETON_ENUM
        && sourceClass.getEnumValues().size() != 1) {
      throw new ValidationError("Enum annoted wih @Mapper must have one value", sourceClass, null, null);
    }
  }
}
