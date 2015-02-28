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
package fr.javatronic.damapping.processor.impl.javaxparsing.type;

import fr.javatronic.damapping.processor.impl.javaxparsing.TypeUtils;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * TypeUtils - Implementation of TypeUtils which delegates functions copied from {@link javax.lang.model.util.Types}
 * to a {@link javax.lang.model.util.Types} instance.
 *
 * @author Sébastien Lesaint
 */
public class TypeUtilsImpl implements TypeUtils {
  @Nonnull
  private final Types types;

  public TypeUtilsImpl(@Nonnull Types types) {
    this.types = checkNotNull(types);
  }

  @Override
  @Nullable
  public DeclaredType asDeclaredType(TypeMirror t) {
    return t.accept(AsDeclaredTypeTypeVisitor.INSTANCE, null);
  }

  private static class AsDeclaredTypeTypeVisitor extends SimpleTypeVisitor6<DeclaredType, Object> {
    public static final AsDeclaredTypeTypeVisitor INSTANCE = new AsDeclaredTypeTypeVisitor();

    private AsDeclaredTypeTypeVisitor() {
      // prevents instantiation
    }

    @Override
    public DeclaredType visitDeclared(DeclaredType t, Object o) {
      return t;
    }
  }

  @Override
  @Nonnull
  public List<TypeMirror> extractClassHierarchyAsList(TypeElement classElement, boolean excludeJavaLang) {
    return classElement.asType().accept(SuperTypeElementsVisitor.getInstance(excludeJavaLang), new ArrayList<TypeMirror>());
  }

  /**
   * A ElementVisitor that will builds the list of class in a class hierarchy, as TypeElement objects, of any
   * TypeElement.
   * <p>
   * The list starts with the visited TypeElement and respects the order of the hierarchyTypeElement.
   * </p>
   * <p>
   * Optionally, classes from {@code java.lang} package and its subpackages can be excluded (ie.
   * {@link java.lang.Object}, {@link java.lang.Enum}). In such case, the returned list can be empty when the visited
   * TypeElement belongs to the theses packages.
   * </p>
   */
  private static class SuperTypeElementsVisitor extends SimpleTypeVisitor6<List<TypeMirror>, List<TypeMirror>> {
    private static final SuperTypeElementsVisitor WITH_JAVA_LANG = new SuperTypeElementsVisitor(false);
    private static final SuperTypeElementsVisitor WITHOUT_JAVA_LANG = new SuperTypeElementsVisitor(true);

    public static SuperTypeElementsVisitor getInstance(boolean excludeJavaLang) {
      return excludeJavaLang ? WITHOUT_JAVA_LANG : WITH_JAVA_LANG;
    }

    private final boolean excludeJavaLang;

    private SuperTypeElementsVisitor(boolean excludeJavaLang) {
      this.excludeJavaLang = excludeJavaLang;
    }

    @Override
    public List<TypeMirror> visitDeclared(DeclaredType t, List<TypeMirror> typeMirrors) {
      TypeElement typeElement = (TypeElement) t.asElement();
      if (excludeJavaLang && typeElement.getQualifiedName().toString().startsWith("java.lang.")) {
        return typeMirrors;
      }
      TypeMirror superTypeMirror = typeElement.getSuperclass();
      typeMirrors.add(t);
      superTypeMirror.accept(this, typeMirrors);
      return typeMirrors;
    }
  }


  @Override
  public Element asElement(TypeMirror t) {
    return types.asElement(t);
  }

  @Override
  public boolean isSameType(TypeMirror t1, TypeMirror t2) {
    return types.isSameType(t1, t2);
  }

  @Override
  public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
    return types.isSubtype(t1, t2);
  }

  @Override
  public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
    return types.isAssignable(t1, t2);
  }

  @Override
  public boolean contains(TypeMirror t1, TypeMirror t2) {
    return types.contains(t1, t2);
  }

  @Override
  public boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
    return types.isSubsignature(m1, m2);
  }

  @Override
  public List<? extends TypeMirror> directSupertypes(TypeMirror t) {
    return types.directSupertypes(t);
  }

  @Override
  public TypeMirror erasure(TypeMirror t) {
    return types.erasure(t);
  }

  @Override
  public TypeElement boxedClass(PrimitiveType p) {
    return types.boxedClass(p);
  }

  @Override
  public PrimitiveType unboxedType(TypeMirror t) {
    return types.unboxedType(t);
  }

  @Override
  public TypeMirror capture(TypeMirror t) {
    return types.capture(t);
  }

  @Override
  public PrimitiveType getPrimitiveType(TypeKind kind) {
    return types.getPrimitiveType(kind);
  }

  @Override
  public NullType getNullType() {
    return types.getNullType();
  }

  @Override
  public NoType getNoType(TypeKind kind) {
    return types.getNoType(kind);
  }

  @Override
  public ArrayType getArrayType(TypeMirror componentType) {
    return types.getArrayType(componentType);
  }

  @Override
  public WildcardType getWildcardType(TypeMirror extendsBound,
                                      TypeMirror superBound) {
    return types.getWildcardType(extendsBound, superBound);
  }

  @Override
  public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
    return types.getDeclaredType(typeElem, typeArgs);
  }

  @Override
  public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem,
                                      TypeMirror... typeArgs) {
    return types.getDeclaredType(containing, typeElem, typeArgs);
  }

  @Override
  public TypeMirror asMemberOf(DeclaredType containing, Element element) {
    return types.asMemberOf(containing, element);
  }
}
