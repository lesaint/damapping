package fr.javatronic.damapping.intellij.plugin.integration.psiparsing;

import java.util.Arrays;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiTypeElement;

import static com.google.common.collect.FluentIterable.from;

/**
 * PsiTypeElementUtil -
 *
 * @author Sébastien Lesaint
 */
public final class PsiTypeElementUtil {
  private static final Predicate<PsiElement> EXTENDS_PSIELEMENT_TEXT_PREDICATE = Predicates.compose(
      Predicates.equalTo("extends"), PsiElementToText.INSTANCE
  );
  private static final Predicate<PsiElement> WILDCARD_PSIELEMENT_TEXT_PREDICATE = Predicates.compose(
      Predicates.equalTo("?"), PsiElementToText.INSTANCE
  );
  private static final Predicate<PsiElement> VOID_PSIELEMENT_TEXT_PREDICATE = Predicates.compose(
      Predicates.equalTo("void"), PsiElementToText.INSTANCE
  );

  private PsiTypeElementUtil() {
    // prevents instanciation
  }

  public static Optional<PsiIdentifier> getPsiIdentifier(PsiTypeElement psiTypeElement) {
    PsiJavaCodeReferenceElement referenceElement = psiTypeElement.getInnermostComponentReferenceElement();
    if (referenceElement == null) { // type natif, so very unlkely, will be boxed
      return Optional.absent();
    }
    return from(Arrays.asList(referenceElement.getChildren())).filter(PsiIdentifier.class).first();
  }

  public static boolean isVoid(PsiTypeElement psiTypeElement) {
    PsiElement[] children = psiTypeElement.getChildren();
    return children.length >= 1 && "void".equals(children[0].getText());
  }

  public static boolean isWildcard(PsiTypeElement psiTypeElement) {
    PsiElement[] children = psiTypeElement.getChildren();
    return children.length >= 1 && "?".equals(children[0].getText());
  }

  public static boolean isExtendsWildcard(PsiTypeElement psiTypeElement) {
    return isWildcard(psiTypeElement)
        && psiTypeElement.getChildren().length >= 3
        && "extends".equals(psiTypeElement.getChildren()[2].getText());
  }

  public static boolean isArray(PsiTypeElement psiTypeElement) {
    PsiElement[] children = psiTypeElement.getChildren();
    if (children.length == 3) {
      return isArray(children, 0);
    }
    if (children.length == 4) {
      return isArray(children, 1);
    }
    return false;
  }

  private static boolean isArray(PsiElement[] children, int offset) {
    return "[".equals(children[offset + 1].getText())
        && "]".equals(children[offset + 2].getText());
  }

  private static enum PsiElementToText implements Function<PsiElement, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(@Nullable PsiElement psiElement) {
      if (psiElement == null) {
        return null;
      }
      return psiElement.getText();
    }

//    @Override
//    public String apply(@Nullable PsiElement psiElement) {
//      return psiElement != null && "extends".equals(psiElement.getText());
//    }
  }
}
