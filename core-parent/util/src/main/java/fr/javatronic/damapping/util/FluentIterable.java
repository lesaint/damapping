package fr.javatronic.damapping.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * FluentIterable - Naive implementation of the subset of Guava's FluentIterable methods used in DAMapping.
 *
 * @author Sébastien Lesaint
 */
public class FluentIterable<Q> {
  @Nonnull
  private final Iterable<Q> source;

  private FluentIterable(@Nullable Iterable<Q> source) {
    this.source = source;
  }

  public static <T> FluentIterable<T> from(@Nonnull Iterable<T> from) {
    return new FluentIterable<T>(checkNotNull(from, "Source can not be null"));
  }

  @SuppressWarnings("unchecked")
  public final <T> FluentIterable<T> filter(final Class<T> type) {
    return new FluentIterable<T>(new PredicateIterable<T>((Iterable<T>) this.source, Predicates.instanceOf(type)));
  }

  public FluentIterable<Q> filter(final Predicate<? super Q> predicate) {
    return new FluentIterable<Q>(new PredicateIterable<Q>(this.source, predicate));
  }

  public <V> FluentIterable<V> transform(final Function<? super Q, ? extends V> function) {
    return new FluentIterable<V>(new FunctionIterable<Q, V>(this.source, function));
  }

  @Nonnull
  private <Q> Iterable<Q> nonNullIterable(@Nullable Iterable<Q> source) {
    return source == null ? Collections.<Q>emptyList() : source;
  }

  public List<Q> toList() {
    if (source == null) {
      return Collections.emptyList();
    }
    if (source instanceof List) {
      return (List<Q>) source;
    }

    return populateFromIterator(new ArrayList<Q>(), source.iterator());
  }

  public Set<Q> toSet() {
    if (source == null) {
      return Collections.emptySet();
    }
    if (source instanceof Set) {
      return (Set<Q>) source;
    }

    return populateFromIterator(new HashSet<Q>(), source.iterator());
  }

  private static <T extends Collection<R>, R> T populateFromIterator(T collection, Iterator<R> iterator) {
    while (iterator.hasNext()) {
      collection.add(iterator.next());
    }
    return collection;
  }

  public Optional<Q> first() {
    if (source == null) {
      return Optional.absent();
    }
    Iterator<Q> iterator = source.iterator();
    if (iterator.hasNext()) {
      return Optional.fromNullable(iterator.next());
    }
    return Optional.absent();
  }

  public Optional<Q> firstMatch(Predicate<Q> predicate) {
    if (source == null) {
      return Optional.absent();
    }

    Iterator<Q> iterator = source.iterator();
    while (iterator.hasNext()) {
      Q current = iterator.next();
      if (predicate.apply(current)) {
        return Optional.fromNullable(current);
      }
    }
    return Optional.absent();
  }

  public int size() {
    int res = 0;
    Iterator<Q> it = this.source.iterator();
    while (it.hasNext()) {
      it.next();
      res++;
    }
    return res;
  }

  private class PredicateIterable<R> implements Iterable<R> {
    @Nonnull
    private final Iterable<R> source;
    @Nonnull
    private final Predicate<? super R> predicate;

    public PredicateIterable(@Nonnull Iterable<R> source, @Nonnull Predicate<? super R> predicate) {
      this.source = checkNotNull(source);
      this.predicate = checkNotNull(predicate);
    }

    @Override
    public Iterator<R> iterator() {
      return new PredicateIterator<R>(this.source.iterator(), predicate);
    }
  }

  private static class PredicateIterator<R> implements Iterator<R> {
    @Nonnull
    private final Iterator<R> iterator;
    @Nonnull
    private final Predicate<? super R> predicate;

    private R next = null;
    private boolean consumed = true;

    public PredicateIterator(@Nonnull Iterator<R> iterator, @Nonnull Predicate<? super R> predicate) {
      this.iterator = checkNotNull(iterator);
      this.predicate = checkNotNull(predicate);
    }

    @Override
    public boolean hasNext() {
      return tryNext();
    }

    @Override
    public R next() {
      if (!tryNext()) {
        throw new NoSuchElementException("No more element in Iterator");
      }

      R n = this.next;
      this.consumed = true;
      this.next = null;
      return n;
    }

    private boolean tryNext() {
      if (!consumed) {
        return true;
      }

      while (iterator.hasNext()) {
        R n = iterator.next();
        if (predicate.apply(n)) {
          this.next = n;
          this.consumed = false;
          return true;
        }
      }
      return false;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Can not remove from this iterator");
    }
  }

  private static class FunctionIterable<R, V> implements Iterable<V> {
    @Nonnull
    private final Iterable<? extends R> source;
    @Nonnull
    private final Function<? super R, ? extends V> function;

    private FunctionIterable(@Nonnull Iterable<? extends R> source, @Nonnull Function<? super R, ? extends V> function) {
      this.source = checkNotNull(source);
      this.function = checkNotNull(function);
    }

    @Override
    public Iterator<V> iterator() {
      return new FunctionIterator<V, R>(source.iterator(), function);
    }
  }

  private static class FunctionIterator<V, R> implements Iterator<V> {
    @Nonnull
    private final Iterator<? extends R> source;
    @Nonnull
    private final Function<? super R, ? extends V> function;
    private V next = null;
    private boolean consumed = true;

    private FunctionIterator(@Nonnull Iterator<? extends R> source, @Nonnull Function<? super R, ? extends V> function) {
      this.source = checkNotNull(source);
      this.function = checkNotNull(function);
    }

    @Override
    public boolean hasNext() {
      return tryNext();
    }

    @Override
    public V next() {
      if (!tryNext()) {
        throw new NoSuchElementException("No more element in Iterator");
      }

      V n = this.next;
      this.consumed = true;
      this.next = null;
      return n;
    }

    private boolean tryNext() {
      if (!consumed) {
        return true;
      }

      if (source.hasNext()) {
        R n = source.next();
        this.next = function.apply(n);
        this.consumed = false;
        return true;
      }
      return false;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Can not remove from this iterator");
    }
  }
}
