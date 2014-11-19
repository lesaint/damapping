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
package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.util.Predicate;

import javax.annotation.Nullable;

/**
 * DANamePredicates -
 *
 * @author Sébastien Lesaint
 */
public final class DANamePredicates {
  private DANamePredicates() {
    // prevents instantiation
  }

  public static Predicate<DAName> isJavaLangType() {
    return JavaLangDANamePredicate.INSTANCE;
  }

  private static enum JavaLangDANamePredicate implements Predicate<DAName> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAName qualifiedName) {
      return qualifiedName != null && qualifiedName.toString().startsWith("java.lang.");
    }
  }
}

