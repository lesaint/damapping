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
package fr.javatronic.damapping.processor.impl.javaxparsing.model;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAAnnotationMember;
import fr.javatronic.damapping.processor.model.DAType;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.lang.model.element.AnnotationMirror;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * JavaxDAAnnotation -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class JavaxDAAnnotation implements DAAnnotation{
  @Nonnull
  private final DAAnnotation delegate;
  @Nonnull
  private final AnnotationMirror annotationMirror;

  public JavaxDAAnnotation(@Nonnull DAAnnotation delegate, @Nonnull AnnotationMirror annotationMirror) {
    this.delegate = checkNotNull(delegate);
    this.annotationMirror = checkNotNull(annotationMirror);
  }

  @Nonnull
  public AnnotationMirror getAnnotationMirror() {
    return annotationMirror;
  }

  @Override
  @Nonnull
  public DAType getType() {
    return delegate.getType();
  }

  @Override
  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return delegate.getAnnotations();
  }

  @Override
  @Nonnull
  public List<DAAnnotationMember> getAnnotationMembers() {
    return delegate.getAnnotationMembers();
  }
}
