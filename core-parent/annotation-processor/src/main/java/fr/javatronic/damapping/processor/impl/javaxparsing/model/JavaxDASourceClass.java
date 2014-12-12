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
import fr.javatronic.damapping.processor.model.DAEnumValue;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.InstantiationType;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;
import fr.javatronic.damapping.util.Optional;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.lang.model.element.TypeElement;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * JavaxDASourceClass -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class JavaxDASourceClass implements DASourceClass {
  @Nonnull
  private final DASourceClass delegate;
  @Nonnull
  private final TypeElement classElement;

  public JavaxDASourceClass(@Nonnull DASourceClass delegate, @Nonnull TypeElement classElement) {
    this.delegate = checkNotNull(delegate);
    this.classElement = checkNotNull(classElement);
  }

  @Nonnull
  public TypeElement getClassElement() {
    return classElement;
  }

  @Override
  @Nonnull
  public DAType getType() {
    return delegate.getType();
  }

  @Override
  @Nullable
  public DAName getPackageName() {
    return delegate.getPackageName();
  }

  @Override
  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return delegate.getAnnotations();
  }

  @Override
  @Nonnull
  public Optional<DAAnnotation> getInjectableAnnotation() {
    return delegate.getInjectableAnnotation();
  }

  @Override
  @Nonnull
  public Set<DAModifier> getModifiers() {
    return delegate.getModifiers();
  }

  @Override
  @Nonnull
  public List<DAInterface> getInterfaces() {
    return delegate.getInterfaces();
  }

  @Override
  @Nonnull
  public List<DAMethod> getMethods() {
    return delegate.getMethods();
  }

  @Override
  @Nonnull
  public List<DAMethod> getAccessibleConstructors() {
    return delegate.getAccessibleConstructors();
  }

  @Override
  @Nonnull
  public List<DAEnumValue> getEnumValues() {
    return delegate.getEnumValues();
  }

  @Override
  @Nonnull
  public InstantiationType getInstantiationType() {
    return delegate.getInstantiationType();
  }

  @Override
  public void accept(DAModelVisitor visitor) {
    delegate.accept(visitor);
  }
}
