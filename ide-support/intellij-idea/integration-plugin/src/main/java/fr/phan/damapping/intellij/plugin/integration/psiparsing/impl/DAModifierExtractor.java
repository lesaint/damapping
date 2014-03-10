package fr.phan.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.phan.damapping.processor.model.DAModifier;

import java.util.Set;

import javax.annotation.Nonnull;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

/**
 * DAModifierExtractor -
 *
 * @author Sébastien Lesaint
 */
public interface DAModifierExtractor {

  @Nonnull
  Set<DAModifier> extractModifiers(PsiClass psiClass);

  @Nonnull
  Set<DAModifier> extractModifiers(PsiMethod psiMethod);

  @Nonnull
  Set<DAModifier> extractModifiers(PsiParameter psiParameter);
}
