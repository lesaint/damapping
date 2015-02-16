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
package fr.javatronic.damapping.processor.impl.javaxparsing;

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

/**
 * TypeUtils - Provides utility methods for classes from the {@code javax.lang.model.type} package.
 * <p>
 * Most of them are copied from {@link javax.lang.model.util.Types} but a few are specific to DAMapping.
 * </p>
 *
 * @author Sébastien Lesaint
 */
public interface TypeUtils {

  /**
   * Casts the specified TypeMirror to a DeclaredType when it applies, otherwise returns {@code null}.
   *
   * @param t a {@link javax.lang.model.type.TypeMirror}
   *
   * @return a {@link javax.lang.model.type.DeclaredType} or {@code null}
   */
  @Nullable
  DeclaredType asDeclaredType(TypeMirror t);

  /**
   * Creates a the class hierarchy of a specified TypeElement, including the specified classElement as the first item,
   * and optionally excluding TypeElement from the {@code java.lang} packages and subpackages.
   *
   * @param classElement    a {@link javax.lang.model.element.TypeElement}
   * @param excludeJavaLang indicates whether TypeElement from {@code java.lang} packages and subpackages should be
   *                        excluded from the returned list
   *
   * @return a {@link java.util.List} of {@link javax.lang.model.type.TypeMirror}
   */
  @Nonnull
  List<TypeMirror> extractClassHierarchyAsList(TypeElement classElement, boolean excludeJavaLang);

  //############################
  // methods copied from Utils #
  //############################

  /**
   * Returns the element corresponding to a type.
   * The type may be a {@code DeclaredType} or {@code TypeVariable}.
   * Returns {@code null} if the type is not one with a
   * corresponding element.
   *
   * @param t the type to map to an element
   *
   * @return the element corresponding to the given type
   *
   * @see {@link javax.lang.model.util.Types#asElement(javax.lang.model.type.TypeMirror)}
   */
  Element asElement(TypeMirror t);

  /**
   * Tests whether two {@code TypeMirror} objects represent the same type.
   * <p/>
   * <p>Caveat: if either of the arguments to this method represents a
   * wildcard, this method will return false.  As a consequence, a wildcard
   * is not the same type as itself.  This might be surprising at first,
   * but makes sense once you consider that an example like this must be
   * rejected by the compiler:
   * <pre>
   *   {@code List<?> list = new ArrayList<Object>();}
   *   {@code list.add(list.get(0));}
   * </pre>
   * <p/>
   * <p>Since annotations are only meta-data associated with a type,
   * the set of annotations on either argument is <em>not</em> taken
   * into account when computing whether or not two {@code
   * TypeMirror} objects are the same type. In particular, two
   * {@code TypeMirror} objects can have different annotations and
   * still be considered the same.
   *
   * @param t1 the first type
   * @param t2 the second type
   *
   * @return {@code true} if and only if the two types are the same
   *
   * @see {@link javax.lang.model.util.Types#isSameType(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)}
   */
  boolean isSameType(TypeMirror t1, TypeMirror t2);

  /**
   * Tests whether one type is a subtype of another.
   * Any type is considered to be a subtype of itself.
   *
   * @param t1 the first type
   * @param t2 the second type
   *
   * @return {@code true} if and only if the first type is a subtype
   * of the second
   *
   * @throws IllegalArgumentException if given an executable or package type
   * @jls 4.10 Subtyping
   *
   * @see {@link javax.lang.model.util.Types#isSubtype(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)}
   */
  boolean isSubtype(TypeMirror t1, TypeMirror t2);

  /**
   * Tests whether one type is assignable to another.
   *
   * @param t1 the first type
   * @param t2 the second type
   *
   * @return {@code true} if and only if the first type is assignable
   * to the second
   *
   * @throws IllegalArgumentException if given an executable or package type
   * @jls 5.2 Assignment Conversion
   *
   * @see {@link javax.lang.model.util.Types#isAssignable(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)}
   */
  boolean isAssignable(TypeMirror t1, TypeMirror t2);

  /**
   * Tests whether one type argument <i>contains</i> another.
   *
   * @param t1 the first type
   * @param t2 the second type
   *
   * @return {@code true} if and only if the first type contains the second
   *
   * @throws IllegalArgumentException if given an executable or package type
   * @jls 4.5.1.1 Type Argument Containment and Equivalence
   *
   * @see {@link javax.lang.model.util.Types#contains(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)}
   */
  boolean contains(TypeMirror t1, TypeMirror t2);

