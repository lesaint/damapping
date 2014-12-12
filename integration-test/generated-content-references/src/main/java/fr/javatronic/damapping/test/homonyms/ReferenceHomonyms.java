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
package fr.javatronic.damapping.test.homonyms;

import fr.javatronic.damapping.annotation.Mapper;

/**
 * ReferenceHomonyms - Dedicated class demonstrating support for dedicated classes with references to generated types
 * with the same names.
 * Here, we reference Mapper interfaces for {@code ClassB} in the current package and {@code ClassB} in package
 * {@code mapperreferences}.
 * We chose to not make an import (except the implicit one of types from the current package) and use qualified
 * references for the {@code ClassBMapper} from package {@code mapperreferences}.
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class ReferenceHomonyms {
  private final ClassBMapper samePackageClassB;
  private final fr.javatronic.damapping.test.mapperreferences.ClassBMapper otherPackageClassB;

  public ReferenceHomonyms(ClassBMapper samePackageClassB,
                           fr.javatronic.damapping.test.mapperreferences.ClassBMapper otherPackageClassB) {
    this.samePackageClassB = samePackageClassB;
    this.otherPackageClassB = otherPackageClassB;
  }

  public String map(Integer input) {
    return null; //implementation does not matter
  }
}
