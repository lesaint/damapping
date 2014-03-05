package fr.phan.damapping.intellij.plugin.integration.psiparsing;

import fr.phan.damapping.processor.model.DAEnumValue;
import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.DATypeKind;
import fr.phan.damapping.processor.model.InstantiationType;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.predicate.DAMethodPredicates;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiWildcardType;
import com.intellij.psi.impl.source.PsiClassReferenceType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.FluentIterable.from;

/**
 * PsiParsingServiceImpl -
 *
 * @author Sébastien Lesaint
 */
public class PsiParsingServiceImpl implements PsiParsingService {
  private static final Logger LOGGER = Logger.getInstance(PsiParsingServiceImpl.class.getName());

  @Override
  public DASourceClass parse(PsiClass psiClass) {
    checkArgument(!psiClass.isAnnotationType(), "Annotation annoted with @Mapper is not supported");
    checkArgument(!psiClass.isInterface(), "Interface annoted with @Mapper is not supported");
    if (psiClass.isEnum()) {
      return parseEnum(psiClass);
    }
    return parseClass(psiClass);
  }

  private DASourceClass parseEnum(PsiClass psiClass) {
    return DASourceClass.enumBuilder(extractDAType(psiClass), extractEnumValues(psiClass))
                        .withPackageName(extractPackageName(psiClass))
                        .withModifiers(extractModifiers(psiClass))
                        .withInterfaces(extractInterfaces(psiClass))
                        .withMethods(extractMethods(psiClass))
                        .withInstantiationType(InstantiationType.SINGLETON_ENUM)
                        .build();
  }

  private DASourceClass parseClass(PsiClass psiClass) {
    return DASourceClass.classbuilder(extractDAType(psiClass))
                        .withPackageName(extractPackageName(psiClass))
                        .withModifiers(extractModifiers(psiClass))
                        .withInterfaces(extractInterfaces(psiClass))
                        .withMethods(extractMethods(psiClass))
                        .withInstantiationType(computeClassInstantiationType(psiClass))
                        .build();
  }

  private List<DAEnumValue> extractEnumValues(PsiClass psiClass) {
    return from(Arrays.asList(psiClass.getChildren()))
        .filter(PsiEnumConstant.class)
        .transform(new Function<PsiEnumConstant, DAEnumValue>() {
          @Nullable
          @Override
          public DAEnumValue apply(@Nullable PsiEnumConstant psiEnumConstant) {
            return new DAEnumValue(psiEnumConstant.getName());
          }
        }
        )
        .toImmutableList();
  }

  private DAType extractDAType(PsiClass psiClass) {
    return DAType.builder(DATypeKind.DECLARED, DANameFactory.from(psiClass.getName()))
        .withQualifiedName(DANameFactory.from(psiClass.getQualifiedName())
        ) // FIXME psiClass.getQualifiedName() can return null
        .withTypeArgs(extractTypeArgs(psiClass))
        .withSuperBound(extractSuperBound(psiClass))
        .withExtendsBound(extractExtendsBound(psiClass))
        .build();
  }

