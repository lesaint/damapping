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
package fr.javatronic.damapping.test;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.annotation.MapperFactoryMethod;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * MultipleImplementationAsEnum - Exemple de mapper nécessitant d'être instancié avec un paramètre
 * (implémenté sous forme d'enum, mais ce n'est pas obligatoire, l'essentiel est que les méthodes annotées avec
 * MapperFactoryMethod soient statiques)
 *
 * @author Sébastien Lesaint
 */
@Mapper
public enum MultipleImplementationAsEnum implements Function<String, Integer> {
  BIG_DECIMAL(true),
  INTEGER(false);

  private final boolean bigDecimal;

  @MapperFactoryMethod
  public static MultipleImplementationAsEnum bigDecimal() {
    return BIG_DECIMAL;
  }

  /**
   * Factory method
   */
  @MapperFactoryMethod
  public static MultipleImplementationAsEnum integer() {
    return INTEGER;
  }

  /**
   * Exemple de factory method à paramètre
   */
  @MapperFactoryMethod
  public static MultipleImplementationAsEnum instance(boolean bigDecimal) {
    if (bigDecimal) {
      return BIG_DECIMAL;
    }
    return INTEGER;
  }

  private MultipleImplementationAsEnum(boolean bigDecimal) {
    this.bigDecimal = bigDecimal;
  }

  @Override
  public Integer apply(@Nullable String input) {
    if (bigDecimal) {
      return new BigDecimal(input).intValue();
    }
    return Integer.parseInt(input);
  }
}
