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

import fr.javatronic.damapping.processor.model.DAAnnotationMember;
import fr.javatronic.damapping.processor.model.DAType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DAAnnotationMemberImpl - Implementation of DAAnnotationMember as an immutable object.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAAnnotationMemberImpl implements DAAnnotationMember {
  @Nonnull
  private final String name;
  @Nonnull
  private final DAType type;
  @Nonnull
  private final String value;

  public DAAnnotationMemberImpl(@Nonnull String name, @Nonnull DAType type, @Nonnull String value) {
    this.name = checkNotNull(name);
    this.type = checkNotNull(type);
    this.value = checkNotNull(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nonnull
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nonnull
  public DAType getType() {
    return type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nonnull
  public String getValue() {
    return value;
  }
}
