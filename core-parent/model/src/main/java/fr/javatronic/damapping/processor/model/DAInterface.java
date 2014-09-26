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

import fr.javatronic.damapping.processor.model.visitor.DAModelVisitable;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * DAInterface - Représente un type Interface implémenté par une classe ou étendue par une autre interface.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAInterface implements DAModelVisitable {
  @Nonnull
  private final DAType type;

  public DAInterface(DAType type) {
    this.type = type;
  }

  @Nonnull
  public DAType getType() {
    return type;
  }

  @Override
  public void accept(DAModelVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAInterface that = (DAInterface) o;

    if (!type.equals(that.type)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }
}