  private List<DAType> extractTypeArgs(PsiClass psiClass) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  private DAType extractSuperBound(PsiClass psiClass) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  private DAType extractExtendsBound(PsiClass psiClass) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  @Nullable
  private DAName extractInterfaceQualifiedName(final PsiClassType psiClassType, PsiImportList psiImportList) {
    final String simpleName = psiClassType.getClassName();
    DAName nameFromImports = extractQualifiedName(simpleName, psiImportList);
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

  private DATypeKind extractDATypeKind(PsiClassType psiClassType) {
    if (psiClassType.getArrayDimensions() > 0) {
      return DATypeKind.ARRAY;
    }
    return DATypeKind.DECLARED;
  }

  private DAName extractPackageName(PsiClass psiClass) {
    PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
    return DANameFactory.from(javaFile.getPackageName());
  }

  private Set<DAModifier> extractModifiers(PsiClass psiClass) {
    return toDAModifierSet(psiClass.getModifierList());
  }

  private List<DAInterface> extractInterfaces(final PsiClass psiClass) {
    PsiReferenceList implementsList = psiClass.getImplementsList();
    if (implementsList != null /* null for anonymous classes */
        && implementsList.getRole() == PsiReferenceList.Role.IMPLEMENTS_LIST) {
      return from(Arrays.asList(implementsList.getReferencedTypes()))
          .transform(new Function<PsiClassType, DAInterface>() {
            @Override
            public DAInterface apply(@Nullable PsiClassType psiClassType) {
              return new DAInterface(extractInterfaceDAType(psiClass, psiClassType));
            }
          }
          )
          .toImmutableList();
    }
    return Collections.emptyList();
  }

  private DAType extractInterfaceDAType(PsiClass psiClass, PsiClassType psiClassType) {
    PsiImportList psiImportList = extractPsiImportList(psiClass);
    return DAType.builder(extractDATypeKind(psiClassType), DANameFactory.from(psiClassType.getClassName()))
                 .withQualifiedName(extractInterfaceQualifiedName(psiClassType, psiImportList))
                 .withTypeArgs(extractTypeArgs(psiClassType, psiImportList))
                 .build();
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

  private DAType extractDAType(PsiType psiType, PsiImportList psiImportList) {
    DAName simpleName = extractSimpleName(psiType);
    return DAType.builder(extractDATypeKind(psiType), simpleName)
                 .withQualifiedName(extractQualifiedName(simpleName.getName(), psiImportList))
                 .withTypeArgs(extractTypeArgs(psiType, psiImportList))
                 .withExtendsBound(extractExtendsBound(psiType, psiImportList))
                 .withSuperBound(extractSuperBound(psiType, psiImportList))
                 .build();
  }

  private DAName extractQualifiedName(final String simpleName, PsiImportList psiImportList) {
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

  private DAType extractSuperBound(PsiType psiType, PsiImportList psiImportList) {
    if (!(psiType instanceof PsiWildcardType)) {
      return null;
    }

    PsiWildcardType psiWildcardType = (PsiWildcardType) psiType;
    if (!psiWildcardType.isExtends()) {
      return extractDAType(psiWildcardType.getBound(), psiImportList);
    }
    return null;
  }

  private DAType extractExtendsBound(PsiType psiType, PsiImportList psiImportList) {
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

  private List<DAType> extractTypeArgs(PsiType psiType, PsiImportList psiImportList) {
    if (psiType instanceof PsiClassType) {
      return extractTypeArgs((PsiClassType) psiType, psiImportList);
    }
    if (psiType instanceof PsiArrayType || psiType instanceof PsiPrimitiveType ||
        psiType instanceof PsiWildcardType) {
      return Collections.emptyList();
    }
    throw new IllegalArgumentException("Huhu, PsiType is not a PsiClassType ?! fix it then !");
  }

  private DAName extractSimpleName(PsiType psiType) {
    if (psiType instanceof PsiClassReferenceType) {
      return DANameFactory.from(((PsiClassReferenceType) psiType).getClassName());
    }
    if (psiType instanceof PsiArrayType) {
      return extractSimpleName(((PsiArrayType) psiType).getComponentType());
    }
    if (psiType instanceof PsiWildcardType) {
      return DANameFactory.wildcard();
    }
    return DANameFactory.from(psiType.getCanonicalText());
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

  @Nullable
  private PsiImportList extractPsiImportList(PsiClass psiClass) {
    return from(Arrays.asList(psiClass.getParent().getChildren()))
        .filter(PsiImportList.class)
        .first()
        .orNull();
  }


  private List<DAMethod> extractMethods(PsiClass psiClass) {
    final PsiImportList psiImportList = extractPsiImportList(psiClass);
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
                .withModifiers(extractModifiers(psiMethod))
                .withParameters(extractParameters(psiMethod, psiImportList))
                .withReturnType(extractReturnType(psiMethod, psiImportList))
                .withMapperFactoryMethod(isMapperFactoryMethod(psiMethod))
                .withMapperMethod(isMapperMethod(psiMethod))
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
    if (!Iterables.any(daMethods, DAMethodPredicates.isDefaultConstructor())) {
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
        .withReturnType(extractDAType(psiClass))
        .build();
  }

  private Set<DAModifier> extractModifiers(PsiMethod psiMethod) {
    return toDAModifierSet(psiMethod.getModifierList());
  }

  private static Set<DAModifier> toDAModifierSet(@Nullable PsiModifierList modifierList) {
    if (modifierList == null) {
      return Collections.emptySet();
    }

    return from(Arrays.asList(modifierList.getChildren()))
        .filter(PsiKeyword.class)
        .transform(PsiKeywordToDAModifier.INSTANCE)
        .toImmutableSet();
  }

  private static enum PsiKeywordToDAModifier implements Function<PsiKeyword, DAModifier> {
    INSTANCE;

    private static final Map<String, DAModifier> PSIKEYWORD_DAMODIFIER_MAP = buildPsiKeywordToDAModifierMap();

    private static Map<String, DAModifier> buildPsiKeywordToDAModifierMap() {
      ImmutableMap.Builder<String, DAModifier> builder = ImmutableMap.<String, DAModifier>builder();
      for (DAModifier daModifier : DAModifier.values()) {
        builder.put(daModifier.name().toLowerCase(Locale.US), daModifier);
      }
      return builder.build();
    }

    @Nullable
    @Override
    public DAModifier apply(@Nullable PsiKeyword psiKeyword) {
      if (psiKeyword == null) {
        return null;
      }
      return PSIKEYWORD_DAMODIFIER_MAP.get(psiKeyword.getText());
    }
  }

  ;

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
                    extractParameterDAType(psiParameter, psiImportList)
                ).withModifiers(extractModifiers(psiParameter))
                .build();
          }
        }
        ).toImmutableList();
  }

  private Set<DAModifier> extractModifiers(PsiParameter psiParameter) {
    Optional<PsiModifierList> modifiers = from(Arrays.asList(psiParameter.getChildren())).filter(PsiModifierList.class)
        .first();
    if (modifiers.isPresent()) {
      return toDAModifierSet(modifiers.get());
    }
    return Collections.<DAModifier>emptySet();
  }

  private DAType extractParameterDAType(PsiParameter psiParameter, PsiImportList psiImportList) {
    Optional<PsiTypeElement> typeElement = from(Arrays.asList(psiParameter.getChildren())).filter(PsiTypeElement.class)
        .first();
    if (!typeElement.isPresent()) {
      throw new IllegalArgumentException("PsiParameter has no PsiTypeElement");
    }
    return extractDAType(typeElement.get(), psiImportList);
  }

  private DAType extractDAType(@Nonnull PsiTypeElement typeElement, PsiImportList psiImportList) {
    return extractDAType(typeElement.getType(), psiImportList);
  }

  private DAType extractReturnType(PsiMethod psiMethod, PsiImportList psiImportList) {
    if (psiMethod.isConstructor()) {
      return null;
    }
    return extractDAType(psiMethod.getReturnTypeElement(), psiImportList);
  }

  private boolean isMapperFactoryMethod(PsiMethod psiMethod) {
    return false;  //To change body of created methods use File | Settings | File Templates.
  }

  private boolean isMapperMethod(PsiMethod psiMethod) {
    return false;  //To change body of created methods use File | Settings | File Templates.
  }

  private InstantiationType computeClassInstantiationType(PsiClass psiClass) {
    return InstantiationType.CONSTRUCTOR;
  }
}
