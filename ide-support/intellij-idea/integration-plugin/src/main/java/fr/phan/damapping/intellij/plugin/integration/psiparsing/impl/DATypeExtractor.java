package fr.phan.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.phan.damapping.processor.model.DAType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

/**
 * DATypeExtractor -
 *
 * @author Sébastien Lesaint
 */
public interface DATypeExtractor {

  @Nonnull
  DAType forClassOrEnum(PsiClass psiClass);

  @Nonnull
  DAType forParameter(PsiParameter psiParameter, PsiImportList psiImportList);

  @Nullable
  DAType forMethod(PsiMethod psiMethod, PsiImportList psiImportList);

  @Nonnull
  DAType forInterface(PsiClassType psiClassType, PsiImportList psiImportList);

  @Nonnull
  DAType forAnnotation(PsiAnnotation psiAnnotation, PsiImportList psiImportList);
}
