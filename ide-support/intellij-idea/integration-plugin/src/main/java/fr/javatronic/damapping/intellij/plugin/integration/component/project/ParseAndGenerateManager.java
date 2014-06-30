package fr.javatronic.damapping.intellij.plugin.integration.component.project;

import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiParsingService;
import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl.PsiParsingServiceImpl;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.sourcegenerator.GeneratedFileDescriptor;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContext;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputer;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputerImpl;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.javatronic.damapping.processor.sourcegenerator.SourceWriterDelegate;
import fr.javatronic.damapping.processor.validator.DASourceClassValidator;
import fr.javatronic.damapping.processor.validator.DASourceClassValidatorImpl;
import fr.javatronic.damapping.processor.validator.ValidationError;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import com.google.common.base.Optional;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.codehaus.groovy.runtime.StringBufferWriter;
import org.jetbrains.annotations.NotNull;

/**
 * DAMappingElementFinder - Project component exposing a method to generate the PSiClass of all classes generated from
 * a class annoted with {@link fr.javatronic.damapping.annotation.Mapper}.
 *
 * @author Sébastien Lesaint
 */
public class ParseAndGenerateManager implements ProjectComponent {
  private static final Logger LOGGER = Logger.getInstance(ParseAndGenerateManager.class.getName());

  private final PsiParsingService parsingService;
  private final DASourceClassValidator sourceClassValidator;
  private final GenerationContextComputer generationContextComputer;
  private final SourceGenerationService sourceGenerationService;

  @NotNull
  public static ParseAndGenerateManager getInstance(@NotNull Project project) {
    return project.getComponent(ParseAndGenerateManager.class);
  }

  public ParseAndGenerateManager() {
    this(new PsiParsingServiceImpl(), new DASourceClassValidatorImpl(), new GenerationContextComputerImpl(),
        new SourceGenerationServiceImpl()
    );
  }

  public ParseAndGenerateManager(PsiParsingService parsingService, DASourceClassValidator sourceClassValidator,
                                 GenerationContextComputer generationContextComputer,
                                 SourceGenerationService sourceGenerationService) {
    this.parsingService = parsingService;
    this.sourceClassValidator = sourceClassValidator;
    this.generationContextComputer = generationContextComputer;
    this.sourceGenerationService = sourceGenerationService;
    LOGGER.debug("ParseAndGenerateManager created");
  }

  @NotNull
  public List<PsiClass> getGeneratedPsiClasses(@NotNull PsiClass psiClass, @NotNull GlobalSearchScope scope) {
    Optional<GenerationContext> generationContext = computeGenerationContext(psiClass, scope);
    if (!generationContext.isPresent()) {
      return Collections.emptyList();
    }

    List<PsiClass> res = new ArrayList<PsiClass>(6);
    for (String key : generationContext.get().getDescriptorKeys()) {
      Optional<PsiClass> psiClass1 = getGeneratedPsiClass(generationContext.get(), key, scope.getProject());
      if (psiClass1.isPresent()) {
        res.add(psiClass1.get());
      }
    }

    return res;
  }


  public Optional<PsiClass> getGeneratedPsiClass(GenerationContext generationContext, String key, Project project) {
    PsiClassGeneratorDelegate delegate = new PsiClassGeneratorDelegate(project);
    try {
      sourceGenerationService.generate(generationContext, key, delegate);
      PsiClass generatedPsiClass = delegate.getGeneratedPsiClass();
      if (generatedPsiClass != null) {
        return Optional.of(generatedPsiClass);
      }
    } catch (IOException e) {
      LOGGER.error("Failed to generate source files");
    }
    return Optional.absent();
  }

  @NotNull
  public Optional<GenerationContext> computeGenerationContext(@NotNull PsiClass psiClass,
                                                              @NotNull GlobalSearchScope scope) {
    DASourceClass daSourceClass = parsingService.parse(psiClass);
    try {
      sourceClassValidator.validate(daSourceClass);
    } catch (ValidationError validationError) {
      LOGGER.debug(String.format("Failed to validate class %s", psiClass.getQualifiedName()), validationError);
      return Optional.absent();
    }

    return Optional.of(generationContextComputer.compute(daSourceClass));
  }

  @Override
  public void projectOpened() {
    // do nothing
  }

  @Override
  public void projectClosed() {
    // do nothing
  }

  @Override
  public void initComponent() {
    // do nothing
  }

  @Override
  public void disposeComponent() {
    // do nothing
  }

  @NotNull
  @Override
  public String getComponentName() {
    return this.getClass().getSimpleName();
  }

  private static class PsiClassGeneratorDelegate implements SourceWriterDelegate {
    private final Project project;
    private PsiClass generatedPsiClass;

    private PsiClassGeneratorDelegate(Project project) {
      this.project = project;
    }

    private PsiClass getGeneratedPsiClass() {
      return generatedPsiClass;
    }

    @Override
    public void generateFile(@Nonnull GeneratedFileDescriptor descriptor) throws IOException {
      StringBuffer buffer = new StringBuffer();
      descriptor.getSourceGenerator().writeFile(new BufferedWriter(new StringBufferWriter(buffer)), descriptor);
      // ((PsiJavaFile) PsiFileFactory.getInstance(project).createFileFromText(sourceGenerator.fileName(context),
      // JavaFileType.INSTANCE, buffer.toString(), LocalTimeCounter     .currentTime(), false,
      // false)).getClasses()[0].getImplementsListTypes()
      PsiJavaFile psiJavaFile = (PsiJavaFile) PsiFileFactory.getInstance(project)
                                                            .createFileFromText(
                                                                descriptor.getType().getSimpleName().getName(),
                                                                JavaFileType.INSTANCE, buffer.toString()
                                                            );
      this.generatedPsiClass = psiJavaFile.getClasses()[0];
    }

  }

}
