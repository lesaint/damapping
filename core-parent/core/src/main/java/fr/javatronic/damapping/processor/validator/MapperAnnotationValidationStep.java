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

import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperAnnotationValidationStep - Makes sure there is one and only one Mapper annotation on DASourceClass.
 *
 * @author Sébastien Lesaint
 */
class MapperAnnotationValidationStep implements ValidationStep {
  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    List<DAAnnotation> mapperAnnotations = from(sourceClass.getAnnotations())
        .filter(DAAnnotationPredicates.isMapper())
        .toList();
    if (mapperAnnotations.size() > 1) {
      throw new ValidationError("Mapper with more than one @Mapper annotation is not supported", sourceClass, null,
          mapperAnnotations.get(1)
      );
    }
    if (mapperAnnotations.isEmpty()) {
      throw new ValidationError("Mapper without @Mapper annotation is not supported", sourceClass, null, null);
    }
  }
}
