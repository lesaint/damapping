package fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl;

import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiParsingService;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAEnumValue;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReferenceList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.FluentIterable.from;

/**
 * PsiParsingServiceImpl -
 *
 * @author Sébastien Lesaint
 */
public class PsiParsingServiceImpl implements PsiParsingService {
//  private static final Logger LOGGER = Logger.getInstance(PsiParsingServiceImpl.class.getName());

  private final DANameExtractor daNameExtractor;
  private final DATypeExtractor daTypeExtractor;
  private final DAModifierExtractor daModifierExtractor;

  public PsiParsingServiceImpl(DANameExtractor daNameExtractor, DATypeExtractor daTypeExtractor,
                               DAModifierExtractor daModifierExtractor) {
    this.daNameExtractor = daNameExtractor;
    this.daTypeExtractor = daTypeExtractor;
    this.daModifierExtractor = daModifierExtractor;
  }

  public PsiParsingServiceImpl() {
    this.daNameExtractor = new DANameExtractorImpl();
    this.daTypeExtractor = new DATypeExtractorImpl(daNameExtractor);
    this.daModifierExtractor = new DAModifierExtractorImpl();
  }

  @Override
  public DASourceClass parse(PsiClass psiClass) {
    checkArgument(!psiClass.isAnnotationType(), "Annotation annoted with @Mapper is not supported");
    checkArgument(!psiClass.isInterface(), "Interface annoted with @Mapper is not supported");

    DASourceClass.Builder builder = daSourceBuilder(psiClass, daTypeExtractor.forClassOrEnum(psiClass));

    PsiImportList psiImportList = extractPsiImportList(psiClass);
    return builder
        .withPackageName(daNameExtractor.extractPackageName(psiClass))
        .withAnnotations(extractAnnotations(psiClass.getModifierList(), psiImportList))
        .withModifiers(daModifierExtractor.extractModifiers(psiClass))
        .withInterfaces(extractInterfaces(psiClass))
        .withMethods(extractMethods(psiClass, psiImportList))
        .build();
  }

  private static DASourceClass.Builder daSourceBuilder(PsiClass psiClass, DAType daType) {
    if (psiClass.isEnum()) {
      return DASourceClass.enumBuilder(daType, extractEnumValues(psiClass));
    }
    else {
      return DASourceClass.classbuilder(daType);
    }
  }

  private static List<DAEnumValue> extractEnumValues(PsiClass psiClass) {
    return from(Arrays.asList(psiClass.getChildren()))
        .filter(PsiEnumConstant.class)
        .transform(PsiEnumConstantDAEnumValue.INSTANCE)
        .filter(Predicates.notNull())
        .toImmutableList();
  }

  private List<DAAnnotation> extractAnnotations(@Nullable PsiModifierList modifierList,
                                                @Nullable final PsiImportList psiImportList) {
    if (modifierList == null) {
      return null;
    }

    return from(Arrays.asList(modifierList.getChildren()))
        .filter(PsiAnnotation.class)
        .transform(new Function<PsiAnnotation, DAAnnotation>() {
          @Nullable
          @Override
          public DAAnnotation apply(@Nullable PsiAnnotation psiAnnotation) {
            DAAnnotation res = new DAAnnotation(
                daTypeExtractor.forAnnotation(psiAnnotation, psiImportList)
            );
            return res;
          }
        }
        )
        .toImmutableList();
  }

  private List<DAInterface> extractInterfaces(final PsiClass psiClass) {
    PsiReferenceList implementsList = psiClass.getImplementsList();
    if (implementsList != null /* null for anonymous classes */
        && implementsList.getRole() == PsiReferenceList.Role.IMPLEMENTS_LIST) {
      final PsiImportList psiImportList = extractPsiImportList(psiClass);
      return from(Arrays.asList(implementsList.getReferencedTypes()))
          .transform(new Function<PsiClassType, DAInterface>() {
            @Override
            public DAInterface apply(@Nullable PsiClassType psiClassType) {
              return new DAInterface(daTypeExtractor.forInterface(psiClassType, psiImportList));
            }
          }
          )
          .toImmutableList();
    }
    return Collections.emptyList();
  }

  @Nullable
  private PsiImportList extractPsiImportList(PsiClass psiClass) {
    return from(Arrays.asList(psiClass.getParent().getChildren()))
        .filter(PsiImportList.class)
        .first()
        .orNull();
  }

  private List<DAMethod> extractMethods(PsiClass psiClass, final PsiImportList psiImportList) {
    List<DAMethod> daMethods = from(Arrays.asList(psiClass.getChildren()))
        .filter(PsiMethod.class)
        .transform(new Function<PsiMethod, DAMethod>() {
          @Nullable
          @Override
          public DAMethod apply(@Nullable PsiMethod psiMethod) {
            if (psiMethod == null) {
              return null;
            }
            return daMethodBuilder(psiMethod)
                .withName(DANameFactory.from(psiMethod.getName()))
                .withAnnotations(extractAnnotations(psiMethod.getModifierList(), psiImportList))
                .withModifiers(daModifierExtractor.extractModifiers(psiMethod))
                .withParameters(extractParameters(psiMethod, psiImportList))
                .withReturnType(daTypeExtractor.forMethod(psiMethod, psiImportList))
                .build();
          }

          private DAMethod.Builder daMethodBuilder(PsiMethod psiMethod) {
            if (psiMethod.isConstructor()) {
              return DAMethod.constructorBuilder();
            }
            return DAMethod.methodBuilder();
          }
        }
        ).toImmutableList();
    if (!Iterables.any(daMethods, DAMethodPredicates.isConstructor())) {
      return ImmutableList.copyOf(
          Iterables.concat(Collections.singletonList(instanceDefaultConstructor(psiClass)), daMethods)
      );
    }
    return daMethods;
  }

  private DAMethod instanceDefaultConstructor(PsiClass psiClass) {
    return DAMethod.constructorBuilder()
                   .withName(DANameFactory.from(psiClass.getName()))
                   .withModifiers(Collections.singleton(DAModifier.PUBLIC))
                   .withReturnType(daTypeExtractor.forClassOrEnum(psiClass))
                   .build();
  }

  private List<DAParameter> extractParameters(PsiMethod psiMethod, final PsiImportList psiImportList) {
    Optional<PsiParameterList> optional = from(Arrays.asList(psiMethod.getChildren())).filter(PsiParameterList.class)
        .first();
    if (!optional.isPresent()) {
      return Collections.emptyList();
    }

    return from(Arrays.asList(optional.get().getParameters()))
        .transform(new Function<PsiParameter, DAParameter>() {
          @Nullable
          @Override
          public DAParameter apply(@Nullable PsiParameter psiParameter) {
            return DAParameter
                .builder(
                    DANameFactory.from(psiParameter.getName()),
                    daTypeExtractor.forParameter(psiParameter, psiImportList)
                ).withModifiers(daModifierExtractor.extractModifiers(psiParameter))
                .build();
          }
        }
        ).toImmutableList();
  }

  private static enum PsiEnumConstantDAEnumValue implements Function<PsiEnumConstant, DAEnumValue> {
    INSTANCE;

    @Nullable
    @Override
    public DAEnumValue apply(@Nullable PsiEnumConstant psiEnumConstant) {
      if (psiEnumConstant == null) {
        return null;
      }
      return new DAEnumValue(psiEnumConstant.getName());
    }
  }
}