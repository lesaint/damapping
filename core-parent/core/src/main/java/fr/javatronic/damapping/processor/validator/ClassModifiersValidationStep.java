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

import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DASourceClass;

import javax.annotation.Nonnull;

/**
 * ModifiersValidationStep - Makes sure DASourceClass is not private which would make accessing the type impossible to generates types.
 *
 * @author Sébastien Lesaint
 */
public class ClassModifiersValidationStep implements ValidationStep {
  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    // retrieve qualifiers of the class with @Mapper + make validate : must be public or protected sinon erreur de
    // compilation
    if (sourceClass.getModifiers().contains(DAModifier.PRIVATE)) {
      throw new ValidationError("Class annoted with @Mapper can not be private", sourceClass, null, null);
    }
  }
}
