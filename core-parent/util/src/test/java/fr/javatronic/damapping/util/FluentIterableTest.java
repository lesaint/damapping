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
package fr.javatronic.damapping.util;

import java.util.List;
import javax.annotation.Nullable;

import org.testng.annotations.Test;

import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Predicates.equalTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * FluentIterableTest -
 *
 * @author Sébastien Lesaint
 */
public class FluentIterableTest {

  private static final List<Integer> LIST_1_2_3 = Lists.of(1, 2, 3);
  private static final List<Integer> LIST_2_1_3 = Lists.of(2, 1, 3);
  private static final Predicate<Integer> MORE_THAN_ONE_PREDICATE = new Predicate<Integer>() {
    @Override
    public boolean apply(@Nullable Integer s) {
      return s > 1;
    }
  };
  private static final Function<Integer,Integer> MULTIPLY_BY_TWO_FUNCTION = new Function<Integer, Integer>() {
    @Nullable
    @Override
    public Integer apply(@Nullable Integer integer) {
      return integer * 2;
    }
  };
  private static final Function<Integer,String> APPEND_COLON_FUNCTION = new Function<Integer, String>() {
    @Nullable
    @Override
    public String apply(@Nullable Integer integer) {
      return integer + ":";
    }
  };

  @Test(expectedExceptions = NullPointerException.class)
  public void filter_from_null_collection_throws_NPE() throws Exception {
    from(null);
  }

  @Test
  public void result_of_toSet_is_result_of_predicate_applied() throws Exception {
    assertThat(
        from(LIST_1_2_3).filter(equalTo(2)).toSet()
    ).containsExactly(2);
    assertThat(
        from(LIST_2_1_3).filter(MORE_THAN_ONE_PREDICATE).toSet()
    ).containsExactly(2, 3);
  }

  @Test
  public void result_of_toList_after_transform() throws Exception {
    assertThat(
        from(LIST_1_2_3).transform(MULTIPLY_BY_TWO_FUNCTION).toList()
    ).containsExactly(2, 4, 6);
  }

  @Test
  public void result_of_toSet_after_transform() throws Exception {
    assertThat(
        from(LIST_2_1_3).transform(MULTIPLY_BY_TWO_FUNCTION).toSet()
    ).containsExactly(2, 4, 6);
  }

  @Test
  public void result_of_toList_after_multiple_transforms() throws Exception {
    assertThat(
        from(LIST_1_2_3)
            .transform(MULTIPLY_BY_TWO_FUNCTION)
            .transform(APPEND_COLON_FUNCTION)
            .toList()
    ).containsExactly("2:", "4:", "6:");
  }

  @Test
  public void result_of_toSet_after_multiple_transforms() throws Exception {
    assertThat(
        from(LIST_2_1_3)
            .transform(MULTIPLY_BY_TWO_FUNCTION)
            .transform(APPEND_COLON_FUNCTION)
            .toSet()
    ).containsOnly("2:", "4:", "6:");
  }

  public void ordering_of_filters_matters() throws Exception {
    Predicate<Integer> evenPredicate = new Predicate<Integer>() {
      @Override
      public boolean apply(@Nullable Integer s) {
        return s % 2 == 1;
      }
    };

    assertThat(
        from(LIST_1_2_3).filter(MORE_THAN_ONE_PREDICATE).filter(evenPredicate).toSet()
    ).containsExactly(3);
    assertThat(
        from(LIST_1_2_3).filter(evenPredicate).filter(MORE_THAN_ONE_PREDICATE).toSet()
    ).containsExactly(3);
  }
}
