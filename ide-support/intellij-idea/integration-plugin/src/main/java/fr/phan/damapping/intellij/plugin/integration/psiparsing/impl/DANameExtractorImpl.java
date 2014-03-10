package fr.phan.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.factory.DANameFactory;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiWildcardType;
import com.intellij.psi.impl.source.PsiClassReferenceType;

import static com.google.common.collect.FluentIterable.from;

/**
 * DANameExtractorImpl -
 *
 * @author Sébastien Lesaint
 */
public class DANameExtractorImpl implements DANameExtractor {
  private static final Logger LOGGER = Logger.getInstance(DANameExtractorImpl.class.getName());

  @Override
  @Nonnull
  public DAName extractPackageName(@Nonnull PsiClass psiClass) {
    PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
    return DANameFactory.from(javaFile.getPackageName());
  }

  @Override
  @Nonnull
  public DAName simpleName(@Nonnull PsiType psiType) {
    if (psiType instanceof PsiClassReferenceType) {
      return DANameFactory.from(((PsiClassReferenceType) psiType).getClassName());
    }
    if (psiType instanceof PsiArrayType) {
      return simpleName(((PsiArrayType) psiType).getComponentType());
    }
    if (psiType instanceof PsiWildcardType) {
      return DANameFactory.wildcard();
    }
    return DANameFactory.from(psiType.getCanonicalText());
  }

  /**
   * Extracts the qualifiedName of the PsiClass representing the enum or class annotated with @Mapper.
   * {@link com.intellij.psi.PsiClass#getQualifiedName()} can return {@code null} but in our case, I don't see yet if
   * it is possible. Yet, we currently deal with it by throwing a {@link IllegalArgumentException} if it happens.
   */
  @Override
  @Nonnull
  public DAName qualifiedName(@Nonnull PsiClass psiClass) {
    if (psiClass.getQualifiedName() == null) {
      throw new IllegalArgumentException("null qualifiedName on the class/enum annoted with @Mapper is not supported");
    }
    return DANameFactory.from(psiClass.getQualifiedName());
  }

  @Override
  @Nullable
  public DAName qualifiedName(final String simpleName, @Nullable PsiImportList psiImportList) {
    if (psiImportList == null) {
      return null;
    }

    Optional<DAName> foundImport = from(Arrays.asList(psiImportList.getImportStatements()))
        .filter(new Predicate<PsiImportStatement>() {
          @Override
          public boolean apply(@Nullable PsiImportStatement psiImportStatement) {
            return psiImportStatement != null
                && psiImportStatement.getQualifiedName() != null
                && psiImportStatement.getQualifiedName().endsWith(simpleName);
          }
        }
        )
        .transform(new Function<PsiImportStatement, DAName>() {
          @Nullable
          @Override
          public DAName apply(@Nullable PsiImportStatement psiImportStatement) {
            if (psiImportStatement == null || psiImportStatement.getQualifiedName() == null) {
              return null;
            }
            return DANameFactory.from(psiImportStatement.getQualifiedName());
          }
        }
        )
        .filter(Predicates.notNull())
        .first();
    if (foundImport.isPresent()) {
      return foundImport.get();
    }
    return null;
  }

  @Override
  @Nullable
  public DAName interfaceQualifiedName(PsiClassType psiClassType, PsiImportList psiImportList) {
    String simpleName = psiClassType.getClassName();
    DAName nameFromImports = qualifiedName(simpleName, psiImportList);
    if (nameFromImports != null) {
      return nameFromImports;
    }

    // If implements statement uses qualifiedName, psiClassType should be an instanceof PsiClassReferenceType
    if (psiClassType instanceof PsiClassReferenceType) {
      return DANameFactory.from(((PsiClassReferenceType) psiClassType).getReference().getQualifiedName());
    }
    LOGGER.error(String.format("No matching import for interface PsiClassType %s", simpleName));
    return null;
  }

}
