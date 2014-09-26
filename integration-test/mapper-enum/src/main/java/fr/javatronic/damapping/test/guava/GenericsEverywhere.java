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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

@Mapper
public enum GenericsEverywhere implements Function<Optional<Integer[]>, Collection<Predicate<Map<Long, File>>>> {
  INSTANCE;

  @Override
  public Collection<Predicate<Map<Long, File>>> apply(@Nullable Optional<Integer[]> input) {
    return Collections.emptyList();
  }
}
