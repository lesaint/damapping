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

import javax.annotation.Nonnull;

/**
 * DAInterface - Represents an interface implemented by a class or extended by an interface.
 *
 * @author Sébastien Lesaint
 */
public interface DAInterface extends DAModelVisitable {
  /**
   * The type representing the implemented/extended interface.
   *
   * @return a {@link DAType}
   */
  @Nonnull
  DAType getType();
}