  /**
   * Tests whether the signature of one method is a <i>subsignature</i>
   * of another.
   *
   * @param m1 the first method
   * @param m2 the second method
   *
   * @return {@code true} if and only if the first signature is a
   * subsignature of the second
   *
   * @jls 8.4.2 Method Signature
   *
   * @see {@link javax.lang.model.util.Types#isSubsignature(javax.lang.model.type.ExecutableType, javax.lang.model.type.ExecutableType)}
   */
  boolean isSubsignature(ExecutableType m1, ExecutableType m2);

  /**
   * Returns the direct supertypes of a type.  The interface types, if any,
   * will appear last in the list.
   *
   * @param t the type being examined
   *
   * @return the direct supertypes, or an empty list if none
   *
   * @throws IllegalArgumentException if given an executable or package type
   *
   * @see {@link javax.lang.model.util.Types#directSupertypes(javax.lang.model.type.TypeMirror)}
   */
  List<? extends TypeMirror> directSupertypes(TypeMirror t);

  /**
   * Returns the erasure of a type.
   *
   * @param t the type to be erased
   *
   * @return the erasure of the given type
   *
   * @throws IllegalArgumentException if given a package type
   * @jls 4.6 Type Erasure
   *
   * @see {@link javax.lang.model.util.Types#erasure(javax.lang.model.type.TypeMirror)}
   */
  TypeMirror erasure(TypeMirror t);

  /**
   * Returns the class of a boxed value of a given primitive type.
   * That is, <i>boxing conversion</i> is applied.
   *
   * @param p the primitive type to be converted
   *
   * @return the class of a boxed value of type {@code p}
   *
   * @jls 5.1.7 Boxing Conversion
   *
   * @see {@link javax.lang.model.util.Types#boxedClass(javax.lang.model.type.PrimitiveType)}
   */
  TypeElement boxedClass(PrimitiveType p);

  /**
   * Returns the type (a primitive type) of unboxed values of a given type.
   * That is, <i>unboxing conversion</i> is applied.
   *
   * @param t the type to be unboxed
   *
   * @return the type of an unboxed value of type {@code t}
   *
   * @throws IllegalArgumentException if the given type has no
   *                                  unboxing conversion
   * @jls 5.1.8 Unboxing Conversion
   *
   * @see {@link javax.lang.model.util.Types#unboxedType(javax.lang.model.type.TypeMirror)}
   */
  PrimitiveType unboxedType(TypeMirror t);

  /**
   * Applies capture conversion to a type.
   *
   * @param t the type to be converted
   *
   * @return the result of applying capture conversion
   *
   * @throws IllegalArgumentException if given an executable or package type
   * @jls 5.1.10 Capture Conversion
   *
   * @see {@link javax.lang.model.util.Types#capture(javax.lang.model.type.TypeMirror)}
   */
  TypeMirror capture(TypeMirror t);

  /**
   * Returns a primitive type.
   *
   * @param kind the kind of primitive type to return
   *
   * @return a primitive type
   *
   * @throws IllegalArgumentException if {@code kind} is not a primitive kind
   *
   * @see {@link javax.lang.model.util.Types#getPrimitiveType(javax.lang.model.type.TypeKind)}
   */
  PrimitiveType getPrimitiveType(TypeKind kind);

  /**
   * Returns the null type.  This is the type of {@code null}.
   *
   * @return the null type
   *
   * @see {@link javax.lang.model.util.Types#getNullType()}
   */
  NullType getNullType();

  /**
   * Returns a pseudo-type used where no actual type is appropriate.
   * The kind of type to return may be either
   * {@link TypeKind#VOID VOID} or {@link TypeKind#NONE NONE}.
   * For packages, use
   * {@link javax.lang.model.util.Elements#getPackageElement(CharSequence)}{@code .asType()}
   * instead.
   *
   * @param kind the kind of type to return
   *
   * @return a pseudo-type of kind {@code VOID} or {@code NONE}
   *
   * @throws IllegalArgumentException if {@code kind} is not valid
   *
   * @see {@link javax.lang.model.util.Types#getNoType(javax.lang.model.type.TypeKind)}
   */
  NoType getNoType(TypeKind kind);

