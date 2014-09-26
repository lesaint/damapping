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

import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * Predicates -
 *
 * @author Sébastien Lesaint
 */
public final class Predicates {
  private Predicates() {
    // prevents instantiation
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> notNull() {
    return (Predicate<T>) NotNullPredicate.INSTANCE;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> alwaysTrue() {
    return (Predicate<T>) AlwaysTruePredicate.INSTANCE;
  }

  public static <T> Predicate<T> not(final Predicate<T> p) {
    return new Predicate<T>() {
      @Override
      public boolean apply(@Nullable T t) {
        return !p.apply(t);
      }
    };
  }

  public static <T> Predicate<T> equalTo(final T obj) {
    return new Predicate<T>() {
      @Override
      public boolean apply(@Nullable T t) {
        return obj == null ? t == null : obj.equals(t);
      }
    };
  }

  public static <T> Predicate<Object> instanceOf(Class<?> type) {
    return new InstanceOfPredicate(type);
  }

  private static class InstanceOfPredicate implements Predicate<Object> {
    private final Class<?> type;

    public InstanceOfPredicate(@Nonnull Class<?> type) {
      this.type = checkNotNull(type);
    }

    @Override
    public boolean apply(@Nullable Object t) {
      return type.isInstance(t);
    }
  }

  public static <Q> Predicate<Q> and(final Predicate<? super Q> left, final Predicate<? super Q> right) {
    return new Predicate<Q>() {
      @Override
      public boolean apply(@Nullable Q q) {
        return left.apply(q) && right.apply(q);
      }
    };
  }

  public static <Q> Predicate<Q> or(final Predicate<? super Q> left, final Predicate<? super Q> right) {
    return new Predicate<Q>() {
      @Override
      public boolean apply(@Nullable Q q) {
        return left.apply(q) || right.apply(q);
      }
    };
  }

  public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function) {
    return new CompositionPredicate<A, B>(predicate, function);
  }

  private static class CompositionPredicate<A, B> implements Predicate<A>, Serializable {
    final Predicate<B> p;
    final Function<A, ? extends B> f;

    private CompositionPredicate(Predicate<B> p, Function<A, ? extends B> f) {
      this.p = checkNotNull(p);
      this.f = checkNotNull(f);
    }

    @Override
    public boolean apply(@Nullable A a) {
      return p.apply(f.apply(a));
    }

    @Override
    public String toString() {
      return p.toString() + "(" + f.toString() + ")";
    }

  }

  private static enum NotNullPredicate implements Predicate<Object> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable Object t) {
      return t != null;
    }
  }

  private static enum AlwaysTruePredicate implements Predicate<Object> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable Object t) {
      return true;
    }
  }
}
