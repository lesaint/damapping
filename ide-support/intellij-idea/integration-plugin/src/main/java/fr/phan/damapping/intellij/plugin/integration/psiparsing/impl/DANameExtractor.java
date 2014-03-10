package fr.phan.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.phan.damapping.processor.model.DAName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiType;

/**
 * DANameExtractor -
 *
 * @author Sébastien Lesaint
 */
public interface DANameExtractor {

  @Nonnull
  DAName extractPackageName(@Nonnull PsiClass psiClass);

  @Nonnull
  DAName simpleName(@Nonnull PsiType psiType);

  @Nonnull
  DAName qualifiedName(@Nonnull PsiClass psiClass);

  @Nullable
  DAName qualifiedName(String simpleName, @Nullable PsiImportList psiImportList);

  @Nullable
  DAName interfaceQualifiedName(PsiClassType psiClassType, PsiImportList psiImportList);
}
