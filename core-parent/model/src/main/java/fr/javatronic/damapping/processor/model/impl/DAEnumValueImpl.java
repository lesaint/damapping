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

import fr.javatronic.damapping.processor.model.DAEnumValue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DAEnumValueImpl - Implementation of DAEnumValueImpl as an immutable object.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAEnumValueImpl implements DAEnumValue {
  @Nonnull
  private final String name;

  public DAEnumValueImpl(@Nonnull String name) {
    this.name = checkNotNull(name);
  }

  @Override
  @Nonnull
  public String getName() {
    return name;
  }

  @Override
  public int length() {
    return name.length();
  }

  @Override
  public char charAt(int index) {
    return name.charAt(index);
  }

  @Override
  public CharSequence subSequence(int beginIndex, int endIndex) {
    return name.subSequence(beginIndex, endIndex);
  }

  @Override
  public String toString() {
    return name.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAEnumValueImpl that = (DAEnumValueImpl) o;

    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
