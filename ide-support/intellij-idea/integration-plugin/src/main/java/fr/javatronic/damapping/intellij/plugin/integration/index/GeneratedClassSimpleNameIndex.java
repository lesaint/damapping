package fr.javatronic.damapping.intellij.plugin.integration.index;

import fr.javatronic.damapping.intellij.plugin.integration.provider.Common;

import java.util.Collections;
import java.util.Set;

import com.intellij.psi.PsiClass;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

/**
 * GeneratedClassSimpleNameIndex - Index VirtualFile of classes annoted with @Mapper by the simple name of the
 * generated classes/interfaces. This index is a cheap and efficient way of implementing
 * {@link fr.javatronic.damapping.intellij.plugin.integration.cache.DAMappingPsiShortNamesCache}.
 *
 * @author Sébastien Lesaint
 */
public class GeneratedClassSimpleNameIndex extends PsiClassIndex {
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
    return Collections.singleton(psiClass.getName() + "Mapper");
  }
}
