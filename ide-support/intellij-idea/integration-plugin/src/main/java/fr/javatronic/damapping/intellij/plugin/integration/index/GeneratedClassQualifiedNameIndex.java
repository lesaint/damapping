package fr.javatronic.damapping.intellij.plugin.integration.index;

import fr.javatronic.damapping.intellij.plugin.integration.provider.Common;

import java.util.Collections;
import java.util.Set;

import com.intellij.psi.PsiClass;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

/**
 * GeneratedClassQualifiedNameIndex -
 *
 * @author Sébastien Lesaint
 */
public class GeneratedClassQualifiedNameIndex extends PsiClassIndex {
  public static final ID<String,Void> NAME = ID.create("GeneratedClassQualifiedNameIndex");

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
  protected Set<String> getKeys(PsiClass psiClass) {
    return Collections.singleton(psiClass.getQualifiedName() + "Mapper");
  }

  @Override
  public int getVersion() {
    return 0;
  }
}
