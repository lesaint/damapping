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

import javax.annotation.Nonnull;

/**
 * DASourceClassValidator - Class responsible for validating part or all of a DASourceClass instance before processing
 * to generate classes.
 *
 * @author Sébastien Lesaint
 */
public interface DASourceClassValidator {
  /**
   * Performs validations on the specified DASourceClass instance.
   * Validations check that:
   * <ul>
   * <li>applies compilation check for illegal use of annotations @Mapper and/or @MapperFactory</li>
   * <li>verifies that the specified DASourceClass is consistent enough so that only valid types will be generated</li>
   * </ul>
   *
   * @param sourceClass a {@link fr.javatronic.damapping.processor.model.DASourceClass}
   *
   * @throws ValidationError for the first failing validation
   */
  void validate(@Nonnull DASourceClass sourceClass) throws ValidationError;

  void validateInstantiationTypeRequirements(DASourceClass daSourceClass) throws ValidationError;

}
