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

import javax.annotation.Nonnull;
import javax.lang.model.element.Name;

/**
 * VarTypeArgument - A TypeArgument implementation that represents a generic type argument.
 *
 * @author Sébastien Lesaint
 */
public class VarTypeArgument implements TypeArgument {
  @Nonnull
  private final Name name;

  public VarTypeArgument(@Nonnull Name name) {
    this.name = name;
  }

  @Nonnull
  public Name getName() {
    return name;
  }

  @Override
  public String toString() {
    return name.toString();
  }
}
