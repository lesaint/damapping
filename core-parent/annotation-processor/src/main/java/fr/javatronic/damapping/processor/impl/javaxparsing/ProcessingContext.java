package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Sets;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * ProcessingContext -
 *
 * @author Sébastien Lesaint
 */
public class ProcessingContext {
  @Nonnull
  private final Set<DAType> successful = new HashSet<DAType>();
  @Nonnull
  private final Set<DAType> failed = new HashSet<DAType>();
  @Nonnull
  private final Set<ParsingResult> postponed = new HashSet<ParsingResult>();

  public void addSuccessful(@Nonnull DAType daType) {
    successful.add(checkNotNull(daType));
  }

  public void addFailed(@Nonnull DAType daType) {
    failed.add(checkNotNull(daType));
  }

  public void addPostponed(@Nonnull ParsingResult parsingResult) {
    postponed.add(checkNotNull(parsingResult));
  }

  public void setFailed(@Nonnull ParsingResult parsingResult) {
    postponed.remove(checkNotNull(parsingResult));
    failed.add(parsingResult.getType());
  }

  public void setSuccessful(@Nonnull ParsingResult parsingResult) {
    postponed.remove(checkNotNull(parsingResult));
    successful.add(parsingResult.getType());
  }

  @Nonnull
  public Set<ParsingResult> getPostponed() {
    return postponed.isEmpty() ? Collections.<ParsingResult>emptySet() : Sets.copyOf(postponed);
  }

  public Set<DAType> findSuccessfullBySimpleName(@Nullable String simpleName) {
    if (simpleName == null || successful.isEmpty()) {
      return Collections.emptySet();
    }

    return from(successful).filter(new DATypeSimpleNamePredicate(simpleName)).toSet();
  }

  public Set<DAType> findFailedBySimpleName(@Nullable String simpleName) {
    if (simpleName == null || failed.isEmpty()) {
      return Collections.emptySet();
    }

    return from(failed).filter(new DATypeSimpleNamePredicate(simpleName)).toSet();
  }

  private static class DATypeSimpleNamePredicate implements Predicate<DAType> {
    private final String simpleName;

    public DATypeSimpleNamePredicate(@Nonnull String simpleName) {
      this.simpleName = checkNotNull(simpleName);
    }

    @Override
    public boolean apply(@Nullable DAType daType) {
      return daType != null && simpleName.equals(daType.getSimpleName().getName());
    }
  }
}
