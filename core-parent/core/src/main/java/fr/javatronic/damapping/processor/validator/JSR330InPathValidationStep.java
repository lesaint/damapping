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

import fr.javatronic.damapping.processor.ProcessorClasspathChecker;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.util.Optional;

import javax.annotation.Nonnull;

/**
 * JSR330InPathValidationStep - If DASoureClass has annotation @Injectable, makes sur JSR-330 annotations are available
 * in classpath.
 *
 * @author Sébastien Lesaint
 */
public class JSR330InPathValidationStep implements ValidationStep {
  private final ProcessorClasspathChecker classpathChecker;

  public JSR330InPathValidationStep(ProcessorClasspathChecker classpathChecker) {
    this.classpathChecker = classpathChecker;
  }

  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    Optional<DAAnnotation> injectableAnnotation = sourceClass.getInjectableAnnotation();
    if (injectableAnnotation.isPresent() && !classpathChecker.isJSR330Present()) {
      throw new ValidationError(
          "Class annotated with @Mapper and @Injectable requires JSR 330's annotations (@Named, @Inject, ...) to be " +
              "available in classpath",
          sourceClass, null, injectableAnnotation.get()
      );
    }
  }
}
