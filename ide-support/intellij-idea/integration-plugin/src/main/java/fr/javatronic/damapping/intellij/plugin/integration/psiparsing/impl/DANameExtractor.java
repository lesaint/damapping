package fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiContext;
import fr.javatronic.damapping.processor.model.DAName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;

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
  DAName simpleName(PsiTypeElement psiTypeElement);

  @Nonnull
  DAName qualifiedName(@Nonnull PsiClass psiClass);

  @Nullable
  DAName qualifiedName(String name, @Nullable PsiContext psiContext);

  @Nullable
  DAName interfaceQualifiedName(PsiClassType psiClassType, PsiContext psiContext);

  @Nullable
  DAName interfaceQualifiedName(PsiJavaCodeReferenceElement referenceElement, PsiContext psiContext);

  @Nullable
  DAName qualifiedName(PsiTypeElement psiTypeElement, PsiContext psiContext);
}
