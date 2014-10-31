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

import fr.javatronic.damapping.annotation.Injectable;
import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.test.implicitemappermethod.UsePackageTypeInjectableMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * UseMapperFromOtherPackageFunction - Demonstrate how a Mapper implementing Guava's {@link Function} and annotated with
 * {@link Injectable} can reference a Mapper generated from a dedicated class in another package.
 * The generated MapperImpl class is expected to have a constructor annotated with {@link javax.inject.Inject}.
 *
 * @author Sébastien Lesaint
 */
@Mapper
@Injectable
public class UseMapperFromOtherPackageFunction implements Function<Boolean, String> {
  @Nonnull
  private final UsePackageTypeInjectableMapper usePackageTypeInjectableMapper;

  public UseMapperFromOtherPackageFunction(@Nonnull UsePackageTypeInjectableMapper usePackageTypeInjectableMapper) {
    this.usePackageTypeInjectableMapper = checkNotNull(usePackageTypeInjectableMapper);
  }

  @Nullable
  @Override
  public String apply(@Nullable Boolean aBoolean) {
    return null;  //implementation does not matter
  }
}
