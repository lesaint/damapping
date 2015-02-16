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
package fr.javatronic.damapping.processor.impl.javaxparsing.generics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.SimpleTypeVisitor6;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * GenericTypeContextImpl - Implementation of the GenericTypeContext interface.
 *
 * @author Sébastien Lesaint
 */
public class GenericTypeContextImpl implements GenericTypeContext {
  @Nonnull
  private final DeclaredType declaredType;
  @Nonnull
  private final GenericTypeContext parentContext; // context of the implementing class or interface
  /**
   * Map of the type argument of the current declaredType (if any), indexed by their type parameter name.
   */
  @Nonnull
  private final Map<Name, TypeArgument> typeArguments;

  private GenericTypeContextImpl(@Nonnull GenericTypeContext parentContext,
                                 @Nonnull DeclaredType declaredType) {
    this.declaredType = checkNotNull(declaredType);
    this.parentContext = checkNotNull(parentContext);
    this.typeArguments = buildTypeArgumentMap(declaredType);
  }

  public static GenericTypeContext emptyContext() {
    return EmptyGenericTypeContext.EMPTY_GENERIC_TYPE_CONTEXT;
  }

  public static GenericTypeContext create(@Nonnull DeclaredType declaredType) {
    return emptyContext().subContext(declaredType);
  }

  @Nonnull
  @Override
  public GenericTypeContext subContext(@Nonnull DeclaredType declaredType) {
    return new GenericTypeContextImpl(this, declaredType);
  }

  private static Map<Name, TypeArgument> buildTypeArgumentMap(DeclaredType type) {
    Map<Name, TypeMirror> typeParameterToTypeArgumentMap = buildTypeParameterToArgumentMap(type);
    Map<Name, TypeArgument> typeParameterMap = new HashMap<>();
    for (Map.Entry<Name, TypeMirror> nameTypeMirrorEntry : typeParameterToTypeArgumentMap.entrySet()) {
      TypeMirror typeMirror = nameTypeMirrorEntry.getValue();
      typeParameterMap.put(
          nameTypeMirrorEntry.getKey(),
          type.equals(typeMirror) ? SelfTypeArgument.SELF : toTypeArgument(typeMirror)
      );
    }
    return typeParameterMap;
  }

  private static TypeArgument toTypeArgument(TypeMirror typeMirror) {
    return typeMirror.accept(toTypeArgumentVisitor, null);
  }

  private static final SimpleTypeVisitor6<TypeArgument, Void> toTypeArgumentVisitor = new SimpleTypeVisitor6<TypeArgument, Void>() {
    @Override
    public TypeArgument visitDeclared(DeclaredType t, Void o) {
      return new DeclaredTypeArgument(t);
    }

    @Override
    public TypeArgument visitTypeVariable(TypeVariable t, Void o) {
      return new VarTypeArgument(t.asElement().getSimpleName());
    }
  };

  private static Map<Name, TypeMirror> buildTypeParameterToArgumentMap(TypeMirror type) {
    Map<Name, TypeMirror> typeParameterToTypeArgumentMap = new HashMap<>();

    DeclaredType declaredType = (DeclaredType) type;
    if (declaredType.getTypeArguments().isEmpty()) {
      return typeParameterToTypeArgumentMap;
    }

    TypeElement typeElement = (TypeElement) declaredType.asElement();
    Iterator<? extends TypeMirror> typeArguments = declaredType.getTypeArguments().iterator();
    Iterator<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters().iterator();
    while (typeArguments.hasNext() && typeParameters.hasNext()) {
      typeParameterToTypeArgumentMap.put(typeParameters.next().getSimpleName(), typeArguments.next());
    }
    if (typeArguments.hasNext() != typeParameters.hasNext()) {
      throw new IllegalStateException("There isn't the same number of type argument as type parameter");
    }

    return typeParameterToTypeArgumentMap;
  }

  @Nonnull
  @Override
  public Map<Name, TypeArgument> getTypeArguments() {
    return this.typeArguments;
  }

  @Override
  @Nullable
  public TypeArgument lookup(@Nonnull Name typeParameterName) {
    checkNotNull(typeParameterName);

    TypeArgument typeArgument = typeArguments.get(typeParameterName);
    if (typeArgument != null && typeArgument instanceof VarTypeArgument) {
      return parentContext.lookup(((VarTypeArgument) typeArgument).getName());
    }
    return typeArgument;
  }

  @Override
  @Nonnull
  public GenericTypeContext getParent() {
    return this.parentContext;
  }

  /**
   * Implementation of an empty GenericTypeContext to be used as the root parent of all GenericTypeContextImpl.
   */
  private static enum EmptyGenericTypeContext implements GenericTypeContext {
    EMPTY_GENERIC_TYPE_CONTEXT;

    @Nonnull
    @Override
    public GenericTypeContext subContext(@Nonnull DeclaredType declaredType) {
      return new GenericTypeContextImpl(this, declaredType);
    }

    @Nonnull
    @Override
    public Map<Name, TypeArgument> getTypeArguments() {
      return Collections.<Name, TypeArgument>emptyMap();
    }

    @Nullable
    @Override
    public TypeArgument lookup(@Nonnull Name typeParameterName) {
      return null;
    }

    @Nullable
    @Override
    public GenericTypeContext getParent() {
      return null;
    }

  }

  @Override
  public String toString() {
    return "GenericTypeContextImpl{" +
        "type=" + declaredType +
        ", typeArguments=" + typeArguments +
        ", parentContext=" + parentContext +
        '}';
  }
}
