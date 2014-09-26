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
package fr.javatronic.damapping.intellij.plugin.integration.psiparsing;

import fr.javatronic.damapping.processor.model.DAName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * PsiContext -
 *
 * @author Sébastien Lesaint
 */
public class PsiContext {
  @Nullable
  private final PsiImportList psiImportList;
  @Nonnull
  private final DAName packageName;

  public PsiContext(@Nullable PsiImportList psiImportList, @Nonnull DAName packageName) {
    this.psiImportList = psiImportList;
    this.packageName = checkNotNull(packageName);
  }

  @Nullable
  public PsiImportList getPsiImportList() {
    return psiImportList;
  }

  @Nonnull
  public DAName getPackageName() {
    return packageName;
  }

  @Nonnull
  public PsiImportStatement[] getImportStatements() {
    return psiImportList == null ? PsiImportStatement.EMPTY_ARRAY : psiImportList.getImportStatements();
  }
}
