/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiContext;
import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiTypeElementUtil;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
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

  private static final String JAVA_LANG_QUALIFIED_NAME_PREFIX = "java.lang.";

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

  @Nonnull
  @Override
  public DAName simpleName(PsiTypeElement psiTypeElement) {
    Optional<PsiIdentifier> psiIdentifier = PsiTypeElementUtil.getPsiIdentifier(psiTypeElement);
    if (psiIdentifier.isPresent()) {
      // TODO handle case or an array
      return DANameFactory.simpleFromQualified(psiIdentifier.get().getText());
    }
    if (PsiTypeElementUtil.isWildcard(psiTypeElement)) {
      return DANameFactory.wildcard();
    }
    return DANameFactory.from(psiTypeElement.getText());
//    PsiJavaCodeReferenceElement innermostComponentReferenceElement = psiTypeElement.getInnermostComponentReferenceElement();
//    if (innermostComponentReferenceElement == null) { // psiTypeElement is a primitive type
//      return DANameFactory.from(psiTypeElement.getText());
//    }

  }

  @Nullable
  @Override
  public DAName simpleName(PsiJavaCodeReferenceElement referenceElement) {
    return DANameFactory.simpleFromQualified(referenceElement.getReferenceName());
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
  public DAName qualifiedName(@Nonnull final String name, @Nullable PsiContext psiContext) {
    if (isQualified(name)) {
      return DANameFactory.from(name);
    }

    if (psiContext == null) {
      return null;
    }

    Optional<DAName> foundImport = from(Arrays.asList(psiContext.getImportStatements()))
        .filter(new Predicate<PsiImportStatement>() {
          @Override
          public boolean apply(@Nullable PsiImportStatement psiImportStatement) {
            return psiImportStatement != null
                && psiImportStatement.getQualifiedName() != null
                && psiImportStatement.getQualifiedName().endsWith(name);
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

  private static boolean isQualified(@Nonnull String name) {
    return name.indexOf(".") >= 0;
  }

  @Nullable
  @Override
  public DAName interfaceQualifiedName(PsiJavaCodeReferenceElement referenceElement, PsiContext psiContext) {
    Optional<PsiIdentifier> psiIdentifier = PsiTypeElementUtil.getPsiIdentifier(referenceElement);
    Optional<DAName> res = qualifiedName(psiIdentifier, psiContext);
    if (res.isPresent()) {
      return res.get();
    }
    LOGGER.error(String.format("No matching import for interface PsiClassType %s", referenceElement.getQualifiedName()));
    return null;
  }

  @Nullable
  @Override
  public DAName qualifiedName(PsiTypeElement psiTypeElement, PsiContext psiContext) {
    if (PsiTypeElementUtil.isWildcard(psiTypeElement)) {
      return DANameFactory.wildcard();
    }
    if (PsiTypeElementUtil.isVoid(psiTypeElement)) {
      return DANameFactory.voidDAName();
    }

    Optional<PsiIdentifier> psiIdentifier = PsiTypeElementUtil.getPsiIdentifier(psiTypeElement);
    Optional<DAName> res = qualifiedName(psiIdentifier, psiContext);
    if (res.isPresent()) {
      return res.get();
    }
    return DANameFactory.from(psiContext.getPackageName().getName() + "." + psiTypeElement.getText());
  }

  @Nonnull
  Optional<DAName> qualifiedName(Optional<PsiIdentifier> psiIdentifier, PsiContext psiContext) {
    if (psiIdentifier.isPresent()) {
      // FIXME must handle the case of an array
      DAName nameFromImports = qualifiedName(psiIdentifier.get().getText(), psiContext);
      if (nameFromImports != null) {
        return Optional.of(nameFromImports);
      }
      DAName javaLangName = resolveJavaLangQualifiedName(psiIdentifier.get().getText());
      if (javaLangName != null) {
        return Optional.of(javaLangName);
      }
    }
    return Optional.absent();
  }

  @Nullable
  @Override
  public DAName qualifiedName(PsiAnnotation psiAnnotation, PsiContext psiContext) {
    Optional<PsiIdentifier> psiIdentifier = PsiTypeElementUtil.getPsiIdentifier(psiAnnotation.getNameReferenceElement());

    Optional<DAName> res = qualifiedName(psiIdentifier, psiContext);
    if (res.isPresent()) {
      return res.get();
    }
    return DANameFactory.from(psiContext.getPackageName().getName() + "." + removeHeadingAt(psiAnnotation.getText()));
  }

  /**
   * Remove heading "@" char from the specified String
   */
  private static String removeHeadingAt(String text) {
    return text.substring(1);
  }

  @Nullable
  private DAName resolveJavaLangQualifiedName(@Nonnull String simpleName) {
    try {
      Class.forName(JAVA_LANG_QUALIFIED_NAME_PREFIX + simpleName);
      return DANameFactory.from(JAVA_LANG_QUALIFIED_NAME_PREFIX + simpleName);
    } catch (ClassNotFoundException e) {
      LOGGER.debug(simpleName + " is not a class of java.lang");
      return null;
    }
  }
}
