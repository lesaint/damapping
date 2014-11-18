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
package fr.javatronic.damapping.processor.impl;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.processor.impl.javaxparsing.JavaxParsingService;
import fr.javatronic.damapping.processor.impl.javaxparsing.JavaxParsingServiceImpl;
import fr.javatronic.damapping.processor.impl.javaxparsing.ParsingResult;
import fr.javatronic.damapping.processor.impl.javaxparsing.ProcessingContext;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContext;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputer;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputerImpl;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.javatronic.damapping.processor.validator.DASourceClassValidator;
import fr.javatronic.damapping.processor.validator.DASourceClassValidatorImpl;
import fr.javatronic.damapping.processor.validator.ValidationError;
import fr.javatronic.damapping.util.Sets;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {

  private static final Set<ElementKind> SUPPORTED_ELEMENTKINDS = Sets.of(
      ElementKind.CLASS, ElementKind.ENUM
  );

  @Nonnull
  private final ProcessingContext processingContext;

  public MapperAnnotationProcessor(ProcessingEnvironment processingEnv, @Nonnull ProcessingContext processingContext) {
    super(processingEnv, Mapper.class);
    this.processingContext = checkNotNull(processingContext);
  }

  @Override
  protected void processNewElement(Element element, RoundEnvironment roundEnv) throws IOException {
    if (!SUPPORTED_ELEMENTKINDS.contains(element.getKind())) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
          String.format(
              "Type %s annoted with @Mapper annotation is not a class nor an enum (kind found = %s)",
              element, element.getKind()
          )
      );
      return;
    }

    TypeElement classElement = (TypeElement) element;

    ParsingResult parsingResult = parse(classElement, null);
    switch (parsingResult.getParsingStatus()) {
      case OK:
        if (validate(parsingResult)) {
          generateFiles(parsingResult);
        }
        break;
      case FAILED:
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            "Parsing failed. DAMapping won't generate any class/interface"
        );
        processingContext.setFailed(parsingResult);
        break;
      case HAS_UNRESOLVED:
        registerPostponedValidationAndGeneration(parsingResult);
        break;
    }
  }

  private ParsingResult parse(TypeElement classElement, @Nullable Collection<DAType> generatedTypes)
      throws IOException {
    JavaxParsingService javaxParsingService = new JavaxParsingServiceImpl(processingEnv);
    return javaxParsingService.parse(classElement, generatedTypes);
  }

  private void generateFiles(ParsingResult parsingResult) throws IOException {
    GenerationContextComputer generationContextComputer = new GenerationContextComputerImpl();
    SourceGenerationService sourceGenerationService = new SourceGenerationServiceImpl();
    GenerationContext generationContext = generationContextComputer.compute(parsingResult.getSourceClass());
    sourceGenerationService.generateAll(
        generationContext,
        new JavaxSourceWriterDelegate(processingEnv.getProcessingEnvironment(), parsingResult.getClassElement())
    );
  }

  private boolean validate(ParsingResult parsingResult) {
    try {
      DASourceClassValidator checker = new DASourceClassValidatorImpl();
      checker.validate(parsingResult.getSourceClass());
      return true;
    } catch (ValidationError e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), parsingResult.getClassElement());
      return false;
    }
  }

  private void registerPostponedValidationAndGeneration(ParsingResult parsingResult) {
    processingEnv.getMessager().printMessage(
        Diagnostic.Kind.NOTE,
        "Parsing found unresolved class/interface references. DAMapping won't generate any class/interface."
    );
    processingContext.addPostponed(parsingResult);
  }

  @Override
  public void processPostponed(boolean lastRound) {
    if (processingContext.getPostponed().isEmpty()) {
      return;
    }

    if (lastRound) {
      // no need to process postponed ParsingResult, file that could be generated from them won't be processed
      // by the compiler anyway => abort
      return;
    }

    for (ParsingResult parsingResult : processingContext.getPostponed()) {
      try {
        processPostponed(parsingResult);
      } catch (IOException e) {
        processingEnv.printMessage(Mapper.class, parsingResult.getClassElement(), e);
        throw new RuntimeException(
            String.format(
                "Exception occured while processing annotation @%s on %s",
                Mapper.class.getSimpleName(),
                parsingResult.getClassElement().asType().toString()
            ),
            e
        );
      }

    }
  }

  private void processPostponed(ParsingResult parsingResult) throws IOException {
    ParsingResult newParsingResult = parse(parsingResult.getClassElement(), processingContext.getGenerated());

    switch (newParsingResult.getParsingStatus()) {
      case OK:
        if (validate(newParsingResult)) {
          generateFiles(newParsingResult);
          processingContext.setSuccessful(parsingResult, newParsingResult);
        }
        else {
          processingContext.setFailed(parsingResult);
        }
        break;
      case FAILED:
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            "Parsing failed. DAMapping won't generate any class/interface"
        );
        if (newParsingResult.getType() != null) {
          processingContext.setFailed(parsingResult);
        }
        break;
      case HAS_UNRESOLVED:
        // do nothing, keep parsingResult as postponed
        break;
    }
  }

}
