package fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiWildcardType;

import static com.google.common.collect.FluentIterable.from;

/**
 * DATypeExtractorImpl -
 *
 * @author Sébastien Lesaint
 */
public class DATypeExtractorImpl implements DATypeExtractor {

  private final DANameExtractor DANameExtractor;

  public DATypeExtractorImpl(DANameExtractor DANameExtractor) {
    this.DANameExtractor = DANameExtractor;
  }

  @Override
  @Nonnull
  public DAType forClassOrEnum(PsiClass psiClass) {
    return DAType.builder(DATypeKind.DECLARED, DANameFactory.from(psiClass.getName()))
                 .withQualifiedName(DANameExtractor.qualifiedName(psiClass))
                 .withTypeArgs(extractTypeArgs(psiClass))
                 .withSuperBound(extractSuperBound(psiClass))
                 .withExtendsBound(extractExtendsBound(psiClass))
                 .build();
  }

  /**
   * Extracts the super bounds of the class annoted/enum with @Mapper.
   * No implementation since this information is not relevant to DAMapping class generation yet.
   */
  private DAType extractSuperBound(PsiClass psiClass) {
    // not used yet in DAMapping
    return null;
  }

  /**
   * Extracts the extends bounds of the class annoted/enum with @Mapper.
   * No implementation since this information is not relevant to DAMapping class generation yet.
   */
  private DAType extractExtendsBound(PsiClass psiClass) {
    // not used yet in DAMapping
    return null;
  }

  /**
   * Extracts type arguments (ie. generics) of the class annoted/enum with @Mapper.
   * No implementation since this information is not relevant to DAMapping class generation yet.
   */
  private List<DAType> extractTypeArgs(PsiClass psiClass) {
    // not used yet in DAMapping
    return null;
  }

  private DAType extractDAType(@Nonnull PsiTypeElement typeElement, PsiImportList psiImportList) {
    return extractDAType(typeElement.getType(), psiImportList);
  }

  private DAType extractDAType(PsiType psiType, @Nullable PsiImportList psiImportList) {
    DAName simpleName = DANameExtractor.simpleName(psiType);
    return DAType.builder(extractDATypeKind(psiType), simpleName)
                 .withQualifiedName(DANameExtractor.qualifiedName(simpleName.getName(), psiImportList))
                 .withTypeArgs(extractTypeArgs(psiType, psiImportList))
                 .withExtendsBound(extractExtendsBound(psiType, psiImportList))
                 .withSuperBound(extractSuperBound(psiType, psiImportList))
                 .build();
  }

  @Override
  @Nonnull
  public DAType forInterface(PsiClassType psiClassType, PsiImportList psiImportList) {
    return DAType.builder(extractDATypeKind(psiClassType), DANameFactory.from(psiClassType.getClassName()))
                 .withQualifiedName(DANameExtractor.interfaceQualifiedName(psiClassType, psiImportList))
                 .withTypeArgs(extractTypeArgs(psiClassType, psiImportList))
                 .build();
  }

  private DATypeKind extractDATypeKind(PsiType psiType) {
    if (psiType instanceof PsiWildcardType) {
      return DATypeKind.WILDCARD;
    }
    if (psiType.getArrayDimensions() > 0) {
      return DATypeKind.ARRAY;
    }
    return DATypeKind.DECLARED;
  }


  private List<DAType> extractTypeArgs(PsiType psiType, @Nullable PsiImportList psiImportList) {
    if (psiType instanceof PsiClassType) {
      return extractTypeArgs((PsiClassType) psiType, psiImportList);
    }
    if (psiType instanceof PsiArrayType || psiType instanceof PsiPrimitiveType ||
        psiType instanceof PsiWildcardType) {
      return Collections.emptyList();
    }
    throw new IllegalArgumentException("Huhu, PsiType is not a PsiClassType ?! fix it then !");
  }

  private List<DAType> extractTypeArgs(PsiClassType psiClassType, final PsiImportList psiImportList) {

    return from(Arrays.asList(psiClassType.getParameters()))
        .transform(new Function<PsiType, DAType>() {
          @Nullable
          @Override
          public DAType apply(@Nullable PsiType psiType) {
            if (psiType == null) {
              return null;
            }
            DAType daType = extractDAType(psiType, psiImportList);
            return daType;
          }
        }
        ).filter(Predicates.notNull())
        .toImmutableList();
  }

  private DAType extractSuperBound(PsiType psiType, @Nullable PsiImportList psiImportList) {
    if (!(psiType instanceof PsiWildcardType)) {
      return null;
    }

    PsiWildcardType psiWildcardType = (PsiWildcardType) psiType;
    if (!psiWildcardType.isExtends()) {
      return extractDAType(psiWildcardType.getBound(), psiImportList);
    }
    return null;
  }

  private DAType extractExtendsBound(PsiType psiType, @Nullable PsiImportList psiImportList) {
    if (!(psiType instanceof PsiWildcardType)) {
      return null;
    }

    PsiWildcardType psiWildcardType = (PsiWildcardType) psiType;
    if (psiWildcardType.isExtends()) {
      DAType extendsBound = extractDAType(psiWildcardType.getBound(), psiImportList);
      return extendsBound;
    }
    return null;
  }

  @Override
  @Nonnull
  public DAType forParameter(PsiParameter psiParameter, PsiImportList psiImportList) {
    Optional<PsiTypeElement> typeElement = from(Arrays.asList(psiParameter.getChildren())).filter(PsiTypeElement.class)
        .first();
    if (!typeElement.isPresent()) {
      throw new IllegalArgumentException("PsiParameter has no PsiTypeElement");
    }
    return extractDAType(typeElement.get(), psiImportList);
  }

  @Override
  @Nullable
  public DAType forMethod(PsiMethod psiMethod, PsiImportList psiImportList) {
    if (psiMethod.isConstructor()) {
      return null;
    }
    return extractDAType(psiMethod.getReturnTypeElement(), psiImportList);
  }

  @Override
  @Nonnull
  public DAType forAnnotation(PsiAnnotation psiAnnotation, PsiImportList psiImportList) {
    DAName qualifiedName = DANameFactory.from(psiAnnotation.getQualifiedName());
    DAName simpleName = DANameFactory.simpleFromQualified(qualifiedName);
    if (qualifiedName.equals(simpleName)) {
      qualifiedName = DANameExtractor.qualifiedName(simpleName.getName(), psiImportList);
    }
    return DAType.builder(DATypeKind.DECLARED, simpleName)
                 .withQualifiedName(qualifiedName)
                 .build();
  }
}
