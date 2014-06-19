package fr.javatronic.damapping.intellij.plugin.integration.index;

import com.intellij.psi.PsiClass;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

/**
 * MapperQualifiedNameIndex -
 *
 * @author Sébastien Lesaint
 */
public class MapperQualifiedNameIndex extends MapperNameIndex {
  public static final ID<String,Void> NAME = ID.create("MapperSimpleNameIndex");

  @NotNull
  @Override
  public ID<String, Void> getName() {
    return NAME;
  }

  @Override
  protected String getKey(PsiClass psiClass) {
    return psiClass.getQualifiedName() + "Mapper";
  }

  @Override
  public int getVersion() {
    return 0;
  }
}
