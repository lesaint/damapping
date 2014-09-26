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
