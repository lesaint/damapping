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
package fr.javatronic.damapping.test.mappermethod;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.test.mappermethod.subpackage.OutOfPackage;
import fr.javatronic.damapping.util.Optional;

import java.math.BigDecimal;

/**
 * This mapper is designed to test support for a mapper method next to non-public methods.
 */
@Mapper
public class CustomTypesAndGenerics {

  public InPackage fooBarDonut(Optional<? extends OutOfPackage> optional) {
    return null; // content doesn't matter
  }

  protected String map(Boolean a) {
    return a.toString();
  }

  String map(BigDecimal b) {
    return b.toString();
  }

  private void method_a(String a_param) {
    // content does not matter
  }

}
