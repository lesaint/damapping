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
