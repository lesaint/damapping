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
import fr.javatronic.damapping.test.mapperreferences.ClassBMapper;

/**
 * ReferenceHomonyms - This class has the exact same purpose as {@link ReferenceHomonyms} and its implements differs
 * only by the fact the {@code ClassBMapper} from package {@code mapperreferences} is explicitely imported and references
 * to the {@code ClassBMapper} from the current package are made wiht qualified names.
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class ReferenceHomonyms2 {
  private final fr.javatronic.damapping.test.homonyms.ClassBMapper samePackageClassB;
  private final ClassBMapper otherPackageClassB;

  public ReferenceHomonyms2(fr.javatronic.damapping.test.homonyms.ClassBMapper samePackageClassB,
                            ClassBMapper otherPackageClassB) {
    this.samePackageClassB = samePackageClassB;
    this.otherPackageClassB = otherPackageClassB;
  }

  public String map(Integer input) {
    return null; //implementation does not matter
  }
}
