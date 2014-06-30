package fr.javatronic.damapping.intellij.plugin.integration.provider;

import fr.javatronic.damapping.annotation.Mapper;

import java.util.Arrays;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaCodeReferenceElement;

/**
 * Common -
 *
 * @author Sébastien Lesaint
 */
public class Common {
  private static final String MAPPER_ANNOTATION_TEXT = "@" + Mapper.class.getSimpleName();
  private static final String MAPPER_QUALIFIED_ANNOTATION_TEXT = "@" + Mapper.class.getName();

  public static boolean hasMapperAnnotation(PsiClass psiClass) {
    if (psiClass.getModifierList() == null || psiClass.getModifierList().getAnnotations() == null) {
      return false;
    }

    // look for annotation @Mapper or @com.google.common.base.Function on class
    if (!FluentIterable.from(Arrays.asList(psiClass.getModifierList().getAnnotations()))
                       .filter(new Predicate<PsiAnnotation>() {
                         @Override
                         public boolean apply(@javax.annotation.Nullable PsiAnnotation psiAnnotation) {
                           return psiAnnotation != null
                               && (MAPPER_ANNOTATION_TEXT.equals(psiAnnotation.getText())
                               || MAPPER_QUALIFIED_ANNOTATION_TEXT.equals(psiAnnotation.getText()));
                         }
                       }
                       ).first().isPresent()) {
      return false;
    }

    // look for the import of Guava's Function
    for (PsiElement fileElement : psiClass.getParent().getChildren()) {
      if (fileElement instanceof PsiImportList) {
        for (PsiElement importListElement : fileElement.getChildren()) {
          if (importListElement instanceof PsiImportStatement) {
            for (PsiElement element : importListElement.getChildren()) {
              if (element instanceof PsiJavaCodeReferenceElement) {
                if (Function.class.getName().equals(element.getText())) {
                  return true;
                }
              }
            }
          }

        }
      }
    }
    return false;
  }
}
