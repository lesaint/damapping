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
package fr.javatronic.damapping.intellij.plugin.integration.index;

import fr.javatronic.damapping.intellij.plugin.integration.component.project.ParseAndGenerateManager;
import fr.javatronic.damapping.intellij.plugin.integration.provider.Common;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

import static com.google.common.collect.FluentIterable.from;

/**
 * GeneratedClassSimpleNameIndex - Index VirtualFile of classes annoted with @Mapper by the simple name of the
 * generated classes/interfaces. This index is a cheap and efficient way of implementing
 * {@link fr.javatronic.damapping.intellij.plugin.integration.cache.DAMappingPsiShortNamesCache}.
 *
 * @author Sébastien Lesaint
 */
public class GeneratedClassSimpleNameIndex extends AbstractPsiClassIndex {
  public static final ID<String,Void> NAME = ID.create("GeneratedClassSimpleNameIndex");

  @NotNull
  @Override
  public ID<String, Void> getName() {
    return NAME;
  }

  @Override
  public int getVersion() {
    return 0;
  }

  @Override
  protected boolean filter(PsiClass psiClass) {
    return Common.hasMapperAnnotation(psiClass);
  }

  @Override
  protected Set<String> getKeys(PsiClass psiClass) {
    ParseAndGenerateManager parseAndGenerateManager = ParseAndGenerateManager
        .getInstance(psiClass.getProject());

    List<PsiClass> generatedPsiClasses = parseAndGenerateManager
        .getGeneratedPsiClasses(psiClass, GlobalSearchScope.allScope(psiClass.getProject()));

    return from(generatedPsiClasses).transform(PsiClassToName.INSTANCE).toImmutableSet();
  }

  private static enum PsiClassToName implements Function<PsiClass, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(@Nullable PsiClass psiClass) {
      return psiClass.getName();
    }
  }
}
