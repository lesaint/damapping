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

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;

/**
 * GenericTypeContext - This object holds the generic context of a DeclaredType.
 * <p>
 * A Context is basically a Map of the type arguments of a declaredType (if any), indexed by their type parameter name.
 * </p>
 * <p>
 * Contexts are chained as each of them has a parent unless it is the root of the chain (in which case the parent is
 * {@code null}. The parent is the GenericTypeContext of the DeclaredType of the TypeElement which declares the
 * the current DeclaredType.
 * </p>
 * <p>
 * The main point of the GenericTypeContext is to resolve a generic type to a declaredType using the
 * {@link #lookup(javax.lang.model.element.Name)} method which will recursively lookup the parent GenericTypeContext
 * following the type parameter to type arguments mapping.
 * </p>
 * <p>
 * For the following classes:
 * <pre>
 * public interface Function{@literal <}F, T> {
 *    T apply(F input);
 * }
 * public interface MyFunction{@literal <}T, V> extends Function{@literal <}T, V> {
 *
 * }
 * public abstract class SomeSpecializedFunction{@literal <}T> implements MyFunction{@literal <}String, T> {
 *
 * }
 * public class MyFunction extends SomeSpecializedFunction{@literal <}BigDecimal> {
 *    BigDecimal apply(String input);
 * }
 * </pre>
 * The GenericTypeContext will be the following (one GenericTypeContext per line, each line has the line below as a parent):
 * <table>
 *   <tr>
 *     <td>DeclaredType</td>
 *     <td>parameter argument map</td>
 *   </tr>
 *   <tr>
 *     <td>Function{@literal <}T, V></td>
 *     <td><pre>[T=V, F=T]</pre></td>
 *   </tr>
 *   <tr>
 *     <td>MyFunction{@literal <}String, T></td>
 *     <td><pre>[T=java.lang.String, V=T]</pre></td>
 *   </tr>
 *   <tr>
 *     <td>SomeSpecializedFunction{@literal <}BigDecimal></td>
 *     <td><pre>[T=java.lang.BigDecimal]</pre></td>
 *   </tr>
 * </table>
 *
 * @author Sébastien Lesaint
 */
public interface GenericTypeContext {

  /**
   * The map of TypeArgument indexed by type parameter names.
   */
  @Nonnull
  Map<Name, TypeArgument> getTypeArguments();

  /**
   * Looks for the type argument for the specified type parameter name (if any).
   *
   * @param typeParameterName a {@link Name}
   *
   * @return a {@link TypeArgument} or {@code null}
   */
  @Nullable
  TypeArgument lookup(@Nonnull Name typeParameterName);

  /**
   * Returns the parent {@link GenericTypeContext} (if any).
   *
   * @return a {@link GenericTypeContext}
   */
  @Nullable
  GenericTypeContext getParent();

  /**
   * Creates a new {@link GenericTypeContext} for the specified {@link DeclaredType} with the current
   * {@link GenericTypeContext} as a parent.
   *
   * @param declaredType a {@link DeclaredType}
   *
   * @return a new {@link GenericTypeContext}
   */
  @Nonnull
  GenericTypeContext subContext(@Nonnull DeclaredType declaredType);
}
