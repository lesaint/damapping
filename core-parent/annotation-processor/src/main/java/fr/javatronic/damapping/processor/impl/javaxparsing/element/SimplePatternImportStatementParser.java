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
package fr.javatronic.damapping.processor.impl.javaxparsing.element;

import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * SimplePatternImportStatementParser - An ImportStatementParser that uses a Regexp to parse import statements in a
 * correctly formatted source file (ie. statement on a single line and qualified names do not have blank caracters).
 *
 * @author Sébastien Lesaint
 */
enum SimplePatternImportStatementParser implements ImportStatementParser {
  INSTANCE;

  // TODO improve import statement parsing as they can have any number of blank characters before and after each
  // identifier (even line returns)
  // "         import    javax.   lang.   model.   type  .    DeclaredType    ;     "
  private static final Pattern IMPORT_STMT_PATTERN = Pattern.compile("^\\s*import\\s*([\\w\\.]+)\\s*;s*$",
      Pattern.MULTILINE
  );

  @Nonnull
  @Override
  public Iterable<String> qualifiedNames(@Nullable CharSequence charSequence) {
    if (charSequence == null || charSequence.length() == 0) {
      return Collections.emptyList();
    }

    final Matcher matcher = IMPORT_STMT_PATTERN.matcher(charSequence);

    return new Iterable<String>() {
      @Override
      public Iterator<String> iterator() {
        return new Iterator<String>() {
          @Override
          public boolean hasNext() {
            return matcher.find();
          }

          @Override
          public String next() {
            return matcher.group(1);
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException("remove is not supported by this implementation of Iterator");
          }
        };
      }
    };
  }
}
