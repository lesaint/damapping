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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FileContext -
 *
 * @author Sébastien Lesaint
 */
public interface FileContext {
  /**
   * The name of the package in which the file is created.
   * @return the name of the package, the empty String for the default package.
   */
  @Nonnull
  String getPackageName();

  /**
   * Return the writer for the current file.
   * @return the writer for the current file.
   */
  @Nonnull
  BufferedWriter getWriter();

  /**
   *
   * @param type a {@link DAType} or {@code null}
   * @return a boolean
   */
  boolean hasExpliciteImport(@Nullable DAType type);

  /**
   * Finds out whether the specified type has at least one homonymous class emough the imports.
   * @param type a {@link DAType} or {@code null}
   * @return a boolean
   */
  boolean hasHomonymousImport(@Nullable DAType type);
}
