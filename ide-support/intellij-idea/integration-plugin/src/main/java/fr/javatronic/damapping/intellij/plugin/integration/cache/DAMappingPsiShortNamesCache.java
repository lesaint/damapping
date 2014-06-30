package fr.javatronic.damapping.intellij.plugin.integration.cache;

import fr.javatronic.damapping.intellij.plugin.integration.component.project.ParseAndGenerateManager;
import fr.javatronic.damapping.intellij.plugin.integration.index.GeneratedClassSimpleNameIndex;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContext;

import java.util.Arrays;
import java.util.Collection;
import com.google.common.base.Optional;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.Processor;
import com.intellij.util.containers.HashSet;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * DAMappingPsiShortNamesCache -
 *
 * @author Sébastien Lesaint
 */
public class DAMappingPsiShortNamesCache extends PsiShortNamesCache {

  private static final PsiMethod[] NO_PSI_METHODS = new PsiMethod[0];
  private static final PsiField[] NO_PSI_FIELDS = new PsiField[0];
  private static final String[] NO_NAMES = new String[0];

  private final Project project;

  public DAMappingPsiShortNamesCache(Project project) {
    this.project = project;
  }

  @NotNull
  @Override
  public PsiClass[] getClassesByName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
    ParseAndGenerateManager parseAndGenerateManager = ParseAndGenerateManager.getInstance(project);
    Collection<VirtualFile> files = FileBasedIndex.getInstance()
                                               .getContainingFiles(GeneratedClassSimpleNameIndex.NAME, name, scope);
    for (VirtualFile file : files) {
      PsiManager psiManager = PsiManager.getInstance(project);
      PsiFile psiFile = psiManager.findFile(file);
      if (psiFile instanceof PsiJavaFile) {
        Optional<GenerationContext> generationContext = parseAndGenerateManager.computeGenerationContext(
            ((PsiJavaFile) psiFile).getClasses()[0], scope
        );
        if (!generationContext.isPresent()) {
          continue;
        }

        for (String key : generationContext.get().getDescriptorKeys()) {
          if (name.equals(generationContext.get().getDescriptor(key).getType().getSimpleName().getName())) {
            Optional<PsiClass> generatedPsiClass = parseAndGenerateManager.getGeneratedPsiClass(
                generationContext.get(), key, project
            );
            if (generatedPsiClass.isPresent()) {
              return new PsiClass[] { generatedPsiClass.get() };
            }
          }
        }
      }
    }

    return PsiClass.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String[] getAllClassNames() {
    Collection<String> allKeys = FileBasedIndex.getInstance().getAllKeys(GeneratedClassSimpleNameIndex.NAME, project);
    return allKeys.toArray(new String[allKeys.size()]);
  }

  @Override
  public void getAllClassNames(@NotNull HashSet<String> dest) {
    dest.addAll(Arrays.asList(getAllClassNames()));
  }

  @NotNull
  @Override
  public PsiMethod[] getMethodsByName(@NonNls @NotNull String name, @NotNull GlobalSearchScope scope) {
    return NO_PSI_METHODS;
  }

  @NotNull
  @Override
  public PsiMethod[] getMethodsByNameIfNotMoreThan(@NonNls @NotNull String name, @NotNull GlobalSearchScope scope,
                                                   int maxCount) {
    return NO_PSI_METHODS;
  }

  @NotNull
  @Override
  public PsiField[] getFieldsByNameIfNotMoreThan(@NonNls @NotNull String name, @NotNull GlobalSearchScope scope,
                                                 int maxCount) {
    return NO_PSI_FIELDS;
  }

  @Override
  public boolean processMethodsWithName(@NonNls @NotNull String name, @NotNull GlobalSearchScope scope,
                                        @NotNull Processor<PsiMethod> processor) {
    return false;
  }

  @NotNull
  @Override
  public String[] getAllMethodNames() {
    return NO_NAMES;
  }

  @Override
  public void getAllMethodNames(@NotNull HashSet<String> set) {
    // do nothing -- not supported
  }

  @NotNull
  @Override
  public PsiField[] getFieldsByName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
    return NO_PSI_FIELDS;
  }

  @NotNull
  @Override
  public String[] getAllFieldNames() {
    return NO_NAMES;
  }

  @Override
  public void getAllFieldNames(@NotNull HashSet<String> set) {
    // do nothing -- not supported
  }
}
