package fr.javatronic.damapping.intellij.plugin.integration.provider;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * Common -
 *
 * @author Sébastien Lesaint
 */
public class Common {
  private static final String StudentToPeopleMapper = "package fr.javatronic.damapping.demo.view.mapper;\n" +
      "\n" +
      "// GENERATED CODE, DO NOT MODIFY, THIS WILL BE OVERRIDE\n" +
      "public interface StudentToPeopleMapper {\n" +
      "\n" +
      "}\n";
  private static final String TeacherToPeopleMapper = "package fr.javatronic.damapping.demo.view.mapper;\n" +
      "\n" +
      "// GENERATED CODE, DO NOT MODIFY, THIS WILL BE OVERRIDE\n" +
      "public interface TeacherToPeopleMapper {\n" +
      "\n" +
      "}\n";

  public static PsiClass generateClass(GlobalSearchScope scope, String name) {
    if (name.contains("TeacherToPeople")) {
      PsiJavaFile psiJavaFile = (PsiJavaFile) PsiFileFactory.getInstance(scope.getProject())
                                                            .createFileFromText("TeacherToPeopleMapper.java",
                                                                JavaFileType.INSTANCE, TeacherToPeopleMapper
                                                            );

      return psiJavaFile.getClasses()[0];
    }

    if (name.contains("StudentToPeople")) {
      PsiJavaFile psiJavaFile = (PsiJavaFile) PsiFileFactory.getInstance(scope.getProject())
                                                            .createFileFromText("StudentToPeopleMapper.java",
                                                                JavaFileType.INSTANCE, StudentToPeopleMapper
                                                            );
      return psiJavaFile.getClasses()[0];
    }

    return null;
  }
}
