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
import fr.javatronic.damapping.annotation.MapperFactory;
import fr.javatronic.damapping.test.guava.sub.B;
import fr.javatronic.damapping.test.guava.sub.C;

import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * MultiConstructorOnBusinessTypes -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class MultiConstructorOnBusinessTypes implements Function<C, String> {
  @MapperFactory
  public MultiConstructorOnBusinessTypes(A a) {
    // implementation does not matter
  }

  @MapperFactory
  public MultiConstructorOnBusinessTypes(B b) {
    // implementation does not matter
  }

  @Nullable
  @Override
  public String apply(@Nullable C input) {
    return "doesn't matter";
  }
}
