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

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DANameImpl;

import javax.annotation.Nonnull;
import javax.lang.model.element.Name;

/**
 * JavaxDANameFactory -
 *
 * @author Sébastien Lesaint
 */
public final class JavaxDANameFactory {
  private JavaxDANameFactory() {
    // prevents instanciation
  }

  /**
   * Crée un objet DAName à partir d'un objet Name non {@code null}
   *
   * @param name un {@link javax.lang.model.element.Name}
   *
   * @return un {@link fr.javatronic.damapping.processor.model.DAName}
   */
  @Nonnull
  public static DAName from(@Nonnull Name name) {
    return new DANameImpl(name.toString());
  }
}
