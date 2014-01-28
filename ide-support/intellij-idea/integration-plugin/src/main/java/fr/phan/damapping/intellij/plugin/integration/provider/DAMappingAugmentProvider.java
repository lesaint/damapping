package fr.phan.damapping.intellij.plugin.integration.provider;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.intellij.plugin.integration.psiparsing.PsiAnnotationUtil;
import fr.phan.damapping.intellij.plugin.integration.psiparsing.PsiParsingService;
import fr.phan.damapping.intellij.plugin.integration.psiparsing.PsiParsingServiceImpl;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.sourcegenerator.DefaultFileGeneratorContext;
import fr.phan.damapping.processor.sourcegenerator.FileGeneratorContext;
import fr.phan.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.phan.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.phan.damapping.processor.sourcegenerator.SourceGenerator;
import fr.phan.damapping.processor.sourcegenerator.SourceWriterDelegate;
import fr.phan.damapping.processor.validator.DASourceClassValidator;
import fr.phan.damapping.processor.validator.DASourceClassValidatorImpl;
import fr.phan.damapping.processor.validator.ValidationError;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.augment.PsiAugmentProvider;
import org.codehaus.groovy.runtime.StringBufferWriter;
import org.jetbrains.annotations.NotNull;

/**
 * DAMappingAugmentProvider -
 *
 * @author Sébastien Lesaint
 */
public class DAMappingAugmentProvider extends PsiAugmentProvider {
  private static final Logger LOGGER = Logger.getInstance(DAMappingAugmentProvider.class.getName());

  private final PsiParsingService parsingService;
  private final DASourceClassValidator sourceClassValidator;
  private final SourceGenerationService sourceGenerationService;

  public DAMappingAugmentProvider() {
    this(new PsiParsingServiceImpl(), new DASourceClassValidatorImpl(), new SourceGenerationServiceImpl());
  }

  public DAMappingAugmentProvider(PsiParsingService parsingService, DASourceClassValidator sourceClassValidator,
                                  SourceGenerationService sourceGenerationService) {
    this.parsingService = parsingService;
    this.sourceClassValidator = sourceClassValidator;
    this.sourceGenerationService = sourceGenerationService;
    LOGGER.debug("DAMappingAugmentProvider created");
  }

  @NotNull
  @Override
  public <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement element, @NotNull Class<Psi> type) {
    // Expecting that we are only augmenting an PsiClass
    // Don't filter !isPhysical elements or code auto completion will not work
    if (!(element instanceof PsiClass) || !element.isValid()) {
      LOGGER.debug("Not a PsiClass or no valid : " + element);
      return Collections.emptyList();
    }
    // skip processing during index rebuild
    final Project project = element.getProject();
    if (DumbService.getInstance(project).isDumb()) {
      LOGGER.debug("Index rebuilding in progress");
      return Collections.emptyList();
    }
    // TODO skip processing if plugin is disabled
    final PsiClass psiClass = (PsiClass) element;
    if (!PsiAnnotationUtil.isAnnotatedWith(psiClass, Mapper.class)) {
      LOGGER.debug("Class is not annoted with @Mapper");
      return Collections.emptyList();
    }
    PsiJavaFile javaFile = (PsiJavaFile) element.getContainingFile();
    PsiModifierList modifierList = psiClass.getModifierList();
    PsiAnnotation[] annotations = modifierList.getAnnotations();

    String mapperSrc = createMapper(psiClass);
    JavaPsiFacade.getElementFactory(project)
                 .createClassFromText(mapperSrc, element /*TODO verify what is this second argument*/);

//        PsiClass res = new LightClass(JavaPsiFacade.getElementFactory(project).createClass(psiClass.getName() +
// "Mapper"));

    return Collections.emptyList();
  }

  private String createMapper(PsiClass psiClass) {
    DASourceClass daSourceClass = parsingService.parse(psiClass);
    try {
      sourceClassValidator.validate(daSourceClass);
    } catch (ValidationError validationError) {
      // TODO transform ValidationError into a message displayed on the annotation and IDEA's console
      LOGGER.error("Validation failed", validationError);
    }

    try {
      sourceGenerationService.generateSourceFiles(
          new DefaultFileGeneratorContext(daSourceClass),
          new SourceWriterDelegate() {
            @Override
            public void generateFile(SourceGenerator sourceGenerator, FileGeneratorContext context) throws IOException {
              StringBuffer buffer = new StringBuffer();
              sourceGenerator.writeFile(new BufferedWriter(new StringBufferWriter(buffer)), context);
              generatedFile(sourceGenerator.fileName(context), buffer.toString(), context);
            }

          }
      );
    } catch (IOException e) {
      LOGGER.error("Failed to generate source files");
    }

    // convert PsiClass to DASource class
    // use Writer to create String instead of file from DASource class and return it
    return "";
  }

  private void generatedFile(String fileName, String fileContent, FileGeneratorContext context) {
    // TODO feed the new file to
  }
}
