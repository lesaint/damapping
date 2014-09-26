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

import javax.annotation.concurrent.Immutable;

/**
 * DAAnnotation -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAAnnotation {
  private final DAType type;

  public DAAnnotation(DAType type) {
    this.type = type;
  }

  public DAType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAAnnotation that = (DAAnnotation) o;
    if (type == null ? that.type != null : !type.equals(that.type)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return type != null ? type.hashCode() : 0;
  }
}
