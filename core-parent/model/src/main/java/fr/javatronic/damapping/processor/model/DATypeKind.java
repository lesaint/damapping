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


/**
 * Fork of {@link javax.lang.model.type.TypeKind} - The kind of a type mirror.
 * <p>
 *   Note that it is possible additional type kinds will be added to
 * accommodate new, currently unknown, language structures added to
 * future versions of the Java&trade; programming language.
 * </p>
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @author Peter von der Ah&eacute;
 * @see javax.lang.model.type.TypeMirror
 * @since 1.6
 */
public enum DATypeKind {
  /**
   * The primitive type {@code boolean}.
   */
  BOOLEAN,

  /**
   * The primitive type {@code byte}.
   */
  BYTE,

  /**
   * The primitive type {@code short}.
   */
  SHORT,

  /**
   * The primitive type {@code int}.
   */
  INT,

  /**
   * The primitive type {@code long}.
   */
  LONG,

  /**
   * The primitive type {@code char}.
   */
  CHAR,

  /**
   * The primitive type {@code float}.
   */
  FLOAT,

  /**
   * The primitive type {@code double}.
   */
  DOUBLE,

  /**
   * The pseudo-type corresponding to the keyword {@code void}.
   *
   * @see javax.lang.model.type.NoType
   */
  VOID,

  /**
   * A pseudo-type used where no actual type is appropriate.
   *
   * @see javax.lang.model.type.NoType
   */
  NONE,

  /**
   * The null type.
   */
  NULL,

  /**
   * A class or interface type.
   */
  DECLARED,

  /**
   * A class or interface type that could not be resolved.
   */
  ERROR,

  /**
   * A type variable.
   */
  TYPEVAR,

  /**
   * A wildcard type argument.
   */
  WILDCARD,

  /**
   * A pseudo-type corresponding to a package element.
   *
   * @see javax.lang.model.type.NoType
   */
  PACKAGE,

  /**
   * A method, constructor, or initializer.
   */
  EXECUTABLE,

  /**
   * An implementation-reserved type.
   * This is not the type you are looking for.
   */
  OTHER,

  /**
   * A union type.
   *
   * @since 1.7
   */
  UNION;

  /**
   * Returns {@code true} if this kind corresponds to a primitive
   * type and {@code false} otherwise.
   *
   * @return {@code true} if this kind corresponds to a primitive type
   */
  public boolean isPrimitive() {
    switch (this) {
      case BOOLEAN:
      case BYTE:
      case SHORT:
      case INT:
      case LONG:
      case CHAR:
      case FLOAT:
      case DOUBLE:
        return true;

      default:
        return false;
    }
  }
}

