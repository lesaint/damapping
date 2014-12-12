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
package fr.javatronic.damapping.test.mappermethod;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.annotation.MapperFactory;

import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * MultipleImplementationAsEnum - This mapper ensures supports for enum mapper wiht @MapperFactory methods and which
 * expose more than one mapper method.
 *
 * @author Sébastien Lesaint
 */
@Mapper
public enum MultipleMethodsAsEnum {
  BIG_DECIMAL(true),
  INTEGER(false);

  private final boolean bigDecimal;

  @MapperFactory
  public static MultipleMethodsAsEnum bigDecimal() {
    return BIG_DECIMAL;
  }

  /**
   * Factory method
   */
  @MapperFactory
  public static MultipleMethodsAsEnum integer() {
    return INTEGER;
  }

  private MultipleMethodsAsEnum(boolean bigDecimal) {
    this.bigDecimal = bigDecimal;
  }

  @Nonnull
  public Integer fromString(@Nullable String input) {
    if (input == null) {
      return 0;
    }
    if (bigDecimal) {
      return new BigDecimal(input).intValue();
    }
    return Integer.parseInt(input);
  }

  @Nonnull
  public String toString(@Nullable Integer i) {
    if (i == null) {
      return "";
    }
    if (bigDecimal) {
      return i.toString();
    }
    return String.valueOf(i.intValue());
  }
}
