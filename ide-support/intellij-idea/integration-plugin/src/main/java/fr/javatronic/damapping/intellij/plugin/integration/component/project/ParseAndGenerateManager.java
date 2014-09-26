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
package fr.javatronic.damapping.intellij.plugin.integration.component.project;

import fr.javatronic.damapping.intellij.plugin.integration.provider.Common;
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
import java.util.Set;
import javax.annotation.Nonnull;
import com.google.common.base.Optional;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.psi.util.ParameterizedCachedValueProvider;
import org.codehaus.groovy.runtime.StringBufferWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  public ParseAndGenerateManager(PsiParsingService parsingService,
                                 DASourceClassValidator sourceClassValidator,
                                 GenerationContextComputer generationContextComputer,
                                 SourceGenerationService sourceGenerationService) {
    this.parsingService = parsingService;
    this.sourceClassValidator = sourceClassValidator;
    this.generationContextComputer = generationContextComputer;
    this.sourceGenerationService = sourceGenerationService;
    LOGGER.debug("ParseAndGenerateManager created");
  }


  private static final Key<ParameterizedCachedValue<List<PsiClass>, PsiClass>> DAMAPPING_GENERATED_CLASSES_KEY = Key.create("DAMAPPING_GENERATED_CLASSES");

  @NotNull
  public List<PsiClass> getGeneratedPsiClasses(@NotNull PsiClass psiClass, @NotNull GlobalSearchScope scope) {
    CachedValuesManager manager = CachedValuesManager.getManager(scope.getProject());

//    ParameterizedCachedValue<List<PsiClass>,PsiClass> value = manager.createParameterizedCachedValue(
//        new GeneratedPsiClassCachedValueProvider(), false
//    );
//    return value.getValue(psiClass);

    List<PsiClass> res = manager.getParameterizedCachedValue(psiClass,
        DAMAPPING_GENERATED_CLASSES_KEY, new GeneratedPsiClassCachedValueProvider(), false, psiClass
    );
    return res;

  }

  private Optional<PsiClass> getGeneratedPsiClass(GenerationContext generationContext, String key, Project project) {
    PsiClassWriterDelegate delegate = new PsiClassWriterDelegate(project);
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
  private Optional<GenerationContext> computeGenerationContext(@NotNull PsiClass psiClass) {
    if (!Common.hasMapperAnnotation(psiClass)) {
      return Optional.absent();
    }

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

  private static class PsiClassWriterDelegate implements SourceWriterDelegate {
    private final Project project;
    private PsiClass generatedPsiClass;

    private PsiClassWriterDelegate(Project project) {
      this.project = project;
    }

    private PsiClass getGeneratedPsiClass() {
      return generatedPsiClass;
    }

    @Override
    public void generateFile(@Nonnull GeneratedFileDescriptor descriptor) throws IOException {
      StringBuffer buffer = new StringBuffer();
      descriptor.getSourceGenerator().writeFile(new BufferedWriter(new StringBufferWriter(buffer)));
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

  private class GeneratedPsiClassCachedValueProvider
      implements ParameterizedCachedValueProvider<List<PsiClass>, PsiClass> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<PsiClass>> compute(PsiClass param) {
      Optional<GenerationContext> generationContext = computeGenerationContext(param);
      if (!generationContext.isPresent()) {
        return CachedValueProvider.Result.create(Collections.<PsiClass>emptyList(), param);
      }


      Set<String> keys = generationContext.get().getDescriptorKeys();
      List<PsiClass> res = new ArrayList<PsiClass>(keys.size());
      for (String key : keys) {
        Optional<PsiClass> psiClass1 = getGeneratedPsiClass(generationContext.get(), key, param.getProject());
        if (psiClass1.isPresent()) {
          res.add(psiClass1.get());
        }
      }

      return CachedValueProvider.Result.create(res, param);
    }
  }
}
