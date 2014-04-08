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
