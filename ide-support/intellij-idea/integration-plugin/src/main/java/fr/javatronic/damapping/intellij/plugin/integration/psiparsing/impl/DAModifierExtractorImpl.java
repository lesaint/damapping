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

import fr.javatronic.damapping.processor.model.DAModifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;

import static com.google.common.collect.FluentIterable.from;

/**
 * DAModifierExtractorImpl -
 *
 * @author Sébastien Lesaint
 */
public class DAModifierExtractorImpl implements DAModifierExtractor {

  @Override
  @Nonnull
  public Set<DAModifier> extractModifiers(PsiClass psiClass) {
    return toDAModifierSet(psiClass.getModifierList());
  }

  @Override
  @Nonnull
  public Set<DAModifier> extractModifiers(PsiMethod psiMethod) {
    return toDAModifierSet(psiMethod.getModifierList());
  }

  @Override
  @Nonnull
  public Set<DAModifier> extractModifiers(PsiParameter psiParameter) {
    Optional<PsiModifierList> modifiers = from(Arrays.asList(psiParameter.getChildren())).filter(PsiModifierList.class)
        .first();
    if (modifiers.isPresent()) {
      return toDAModifierSet(modifiers.get());
    }
    return Collections.emptySet();
  }

  private static Set<DAModifier> toDAModifierSet(@Nullable PsiModifierList modifierList) {
    if (modifierList == null) {
      return Collections.emptySet();
    }

    return from(Arrays.asList(modifierList.getChildren()))
        .filter(PsiKeyword.class)
        .transform(PsiKeywordToDAModifier.INSTANCE)
        .toImmutableSet();
  }

  private static enum PsiKeywordToDAModifier implements Function<PsiKeyword, DAModifier> {
    INSTANCE;

    private static final Map<String, DAModifier> PSIKEYWORD_DAMODIFIER_MAP = buildPsiKeywordToDAModifierMap();

    private static Map<String, DAModifier> buildPsiKeywordToDAModifierMap() {
      ImmutableMap.Builder<String, DAModifier> builder = ImmutableMap.<String, DAModifier>builder();
      for (DAModifier daModifier : DAModifier.values()) {
        builder.put(daModifier.name().toLowerCase(Locale.US), daModifier);
      }
      return builder.build();
    }

    @Nullable
    @Override
    public DAModifier apply(@Nullable PsiKeyword psiKeyword) {
      if (psiKeyword == null) {
        return null;
      }
      return PSIKEYWORD_DAMODIFIER_MAP.get(psiKeyword.getText());
    }
  }
}
