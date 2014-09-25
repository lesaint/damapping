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
package fr.javatronic.damapping.test.guava;

import fr.javatronic.damapping.annotation.Mapper;

import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * WildcardGenerics - Demonstrate support for a class annotated with {@code @Mapper} which declare no default
 * constructor but a constructor with a single parameter with a type from {@code java.lang} (ie. does not require
 * an explicite import).
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class ConstructorWithJavaLangParameter implements Function<Integer, String> {
  private final Boolean someFlag;

  public ConstructorWithJavaLangParameter(Boolean someFlag) {
    this.someFlag = someFlag;
  }

  @Nullable
  @Override
  public String apply(@Nullable Integer input) {
    return "resultDoesNotMatter";
  }
}