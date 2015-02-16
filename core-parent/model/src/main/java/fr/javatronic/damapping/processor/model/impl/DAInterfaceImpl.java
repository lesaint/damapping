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
package fr.javatronic.damapping.processor.model.impl;

import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * DAInterfaceImpl - Implementation of DAInterface as a immutable object.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAInterfaceImpl implements DAInterface {
  @Nonnull
  private final DAType type;

  public DAInterfaceImpl(DAType type) {
    this.type = type;
  }

  @Override
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

    DAInterfaceImpl that = (DAInterfaceImpl) o;

    if (!type.equals(that.type)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  @Override
  public String toString() {
    return "DAInterfaceImpl{" + type + '}';
  }
}
