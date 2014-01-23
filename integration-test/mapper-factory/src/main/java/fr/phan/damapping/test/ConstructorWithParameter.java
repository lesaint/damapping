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
package fr.phan.damapping.test;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.annotation.MapperFactoryMethod;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * ConstructorWithParameter -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class ConstructorWithParameter implements Function<BigDecimal, String> {
  private final String salt;

  @MapperFactoryMethod
  public ConstructorWithParameter(String salt) {
    this.salt = salt;
  }

  @Nullable
  @Override
  public String apply(@Nullable BigDecimal bigDecimal) {
    return bigDecimal + "-" + salt;
  }
}
