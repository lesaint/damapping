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

import fr.javatronic.damapping.processor.model.DAName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.util.Preconditions.checkArgument;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DANameImpl - Implementation of DAName as an immutable object.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DANameImpl implements DAName {
  @Nonnull
  private final String name;

  /**
   * Creates a new DANameImpl instance with the specified name value.
   *
   * @param name a {@link String}
   *
   * @throws IllegalArgumentException if the specified String is empty
   */
  public DANameImpl(@Nonnull String name) {
    this.name = checkNotNull(name);
    checkArgument(!name.isEmpty(), "DAName can not be empty");
  }

  @Override
  @Nonnull
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(@Nullable String packageName) {
    if (packageName == null || packageName.isEmpty()) {
      return false;
    }
    return name.equals(packageName);
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
  public CharSequence subSequence(int start, int end) {
    return name.subSequence(start, end);
  }

  @Nonnull
  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(DAName o) {
    return name.compareTo(o.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return name.equals(((DANameImpl) o).name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
