/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.util.Predicate;

import javax.annotation.Nullable;

/**
 * DAInterfacePredicates -
 *
 * @author Sébastien Lesaint
 */
public final class DAInterfacePredicates {
  private DAInterfacePredicates() {
    // prevents instantiation
  }

  private static enum GuavaFunction implements Predicate<DAInterface> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAInterface daInterface) {
      return daInterface != null && daInterface.isGuavaFunction();
    }
  }

  public static Predicate<DAInterface> isGuavaFunction() {
    return GuavaFunction.INSTANCE;
  }
}
