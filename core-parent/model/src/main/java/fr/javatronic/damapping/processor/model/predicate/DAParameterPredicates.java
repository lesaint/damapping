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
package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.util.Predicate;

import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * DAParameterPredicates -
 *
 * @author Sébastien Lesaint
 */
public final class DAParameterPredicates {
  private DAParameterPredicates() {
    // prevents instantiation
  }

  /**
   * Indicate whether the specified parameter has the {@link fr.javatronic.damapping.annotation.MapperDependency}
   * annotation.
   */
  public static Predicate<DAParameter> hasMapperDependencyAnnotation() {
    return HasMapperDependencyAnnotation.INSTANCE;
  }

  private static enum HasMapperDependencyAnnotation implements Predicate<DAParameter> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAParameter daParameter) {
      return daParameter != null
          && from(daParameter.getAnnotations())
          .firstMatch(DAAnnotationPredicates.isMapperDependency())
          .isPresent();
    }
  }
}
