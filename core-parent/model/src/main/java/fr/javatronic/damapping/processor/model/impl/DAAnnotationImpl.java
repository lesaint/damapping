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
package fr.javatronic.damapping.processor.model.impl;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAAnnotationMember;
import fr.javatronic.damapping.processor.model.DAType;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;

/**
 * DAAnnotationImpl - Implementation of DAAnnotation as an immutable object.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAAnnotationImpl implements DAAnnotation {
  @Nonnull
  private final DAType type;
  @Nonnull
  private final List<DAAnnotation> annotations;
  @Nonnull
  private final List<DAAnnotationMember> annotationMembers;

  public DAAnnotationImpl(@Nonnull DAType type) {
    this(type, null, null);
  }

  public DAAnnotationImpl(@Nonnull DAType type,
                          @Nullable List<DAAnnotation> annotations,
                          @Nullable List<DAAnnotationMember> annotationMembers) {
    this.type = type;
    this.annotations = nonNullFrom(annotations);
    this.annotationMembers = nonNullFrom(annotationMembers);
  }

  @Override
  @Nonnull
  public DAType getType() {
    return type;
  }

  @Override
  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  @Nonnull
  public List<DAAnnotationMember> getAnnotationMembers() {
    return annotationMembers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAAnnotationImpl that = (DAAnnotationImpl) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }
}
