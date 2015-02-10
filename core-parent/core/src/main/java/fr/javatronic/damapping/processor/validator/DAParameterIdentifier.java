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

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Function;

import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * Represents the identifier of a DAParameter annotated with
 * {@link fr.javatronic.damapping.annotation.MapperDependency} as far as we need to know to make sure all methods
 * are using the same parameters annotated with {@link fr.javatronic.damapping.annotation.MapperDependency}: ie.
 * the name and the type must be the same.
 */
class DAParameterIdentifier {
  @Nonnull
  private final DAType type;
  @Nonnull
  private final DAName name;

  public DAParameterIdentifier(@Nonnull DAType type, @Nonnull DAName name) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAParameterIdentifier that = (DAParameterIdentifier) o;
    return name.equals(that.name) && type.equals(that.type);
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + name.hashCode();
    return result;
  }

  public static Function<DAParameter, DAParameterIdentifier> fromDAParameter() {
    return DAParameterToDAParameterIdentifier.INSTANCE;
  }

  private static enum DAParameterToDAParameterIdentifier implements Function<DAParameter, DAParameterIdentifier> {
    INSTANCE;

    @Nonnull
    @Override
    public DAParameterIdentifier apply(@Nonnull DAParameter daParameter) {
      return new DAParameterIdentifier(daParameter.getType(), daParameter.getName());
    }
  }
}
