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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FileContextTestImpl -
 *
 * @author Sébastien Lesaint
 */
class FileContextTestImpl implements FileContext {
  private final StringWriter out = new StringWriter();
  private final BufferedWriter bw = new BufferedWriter(out);
  @Nonnull
  private final String packageName;

  FileContextTestImpl() {
    this(null);
  }

  FileContextTestImpl(@Nullable String packageName) {
    this.packageName = packageName == null ? "" : packageName;
  }

  String getRes() throws IOException {
    bw.flush();
    return out.getBuffer().toString();
  }

  @Nonnull
  @Override
  public String getPackageName() {
    return packageName;
  }

  @Nonnull
  @Override
  public BufferedWriter getWriter() {
    return bw;
  }

  @Override
  public boolean hasExpliciteImport(@Nullable DAType type) {
    // returning true makes commonMethod.appendType always write references using the simpeName
    return true;
  }

  @Override
  public boolean hasHomonymousImport(@Nullable DAType type) {
    // we do not test homonymous class imports with this implementation
    return false;
  }
}
