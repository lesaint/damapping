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
import fr.javatronic.damapping.test.guava.SimpleMapper;

import javax.annotation.Nonnull;

/**
 * ConstructorWithMultipleMapperParameters - Demonstrates support for:
 * <ul>
 *   <li>multiple generated types as parameter of a class annotated with @Mapper</li>
 *   <li>only specific constructor parameter having annotations</li>
 * </ul>
 * in the context of a mapper with an implicite mapper method.
 */
@Mapper
public class ConstructorWithMultipleMapperParameters {
  private final Dummy_1Mapper dummy_1Mapper;
  private final SimpleMapper simpleMapper;

  public ConstructorWithMultipleMapperParameters(Dummy_1Mapper dummy_1Mapper,
                                                 @Nonnull SimpleMapper simpleMapper) {
    this.dummy_1Mapper = dummy_1Mapper;
    this.simpleMapper = simpleMapper;
  }

  protected String fooBarDonut(Integer input) {
    return null; // content doesn't matter
  }

}
