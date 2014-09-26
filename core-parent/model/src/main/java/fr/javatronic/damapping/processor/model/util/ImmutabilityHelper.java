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
package fr.javatronic.damapping.processor.model.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * ImmutabilityHelper -
 *
 * @author Sébastien Lesaint
 */
public final class ImmutabilityHelper {
  private ImmutabilityHelper() {
    // prevents intantiation
  }

  public static <T> Set<T> nonNullFrom(Set<T> set) {
    if (set == null || set.isEmpty()) {
      return Collections.emptySet();
    }
    return Collections.unmodifiableSet(set);
  }

  public static <T> List<T> nonNullFrom(List<T> list) {
    if (list == null || list.isEmpty()) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(list);
  }
}
