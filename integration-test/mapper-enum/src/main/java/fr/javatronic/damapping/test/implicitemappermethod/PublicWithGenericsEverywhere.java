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
package fr.javatronic.damapping.test.implicitemappermethod;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicate;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * This mapper is designed to test:
 * <ul>
 *   <li>support for generics and array in mapper method</li>
 *   <li>support for implicite mapper method with keyword public</li>
 *   <li>support for implicite mapper method with the same name as one of the implicite methds of the Enum type (values)</li>
 * </ul>
 */
@Mapper
public enum PublicWithGenericsEverywhere {
  INSTANCE;

  public Collection<Predicate<Map<Long, File>>> values(@Nullable Optional<Integer[]> input) {
    return Collections.emptyList();
  }
}
