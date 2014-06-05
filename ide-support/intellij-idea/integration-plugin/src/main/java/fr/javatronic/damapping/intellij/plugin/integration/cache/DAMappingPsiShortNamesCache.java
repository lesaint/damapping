package fr.javatronic.damapping.intellij.plugin.integration.cache;

import fr.javatronic.damapping.intellij.plugin.integration.provider.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.Processor;
import com.intellij.util.containers.HashSet;
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
  private static final PsiClass[] NO_PSI_CLASSES = new PsiClass[0];

  private final Project project;

  public DAMappingPsiShortNamesCache(Project project) {
    this.project = project;
  }

  @NotNull
  @Override
  public PsiClass[] getClassesByName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
    Project project = scope.getProject();

    List<PsiClass> res = getAllPsiClass(scope, project);

    for (PsiClass psiClass : res) {
      if (name.equals(psiClass.getName())) {
        return new PsiClass[] { psiClass };
      }
    }

    return NO_PSI_CLASSES;
  }

  private static List<PsiClass> getAllPsiClass(GlobalSearchScope scope, Project project) {
    List<PsiClass> res = new ArrayList<PsiClass>();
    PsiManager psiManager = PsiManager.getInstance(project);
    for (VirtualFile root : ProjectRootManager.getInstance(psiManager.getProject()).getContentSourceRoots()) {
      PsiDirectory directory = psiManager.findDirectory(root);
      addClasses(scope, res, directory);
    }
    return res;
  }

  private static void addClasses(GlobalSearchScope scope, List<PsiClass> res, PsiDirectory directory) {
    PsiClass[] classes = JavaDirectoryService.getInstance().getClasses(directory);

    res.addAll(Arrays.asList(classes));
    for (PsiClass psiClass : classes) {
      PsiClass psiClass1 = Common.generateClass(scope, psiClass.getName());
      if (psiClass1 != null) {
        res.add(psiClass1);
      }
    }

    for (PsiDirectory psiDirectory : directory.getSubdirectories()) {
      addClasses(scope, res, psiDirectory);
    }
  }

  @NotNull
  @Override
  public String[] getAllClassNames() {
    List<PsiClass> classes = getAllPsiClass(GlobalSearchScope.allScope(project), project);
    List<String> names = new ArrayList<String>(classes.size());
    for (PsiClass aClass : classes) {
      names.add(aClass.getName());
    }
    return names.toArray(new String[names.size()]);
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
