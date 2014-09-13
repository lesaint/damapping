/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContext;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputer;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputerImpl;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.javatronic.damapping.processor.validator.DASourceClassValidator;
import fr.javatronic.damapping.processor.validator.DASourceClassValidatorImpl;
import fr.javatronic.damapping.processor.validator.ValidationError;
import fr.javatronic.damapping.util.Maps;
import fr.javatronic.damapping.util.Sets;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {

  private static final Set<ElementKind> SUPPORTED_ELEMENTKINDS = Sets.of(
      ElementKind.CLASS, ElementKind.ENUM
  );

  private final ProcessingContext processingContext = new ProcessingContext();

  public MapperAnnotationProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv, Mapper.class);
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
          processingContext.addSuccessful(parsingResult.getType());
        }
        else {
          processingContext.addFailed(parsingResult.getType());
        }
        break;
      case FAILED:
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            "Parsing failed. DAMapping won't generate any class/interface"
        );
        if (parsingResult.getType() != null) {
          processingContext.addFailed(parsingResult.getType());
        }
        break;
      case HAS_UNRESOLVED:
        registerPostponedValidationAndGeneration(parsingResult);
        break;
    }
  }

  private ParsingResult parse(TypeElement classElement,
                              @Nullable Map<DAType, DAType> fixedResolutions) {
    JavaxParsingService javaxParsingService = new JavaxParsingServiceImpl(processingEnv.getProcessingEnvironment(), fixedResolutions);
    return javaxParsingService.parse(classElement);
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
    // guess the simple name of the source classes of the unresolved types which name can be one of
    // a class/interface generated by DAMapping
    // FIXME filtering based on name isn't fullproof, go instead for generating all classes with a specified
    // annotation, so that our annotation processor can be notified of generated classes and we can index them
    // next, we can fixed unresolved type by looking for the generated type, not the type annnotated with @Mapper
    Map<DAType, String> sourceClassSimpleNameByUnresolved = computeSourceClassSimpleNames(parsingResult);

    // at least one unresolved type is not named as a class/interface generated by DAMapping
    if (parsingResult.getUnresolved().size() != sourceClassSimpleNameByUnresolved.size()) {
      processingContext.setFailed(parsingResult);
      return;
    }

    Map<DAType, DAType> fixedResolutions = Maps.newHashMap();
    boolean atLeastOneUnresolvedIsFailed = false;
    for (Map.Entry<DAType, String> entry : sourceClassSimpleNameByUnresolved.entrySet()) {
      Set<DAType> resolution = processingContext.findSuccessfullBySimpleName(entry.getValue());
      if (!resolution.isEmpty()) {
        if (resolution.size() > 1) {
          // FIXME : more than one mapper found with the specified simpleName, this is a corner case but it should be handled
          // - solution 1 (has holes): use the type in the same package as the current parsingResult
          // - solution 2 (bullet proof): find a way to know the imports in the source file and choose from that
        }
        DAType resolutionDAType = resolution.iterator().next();
        fixedResolutions.put(
            entry.getKey(),
            // FIXME computing qualifiedName as below doesn't work when type has no package
            DATypeFactory.declared(resolutionDAType.getPackageName() + "." + entry.getKey().getSimpleName())
        );
      }

      Set<DAType> failed = processingContext.findFailedBySimpleName(entry.getValue());
      if (!failed.isEmpty()) {
        atLeastOneUnresolvedIsFailed = true;
        break;
      }
    }

    if (atLeastOneUnresolvedIsFailed) {
      processingContext.setFailed(parsingResult);
    }

    if (fixedResolutions.size() != parsingResult.getUnresolved().size()) {
      return;
    }

    ParsingResult newParsingResult = parse(parsingResult.getClassElement(), fixedResolutions);

    switch (newParsingResult.getParsingStatus()) {
      case OK:
        if (validate(newParsingResult)) {
          generateFiles(newParsingResult);
          processingContext.setSuccessful(parsingResult);
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
        if (parsingResult.getType() != null) {
          processingContext.setFailed(parsingResult);
        }
        break;
      case HAS_UNRESOLVED:
        // do nothing, keep parsingResult as postponed
        break;
    }
  }

  private Map<DAType, String> computeSourceClassSimpleNames(ParsingResult parsingResult) {
    Map<DAType, String> res = Maps.newHashMap();
    for (DAType unresolved : parsingResult.getUnresolved()) {
      String sourceClassSimpleName = extractSourceClassSimpleName(unresolved);
      if (sourceClassSimpleName != null) {
        res.put(unresolved, sourceClassSimpleName);
      }
    }
    return res;
  }

  private String extractSourceClassSimpleName(DAType daType) {
    String simpleName = daType.getSimpleName().getName();
    if (!simpleName.contains("Mapper")) {
      return null;
    }
    for (String suffix : Sets.of("Mapper", "MapperImpl", "MapperFactory", "MapperFactoryImpl")) {
      if (simpleName.endsWith(suffix)) {
        return simpleName.substring(0, simpleName.length() - suffix.length());
      }
    }
    return null;
  }

}
