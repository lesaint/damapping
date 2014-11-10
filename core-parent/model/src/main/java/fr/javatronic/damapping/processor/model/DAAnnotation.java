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
package fr.javatronic.damapping.processor.model;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;

/**
 * DAAnnotation -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAAnnotation {
  @Nonnull
  private final DAType type;
  @Nonnull
  private final List<DAAnnotation> annotations;

  public DAAnnotation(@Nonnull DAType type) {
    this(type, null);
  }

  public DAAnnotation(@Nonnull DAType type, @Nullable List<DAAnnotation> annotations) {
    this.type = type;
    this.annotations = nonNullFrom(annotations);
  }

  @Nonnull
  public DAType getType() {
    return type;
  }

  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAAnnotation that = (DAAnnotation) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }
}
