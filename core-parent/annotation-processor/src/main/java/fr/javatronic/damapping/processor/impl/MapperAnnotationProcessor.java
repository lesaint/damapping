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
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContext;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputer;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputerImpl;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.javatronic.damapping.processor.validator.DASourceClassValidator;
import fr.javatronic.damapping.processor.validator.DASourceClassValidatorImpl;
import fr.javatronic.damapping.processor.validator.ValidationError;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Sets;

import java.io.IOException;
import java.util.Set;
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

  public MapperAnnotationProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv, Mapper.class);
  }

  @Override
  protected void process(Element element, RoundEnvironment roundEnv) throws IOException {
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

//        System.out.println("Processing " + classElement.getQualifiedName() + " in " + getClass().getCanonicalName());

    JavaxParsingService javaxParsingService = new JavaxParsingServiceImpl(processingEnv);
    Optional<DASourceClass> daSourceClass = javaxParsingService.parse(classElement);
    if (!daSourceClass.isPresent()) {
      processingEnv.getMessager().printMessage(
          Diagnostic.Kind.NOTE,
          "Parsing failed. DAMapping won't generate any class/interface"
      );
      return;
    }

    try {
      DASourceClassValidator checker = new DASourceClassValidatorImpl();
      checker.validate(daSourceClass.get());
    } catch (ValidationError e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), classElement);
      return;
    }

    GenerationContextComputer generationContextComputer = new GenerationContextComputerImpl();
    SourceGenerationService sourceGenerationService = new SourceGenerationServiceImpl();
    GenerationContext generationContext = generationContextComputer.compute(daSourceClass.get());
    sourceGenerationService.generateAll(
        generationContext,
        new JavaxSourceWriterDelegate(processingEnv, classElement)
    );
  }

}
