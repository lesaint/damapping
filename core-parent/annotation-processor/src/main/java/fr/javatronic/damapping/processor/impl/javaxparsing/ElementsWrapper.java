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

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

/**
 * ElementsWrapper - This interface extends {@link Elements} to add methods specific to DAMapping.
 * <p>
 * These methods may be compiler specific.
 * </p>
 *
 * @author Sébastien Lesaint
 */
public interface ElementsWrapper extends Elements {

  /**
   * Builds up the list of explicite and implicite imports for the specified {@link Element}
   *
   * @param e a {@link Element}
   *
   * @return a {@link ElementImports}
   *
   * @throws IOException implementation of this method may require to perform ressources access operations which may
   * raise {@link IOException}
   */
  @Nonnull
  ElementImports findImports(@Nonnull Element e) throws IOException;

}
