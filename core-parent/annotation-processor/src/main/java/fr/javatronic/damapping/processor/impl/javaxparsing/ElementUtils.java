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

import fr.javatronic.damapping.processor.impl.javaxparsing.element.ElementImports;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * ElementUtils - This interface provide utility methods to work with {@link Element} objects.
 * <p>
 * Some method may require to be implemented with code specific to each supported compilers. Most of them are copied
 * from {@link javax.lang.model.util.Elements}.
 * </p>
 *
 * @author Sébastien Lesaint
 */
public interface ElementUtils {

  /**
   * Builds up the list of explicit and implicit imports for the specified {@link Element}
   *
   * @param e a {@link Element}
   *
   * @return a {@link fr.javatronic.damapping.processor.impl.javaxparsing.element.ElementImports}
   *
   * @throws IOException implementation of this method may require to perform ressources access operations which may
   * raise {@link IOException}
   */
  @Nonnull
  ElementImports findImports(@Nonnull Element e) throws IOException;

  /**
   * Returns the specified {@link Element} as a {@link TypeElement} if applicable.
   *
   * @param element a {@link Element}
   *
   * @return a {@link TypeElement} or {@code null}
   */
  @Nullable
  TypeElement asTypeElement(@Nonnull Element element);

  //###############################
  // methods copied from Elements #
  //###############################

  /**
   * Returns a package given its fully qualified name.
   *
   * @param name  fully qualified package name, or "" for an unnamed package
   * @return the named package, or {@code null} if it cannot be found
   */
  PackageElement getPackageElement(CharSequence name);

  /**
   * Returns a type element given its canonical name.
   *
   * @param name  the canonical name
   * @return the named type element, or {@code null} if it cannot be found
   */
  TypeElement getTypeElement(CharSequence name);

  /**
   * Returns the values of an annotation's elements, including defaults.
   *
   * @see javax.lang.model.element.AnnotationMirror#getElementValues()
   * @param a  annotation to examine
   * @return the values of the annotation's elements, including defaults
   */
  Map<? extends ExecutableElement, ? extends AnnotationValue>
  getElementValuesWithDefaults(AnnotationMirror a);

  /**
   * Returns the text of the documentation (&quot;Javadoc&quot;)
   * comment of an element.
   *
   * <p> A documentation comment of an element is a comment that
   * begins with "{@code /**}" , ends with a separate
   * "<code>*&#47;</code>", and immediately precedes the element,
   * ignoring white space.  Therefore, a documentation comment
   * contains at least three"{@code *}" characters.  The text
   * returned for the documentation comment is a processed form of
   * the comment as it appears in source code.  The leading "{@code
   * /**}" and trailing "<code>*&#47;</code>" are removed.  For lines
   * of the comment starting after the initial "{@code /**}",
   * leading white space characters are discarded as are any
   * consecutive "{@code *}" characters appearing after the white
   * space or starting the line.  The processed lines are then
   * concatenated together (including line terminators) and
   * returned.
   *
   * @param e  the element being examined
   * @return the documentation comment of the element, or {@code null}
   *          if there is none
   * @jls 3.6 White Space
   */
  String getDocComment(Element e);

  /**
   * Returns {@code true} if the element is deprecated, {@code false} otherwise.
   *
   * @param e  the element being examined
   * @return {@code true} if the element is deprecated, {@code false} otherwise
   */
  boolean isDeprecated(Element e);

  /**
   * Returns the <i>binary name</i> of a type element.
   *
   * @param type  the type element being examined
   * @return the binary name
   *
   * @see TypeElement#getQualifiedName
   * @jls 13.1 The Form of a Binary
   */
  Name getBinaryName(TypeElement type);


  /**
   * Returns the package of an element.  The package of a package is
   * itself.
   *
   * @param type the element being examined
   * @return the package of an element
   */
  PackageElement getPackageOf(Element type);

