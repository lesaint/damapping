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
package fr.javatronic.damapping.test.implicitemappermethod;

import fr.javatronic.damapping.annotation.Mapper;

import javax.annotation.Nullable;

/**
 * This mapper is designed to test:
 * <ul>
 *   <li>that implicite mapper method with protected keyword are correctly supported</li>
 *   <li>support for implicite mapper method with the same name as one of the implicite methds of the Enum type (valueOf)</li>
 * </ul>
 */
@Mapper
public enum ProtectedSameNameAsImpliciteEnumMethod {
  INSTANCE;

  protected String valueOf(@Nullable Integer input) {
    return null; // implementation does not matter here
  }
}
