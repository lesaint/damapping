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
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.lang.model.element.ExecutableElement;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * JavaxDAMethod -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class JavaxDAMethod implements DAMethod {
  @Nonnull
  private final DAMethod delegate;
  @Nonnull
  private final ExecutableElement methodElement;

  public JavaxDAMethod(@Nonnull DAMethod delegate, @Nonnull ExecutableElement methodElement) {
    this.delegate = checkNotNull(delegate);
    this.methodElement = checkNotNull(methodElement);
  }

  @Nonnull
  public ExecutableElement getMethodElement() {
    return methodElement;
  }

  @Override
  @Nonnull
  public DAName getName() {
    return delegate.getName();
  }

  @Override
  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return delegate.getAnnotations();
  }

  @Override
  @Nonnull
  public Set<DAModifier> getModifiers() {
    return delegate.getModifiers();
  }

  @Override
  @Nullable
  public DAType getReturnType() {
    return delegate.getReturnType();
  }

  @Override
  @Nonnull
  public List<DAParameter> getParameters() {
    return delegate.getParameters();
  }

  @Override
  public boolean isConstructor() {
    return delegate.isConstructor();
  }

  @Override
  public boolean isMapperFactoryMethod() {
    return delegate.isMapperFactoryMethod();
  }

  @Override
  public boolean isMapperMethod() {
    return delegate.isMapperMethod();
  }

  @Override
  public boolean isGuavaFunctionApplyMethod() {
    return delegate.isGuavaFunctionApplyMethod();
  }

  @Override
  public void accept(DAModelVisitor visitor) {
    delegate.accept(visitor);
  }
}
