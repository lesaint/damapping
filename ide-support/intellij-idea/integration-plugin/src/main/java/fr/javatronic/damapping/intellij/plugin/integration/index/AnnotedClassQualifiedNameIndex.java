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
