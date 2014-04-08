package fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiContext;
import fr.javatronic.damapping.processor.model.DAType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaCodeReferenceElement;
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
  DAType forParameter(PsiParameter psiParameter, PsiContext psiContext);

  @Nullable
  DAType forMethod(PsiMethod psiMethod, PsiContext psiContext);

  @Nonnull
  DAType forInterface(PsiJavaCodeReferenceElement referenceElement, PsiContext psiContext);

  @Nonnull
  DAType forAnnotation(PsiAnnotation psiAnnotation, PsiContext psiContext);
}
