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

import javax.annotation.Nonnull;

/**
 * DAImport - Represents an import statement in a Java source file.
 *
 * @author Sébastien Lesaint
 */
public interface DAImport extends DAElement {
  /**
   * The qualified name of the imported type, ie. a {@link DAName} object which name is {@code com.foo.Bar} for
   * {@code import com.foo.Bar}.
   *
   * @return a {@link DAName}
   */
  @Nonnull
  DAName getQualifiedName();

  /**
   * The package of the import type ie. {@code com.foo} in {@code import com.foo.Bar}.
   * <p>
   * Since it is illegal to import a Type from the unamed package, this method can not return an empty
   * String.
   * </p>
   *
   * @return a non empty {@link String}
   */
  @Nonnull
  String getPackageName();

  /**
   * The simple name of the imported type, ie. {@code Bar} in {@code import com.foo.Bar}.
   *
   * @return a non empty {@link String}
   */
  @Nonnull
  String getSimpleName();
}
