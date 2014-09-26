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
package fr.javatronic.damapping.test.guava;

import fr.javatronic.damapping.annotation.Mapper;

import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * WildcardGenerics - Demonstrates support for a class annotated with {@code @Mapper} which declares no default
 * constructor but a constructor with a single parameter which is itself a generate Mapper interface.
 * <p>
 * This coding structure (ie. a dedicated class using a generated Mapper interface) is the basic pattern for mapping
 * trees of beans.
 * </p>
 * This class also demonstrates how annotations(s) on constructor paramters are also present on the generated
 * MapperImpl constructor.
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class ConstructorWithMapperAsParameter implements Function<Integer, String> {
  private final NonPublicMethodsMapper nonPublicMethodsMapper;

  public ConstructorWithMapperAsParameter(@Nullable NonPublicMethodsMapper nonPublicMethodsMapper) {
    this.nonPublicMethodsMapper = nonPublicMethodsMapper;
  }

  @Nullable
  @Override
  public String apply(@Nullable Integer input) {
    return "resultDoesNotMatter";
  }
}