  /**
   * Returns an array type with the specified component type.
   *
   * @param componentType the component type
   *
   * @return an array type with the specified component type.
   *
   * @throws IllegalArgumentException if the component type is not valid for
   *                                  an array
   *
   * @see {@link javax.lang.model.util.Types#getArrayType(javax.lang.model.type.TypeMirror)}
   */
  ArrayType getArrayType(TypeMirror componentType);

  /**
   * Returns a new wildcard type argument.  Either of the wildcard's
   * bounds may be specified, or neither, but not both.
   *
   * @param extendsBound the extends (upper) bound, or {@code null} if none
   * @param superBound   the super (lower) bound, or {@code null} if none
   *
   * @return a new wildcard
   *
   * @throws IllegalArgumentException if bounds are not valid
   *
   * @see {@link javax.lang.model.util.Types#getWildcardType(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)}
   */
  WildcardType getWildcardType(TypeMirror extendsBound,
                               TypeMirror superBound);

  /**
   * Returns the type corresponding to a type element and
   * actual type arguments.
   * Given the type element for {@code Set} and the type mirror
   * for {@code String},
   * for example, this method may be used to get the
   * parameterized type {@code Set<String>}.
   * <p/>
   * <p> The number of type arguments must either equal the
   * number of the type element's formal type parameters, or must be
   * zero.  If zero, and if the type element is generic,
   * then the type element's raw type is returned.
   * <p/>
   * <p> If a parameterized type is being returned, its type element
   * must not be contained within a generic outer class.
   * The parameterized type {@code Outer<String>.Inner<Number>},
   * for example, may be constructed by first using this
   * method to get the type {@code Outer<String>}, and then invoking
   * {@link #getDeclaredType(DeclaredType, TypeElement, TypeMirror...)}.
   *
   * @param typeElem the type element
   * @param typeArgs the actual type arguments
   *
   * @return the type corresponding to the type element and
   * actual type arguments
   *
   * @throws IllegalArgumentException if too many or too few
   *                                  type arguments are given, or if an inappropriate type
   *                                  argument or type element is provided
   *
   * @see {@link javax.lang.model.util.Types#getDeclaredType(javax.lang.model.element.TypeElement, javax.lang.model.type.TypeMirror...)}
   */
  DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs);

  /**
   * Returns the type corresponding to a type element
   * and actual type arguments, given a
   * {@linkplain DeclaredType#getEnclosingType() containing type}
   * of which it is a member.
   * The parameterized type {@code Outer<String>.Inner<Number>},
   * for example, may be constructed by first using
   * {@link #getDeclaredType(TypeElement, TypeMirror...)}
   * to get the type {@code Outer<String>}, and then invoking
   * this method.
   * <p/>
   * <p> If the containing type is a parameterized type,
   * the number of type arguments must equal the
   * number of {@code typeElem}'s formal type parameters.
   * If it is not parameterized or if it is {@code null}, this method is
   * equivalent to {@code getDeclaredType(typeElem, typeArgs)}.
   *
   * @param containing the containing type, or {@code null} if none
   * @param typeElem   the type element
   * @param typeArgs   the actual type arguments
   *
   * @return the type corresponding to the type element and
   * actual type arguments, contained within the given type
   *
   * @throws IllegalArgumentException if too many or too few
   *                                  type arguments are given, or if an inappropriate type
   *                                  argument, type element, or containing type is provided
   *
   * @see {@link javax.lang.model.util.Types#getDeclaredType(javax.lang.model.type.DeclaredType, javax.lang.model.element.TypeElement, javax.lang.model.type.TypeMirror...)}
   */
  DeclaredType getDeclaredType(DeclaredType containing,
                               TypeElement typeElem, TypeMirror... typeArgs);

  /**
   * Returns the type of an element when that element is viewed as
   * a member of, or otherwise directly contained by, a given type.
   * For example,
   * when viewed as a member of the parameterized type {@code Set<String>},
   * the {@code Set.add} method is an {@code ExecutableType}
   * whose parameter is of type {@code String}.
   *
   * @param containing the containing type
   * @param element    the element
   *
   * @return the type of the element as viewed from the containing type
   *
   * @throws IllegalArgumentException if the element is not a valid one
   *                                  for the given type
   *
   * @see {@link javax.lang.model.util.Types#asMemberOf(javax.lang.model.type.DeclaredType, javax.lang.model.element.Element)}
   */
  TypeMirror asMemberOf(DeclaredType containing, Element element);
}
