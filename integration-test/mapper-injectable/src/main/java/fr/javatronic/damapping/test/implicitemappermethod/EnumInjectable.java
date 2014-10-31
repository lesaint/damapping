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

import fr.javatronic.damapping.annotation.Injectable;
import fr.javatronic.damapping.annotation.Mapper;

import javax.annotation.Nonnull;

/**
 * EnumInjectable - Demonstrate how an injectable bean is created from a dedicated enum with a
 * mapper method with some specific name and a specific instance value name.
 *
 * @author Sébastien Lesaint
 */
@Mapper
@Injectable
public enum EnumInjectable {
  THE_ONLY_INSTANCE;

  @Nonnull
  public String fooBar(String someParam) {
    return someParam.toString();
  }
}
