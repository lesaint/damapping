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
package fr.javatronic.damapping.test.mapperfactory;

import fr.javatronic.damapping.annotation.Injectable;
import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.annotation.MapperDependency;
import fr.javatronic.damapping.annotation.MapperFactory;

import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ConstructorFactoryInjectable - Sames as {@link ConstructorFactoryInjectable} but defines a Mapper dependency using
 * the {@link MapperDependency} annotation.
 *
 * @author Sébastien Lesaint
 */
@Mapper
@Injectable
public class ConstructorFactoryInjectableWithDependency {

  @MapperFactory
  public ConstructorFactoryInjectableWithDependency(Boolean flag, @MapperDependency @Nonnull String otherParam) {
    // implementation does not matter
  }

  @Nullable
  public String apply(@Nullable BigDecimal input) {
    return "doesn't matter";
  }

}
