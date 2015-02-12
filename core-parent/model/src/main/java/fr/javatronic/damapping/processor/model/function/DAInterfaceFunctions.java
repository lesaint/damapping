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
package fr.javatronic.damapping.processor.model.function;

import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Function;

import javax.annotation.Nullable;

/**
 * DAInterfaceFunctions -
 *
 * @author Sébastien Lesaint
 */
public final class DAInterfaceFunctions {
  private DAInterfaceFunctions() {
    // prevents instantiation
  }

  /**
   * Transforms a {@link fr.javatronic.damapping.processor.model.DAInterface} into the value returned by its
   * {@link fr.javatronic.damapping.processor.model.DAInterface#getType()} method.
   */
  public static Function<DAInterface, DAType> toDAType() {
    return DAInterfaceToDAType.INSTANCE;
  }

  private static enum DAInterfaceToDAType implements Function<DAInterface, DAType> {
    INSTANCE;

    @Nullable
    @Override
    public DAType apply(@Nullable DAInterface daInterface) {
      if (daInterface == null) {
        return null;
      }
      return daInterface.getType();
    }
  }
}
