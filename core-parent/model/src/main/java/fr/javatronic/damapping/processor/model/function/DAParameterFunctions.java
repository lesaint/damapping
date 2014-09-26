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

import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.util.Function;

import javax.annotation.Nullable;

/**
 * DAParameterFunctions -
 *
 * @author Sébastien Lesaint
 */
public final class DAParameterFunctions {
  private DAParameterFunctions() {
    // prevents instantiation
  }

  public static Function<DAParameter, String> toName() {
    return DAParameterToName.INSTANCE;
  }

  private static enum DAParameterToName implements Function<DAParameter, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(DAParameter daParameter) {
      if (daParameter == null) {
        return null;
      }
      return daParameter.getName().getName();
    }
  }

  public static Function<DAParameter, String> toNamePrefixedWithThis() {
    return DAParameterToNamePrefixedWithThis.INSTANCE;
  }

  private static enum DAParameterToNamePrefixedWithThis implements Function<DAParameter, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(@Nullable DAParameter daParameter) {
      if (daParameter == null) {
        return null;
      }
      return "this." + daParameter.getName().getName();
    }
  }
}
