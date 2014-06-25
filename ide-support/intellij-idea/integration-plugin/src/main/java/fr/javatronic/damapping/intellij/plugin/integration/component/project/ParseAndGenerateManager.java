package fr.javatronic.damapping.intellij.plugin.integration.component.project;

import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiParsingService;
import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl.PsiParsingServiceImpl;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.sourcegenerator.DefaultFileGeneratorContext;
import fr.javatronic.damapping.processor.sourcegenerator.FileGeneratorContext;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerator;
import fr.javatronic.damapping.processor.sourcegenerator.SourceWriterDelegate;
import fr.javatronic.damapping.processor.validator.DASourceClassValidator;
import fr.javatronic.damapping.processor.validator.DASourceClassValidatorImpl;
import fr.javatronic.damapping.processor.validator.ValidationError;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

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
  private final SourceGenerationService sourceGenerationService;

  @NotNull
  public static ParseAndGenerateManager getInstance(@NotNull Project project) {
    return project.getComponent(ParseAndGenerateManager.class);
  }

  public ParseAndGenerateManager() {
    this(new PsiParsingServiceImpl(), new DASourceClassValidatorImpl(), new SourceGenerationServiceImpl());
  }

  public ParseAndGenerateManager(PsiParsingService parsingService, DASourceClassValidator sourceClassValidator,
                                 SourceGenerationService sourceGenerationService) {
    this.parsingService = parsingService;
    this.sourceClassValidator = sourceClassValidator;
    this.sourceGenerationService = sourceGenerationService;
    LOGGER.debug("ParseAndGenerateManager created");
  }

  @NotNull
  public List<PsiClass> getGeneratedPsiClasses(@NotNull PsiClass psiClass, @NotNull GlobalSearchScope scope) {
    DASourceClass daSourceClass = parsingService.parse(psiClass);
    try {
      sourceClassValidator.validate(daSourceClass);
    } catch (ValidationError validationError) {
      LOGGER.debug(String.format("Failed to validate class %s", psiClass.getQualifiedName()), validationError);
      return Collections.emptyList();
    }

    List<PsiClass> res = new ArrayList<PsiClass>(6);
    try {
      for (PsiClassGeneratorFacade generatorFacade : ImmutableList.of(
          new MapperInterfaceFacade(daSourceClass, scope, sourceGenerationService),
          new MapperFactoryInterfaceGenerator(daSourceClass, scope, sourceGenerationService),
          new MapperImplGenerator(daSourceClass, scope, sourceGenerationService),
          new MapperFactoryImplGenerator(daSourceClass, scope, sourceGenerationService),
          new MapperFactoryClassGenerator(daSourceClass, scope, sourceGenerationService)
      )) {
        Optional<PsiClass> generatePsiClass = generatorFacade.generatePsiClass();
        if (generatePsiClass.isPresent()) {
          res.add(generatePsiClass.get());
        }

      }
    } catch (IOException e) {
      LOGGER.error("Failed to generate source files");
    }

    return res;
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

  private static abstract class PsiClassGeneratorFacade {
    protected final FileGeneratorContext generatorContext;
    protected final GlobalSearchScope scope;
    protected final SourceGenerationService sourceGenerationService;

    private PsiClassGeneratorFacade(DASourceClass daSourceClass, GlobalSearchScope scope,
                                    SourceGenerationService sourceGenerationService) {
      this.scope = scope;
      this.sourceGenerationService = sourceGenerationService;
      this.generatorContext = new DefaultFileGeneratorContext(daSourceClass);
    }

    public Optional<PsiClass> generatePsiClass() throws IOException {
      if (shouldGenerate()) {
        PsiClassGeneratorDelegate delegate = new PsiClassGeneratorDelegate(scope.getProject());
        generate(delegate);
        return Optional.of(delegate.getGeneratedPsiClass());
      }
      return Optional.absent();
    }

    protected abstract boolean shouldGenerate();

    protected abstract void generate(PsiClassGeneratorDelegate delegate) throws IOException;

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
    public void generateFile(SourceGenerator sourceGenerator, FileGeneratorContext context) throws IOException {
      StringBuffer buffer = new StringBuffer();
      sourceGenerator.writeFile(new BufferedWriter(new StringBufferWriter(buffer)), context);
      // ((PsiJavaFile) PsiFileFactory.getInstance(project).createFileFromText(sourceGenerator.fileName(context),
      // JavaFileType.INSTANCE, buffer.toString(), LocalTimeCounter     .currentTime(), false,
      // false)).getClasses()[0].getImplementsListTypes()
      PsiJavaFile psiJavaFile = (PsiJavaFile) PsiFileFactory.getInstance(project)
                                                            .createFileFromText(sourceGenerator.fileName(context),
                                                                JavaFileType.INSTANCE, buffer.toString()
                                                            );
      this.generatedPsiClass = psiJavaFile.getClasses()[0];
    }

  }

  private static class MapperInterfaceFacade extends PsiClassGeneratorFacade {

    private MapperInterfaceFacade(DASourceClass daSourceClass,
                                  GlobalSearchScope scope,
                                  SourceGenerationService sourceGenerationService) {
      super(daSourceClass, scope, sourceGenerationService);
    }

    @Override
    protected boolean shouldGenerate() {
      return true;
    }

    @Override
    protected void generate(PsiClassGeneratorDelegate delegate) throws IOException {
      sourceGenerationService.generateMapperInterface(generatorContext, delegate);
    }
  }

  private static class MapperFactoryInterfaceGenerator extends PsiClassGeneratorFacade {

    public MapperFactoryInterfaceGenerator(DASourceClass daSourceClass,
                                           GlobalSearchScope scope,
                                           SourceGenerationService sourceGenerationService) {
      super(daSourceClass, scope, sourceGenerationService);
    }

    @Override
    protected boolean shouldGenerate() {
      return sourceGenerationService.shouldGenerateMapperFactoryInterface(generatorContext);
    }

    @Override
    protected void generate(PsiClassGeneratorDelegate delegate) throws IOException {
      sourceGenerationService.generateMapperFactoryInterface(generatorContext, delegate);
    }
  }

  private static class MapperImplGenerator extends PsiClassGeneratorFacade {

    public MapperImplGenerator(DASourceClass daSourceClass,
                               GlobalSearchScope scope,
                               SourceGenerationService sourceGenerationService) {
      super(daSourceClass, scope, sourceGenerationService);
    }

    @Override
    protected boolean shouldGenerate() {
      return sourceGenerationService.shouldGenerateMapperImpl(generatorContext);
    }

    @Override
    protected void generate(PsiClassGeneratorDelegate delegate) throws IOException {
      sourceGenerationService.generateMapperImpl(generatorContext, delegate);
    }
  }

  private static class MapperFactoryImplGenerator extends PsiClassGeneratorFacade {

    public MapperFactoryImplGenerator(DASourceClass daSourceClass,
                                      GlobalSearchScope scope,
                                      SourceGenerationService sourceGenerationService) {
      super(daSourceClass, scope, sourceGenerationService);
    }

    @Override
    protected boolean shouldGenerate() {
      return sourceGenerationService.shouldGenerateMapperFactoryImpl(generatorContext);
    }

    @Override
    protected void generate(PsiClassGeneratorDelegate delegate) throws IOException {
      sourceGenerationService.generateMapperFactoryImpl(generatorContext, delegate);
    }
  }

  private static class MapperFactoryClassGenerator extends PsiClassGeneratorFacade {

    public MapperFactoryClassGenerator(DASourceClass daSourceClass,
                                       GlobalSearchScope scope,
                                       SourceGenerationService sourceGenerationService) {
      super(daSourceClass, scope, sourceGenerationService);
    }

    @Override
    protected boolean shouldGenerate() {
      return sourceGenerationService.shouldGenerateMapperFactoryClass(generatorContext);
    }

    @Override
    protected void generate(PsiClassGeneratorDelegate delegate) throws IOException {
      sourceGenerationService.generateMapperFactoryClass(generatorContext, delegate);
    }
  }
}
