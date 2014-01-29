package fr.phan.damapping.intellij.plugin.integration.psiparsing;

import fr.phan.damapping.processor.model.DAEnumValue;
import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.InstantiationType;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.validator.DASourceClassValidator;
import fr.phan.damapping.processor.validator.DASourceClassValidatorImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiReferenceList;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * PsiParsingServiceImpl -
 *
 * @author Sébastien Lesaint
 */
public class PsiParsingServiceImpl implements PsiParsingService {
  private final DASourceClassValidator validator;

  public PsiParsingServiceImpl() {
    this(new DASourceClassValidatorImpl());
  }

  public PsiParsingServiceImpl(DASourceClassValidator validator) {
    this.validator = validator;
  }

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
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  private DAType extractDAType(PsiClass psiClass) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  private DAType extractDAType(PsiClassType psiClassType) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  private DAName extractPackageName(PsiClass psiClass) {
    PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
    PsiPackage pkg = JavaPsiFacade.getInstance(psiClass.getProject()).findPackage(javaFile.getPackageName());
    return DANameFactory.from(pkg.getQualifiedName());
  }

  private Set<DAModifier> extractModifiers(PsiClass psiClass) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  private List<DAInterface> extractInterfaces(PsiClass psiClass) {
    PsiReferenceList implementsList = psiClass.getImplementsList();
    if (implementsList != null /* null for anonymous classes */
        && implementsList.getRole() == PsiReferenceList.Role.IMPLEMENTS_LIST) {
      return FluentIterable.from(Arrays.asList(implementsList.getReferencedTypes()))
          .transform(new Function<PsiClassType, DAInterface>() {
            @Override
            public DAInterface apply(@Nullable PsiClassType psiClassType) {
              return new DAInterface(extractDAType(psiClassType));  //To change body of implemented methods use File | Settings | File Templates.
            }
          })
          .toImmutableList();
    }
    return Collections.emptyList();
  }


  private List<DAMethod> extractMethods(PsiClass psiClass) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  private InstantiationType computeClassInstantiationType(PsiClass psiClass) {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }
}