  /**
   * Returns all members of a type element, whether inherited or
   * declared directly.  For a class the result also includes its
   * constructors, but not local or anonymous classes.
   *
   * <p>Note that elements of certain kinds can be isolated using
   * methods in {@link javax.lang.model.util.ElementFilter}.
   *
   * @param type  the type being examined
   * @return all members of the type
   * @see Element#getEnclosedElements
   */
  List<? extends Element> getAllMembers(TypeElement type);

  /**
   * Returns all annotations <i>present</i> on an element, whether
   * directly present or present via inheritance.
   *
   * @param e  the element being examined
   * @return all annotations of the element
   * @see Element#getAnnotationMirrors
   * @see javax.lang.model.AnnotatedConstruct
   */
  List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e);

  /**
   * Tests whether one type, method, or field hides another.
   *
   * @param hider   the first element
   * @param hidden  the second element
   * @return {@code true} if and only if the first element hides
   *          the second
   */
  boolean hides(Element hider, Element hidden);

  /**
   * Tests whether one method, as a member of a given type,
   * overrides another method.
   * When a non-abstract method overrides an abstract one, the
   * former is also said to <i>implement</i> the latter.
   *
   * <p> In the simplest and most typical usage, the value of the
   * {@code type} parameter will simply be the class or interface
   * directly enclosing {@code overrider} (the possibly-overriding
   * method).  For example, suppose {@code m1} represents the method
   * {@code String.hashCode} and {@code m2} represents {@code
   * Object.hashCode}.  We can then ask whether {@code m1} overrides
   * {@code m2} within the class {@code String} (it does):
   *
   * <blockquote>
   * {@code assert elements.overrides(m1, m2,
   *          elements.getTypeElement("java.lang.String")); }
   * </blockquote>
   *
   * A more interesting case can be illustrated by the following example
   * in which a method in type {@code A} does not override a
   * like-named method in type {@code B}:
   *
   * <blockquote>
   * {@code class A { public void m() {} } }<br>
   * {@code interface B { void m(); } }<br>
   * ...<br>
   * {@code m1 = ...;  // A.m }<br>
   * {@code m2 = ...;  // B.m }<br>
   * {@code assert ! elements.overrides(m1, m2,
   *          elements.getTypeElement("A")); }
   * </blockquote>
   *
   * When viewed as a member of a third type {@code C}, however,
   * the method in {@code A} does override the one in {@code B}:
   *
   * <blockquote>
   * {@code class C extends A implements B {} }<br>
   * ...<br>
   * {@code assert elements.overrides(m1, m2,
   *          elements.getTypeElement("C")); }
   * </blockquote>
   *
   * @param overrider  the first method, possible overrider
   * @param overridden  the second method, possibly being overridden
   * @param type   the type of which the first method is a member
   * @return {@code true} if and only if the first method overrides
   *          the second
   * @jls 8.4.8 Inheritance, Overriding, and Hiding
   * @jls 9.4.1 Inheritance and Overriding
   */
  boolean overrides(ExecutableElement overrider, ExecutableElement overridden,
                    TypeElement type);

  /**
   * Returns the text of a <i>constant expression</i> representing a
   * primitive value or a string.
   * The text returned is in a form suitable for representing the value
   * in source code.
   *
   * @param value  a primitive value or string
   * @return the text of a constant expression
   * @throws IllegalArgumentException if the argument is not a primitive
   *          value or string
   *
   * @see javax.lang.model.element.VariableElement#getConstantValue()
   */
  String getConstantExpression(Object value);

  /**
   * Prints a representation of the elements to the given writer in
   * the specified order.  The main purpose of this method is for
   * diagnostics.  The exact format of the output is <em>not</em>
   * specified and is subject to change.
   *
   * @param w the writer to print the output to
   * @param elements the elements to print
   */
  void printElements(java.io.Writer w, Element... elements);

  /**
   * Return a name with the same sequence of characters as the
   * argument.
   *
   * @param cs the character sequence to return as a name
   * @return a name with the same sequence of characters as the argument
   */
  Name getName(CharSequence cs);

}
