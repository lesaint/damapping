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
package fr.javatronic.damapping.processor.validator;

import fr.javatronic.damapping.processor.model.DAType;

import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * Represents the signature of a DAMethod, ie. its name, its return type and the type of its parameters (in order).
 */
class DAMethodSignature {
  @Nonnull
  private final DAType returnType;
  @Nonnull
  private final String name;
  @Nonnull
  private final List<DAType> parameterTypes;

  public DAMethodSignature(@Nonnull DAType returnType, @Nonnull String name, @Nonnull List<DAType> parameterTypes) {
    this.returnType = checkNotNull(returnType);
    this.name = checkNotNull(name);
    this.parameterTypes = checkNotNull(parameterTypes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAMethodSignature that = (DAMethodSignature) o;
    return name.equals(that.name)
        && parameterTypes.equals(that.parameterTypes)
        && returnType.equals(that.returnType);
  }

  @Override
  public int hashCode() {
    int result = returnType.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + parameterTypes.hashCode();
    return result;
  }
}
