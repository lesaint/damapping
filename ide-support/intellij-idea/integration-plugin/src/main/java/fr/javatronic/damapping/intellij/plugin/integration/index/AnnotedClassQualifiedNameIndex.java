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

import fr.javatronic.damapping.intellij.plugin.integration.provider.Common;

import java.util.Collections;
import java.util.Set;

import com.intellij.psi.PsiClass;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

/**
 * AnnotedClassQualifiedNameIndex - Index VirtualFile of classes annoted with @Mapper by their qualified name.
 * This index is a cheap and efficient way of discorering any class annoted with @Mapper in the current Project.
 *
 * @author Sébastien Lesaint
 */
public class AnnotedClassQualifiedNameIndex extends AbstractPsiClassIndex {
  public static final ID<String,Void> NAME = ID.create("AnnotedClassQualifiedNameIndex");

  @NotNull
  @Override
  public ID<String, Void> getName() {
    return NAME;
  }

  @Override
  protected boolean filter(PsiClass psiClass) {
    return Common.hasMapperAnnotation(psiClass);
  }

  @Override
  public int getVersion() {
    return 0;
  }

  @Override
  protected Set<String> getKeys(PsiClass psiClass) {
    return Collections.singleton(psiClass.getQualifiedName());
  }
}
