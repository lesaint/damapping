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

  private static PsiClass teacherToPeopleMapper;
  private static PsiClass studentToPeopleMapper;

  public static PsiClass generateClass(GlobalSearchScope scope, String name) {
        if (name.contains("TeacherToPeople")) {
          if (teacherToPeopleMapper == null) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) PsiFileFactory.getInstance(scope.getProject())
                                                                  .createFileFromText("TeacherToPeopleMapper.java",
                                                                      JavaFileType.INSTANCE, TeacherToPeopleMapper
                                                                  );

            teacherToPeopleMapper = psiJavaFile.getClasses()[0];
          }
          return teacherToPeopleMapper;
        }

        if (name.contains("StudentToPeople")) {
          if (studentToPeopleMapper == null) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) PsiFileFactory.getInstance(scope.getProject())
                                                                  .createFileFromText("StudentToPeopleMapper.java",
                                                                      JavaFileType.INSTANCE, StudentToPeopleMapper
                                                                  );
            studentToPeopleMapper = psiJavaFile.getClasses()[0];
          }
          return studentToPeopleMapper;
        }

        return null;
      }
}
