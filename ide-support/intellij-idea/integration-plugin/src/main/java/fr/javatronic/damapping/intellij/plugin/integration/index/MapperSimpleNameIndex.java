package fr.javatronic.damapping.intellij.plugin.integration.index;

import com.intellij.psi.PsiClass;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

/**
 * MapperSimpleNameIndex -
 *
 * @author Sébastien Lesaint
 */
public class MapperSimpleNameIndex extends MapperNameIndex {
  public static final ID<String,Void> NAME = ID.create("MapperSimpleNameIndex");

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
  protected String getKey(PsiClass psiClass) {
    return psiClass.getName() + "Mapper";
  }
}
