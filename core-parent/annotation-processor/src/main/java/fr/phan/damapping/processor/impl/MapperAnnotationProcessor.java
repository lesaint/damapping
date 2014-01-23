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
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.processor.impl.javaxparsing.JavaxParsingService;
import fr.phan.damapping.processor.impl.javaxparsing.JavaxParsingServiceImpl;
import fr.phan.damapping.processor.model.DASourceClass;

import java.io.IOException;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.google.common.collect.ImmutableSet;
import fr.phan.damapping.processor.sourcegenerator.*;
import fr.phan.damapping.processor.validator.DASourceClassValidator;
import fr.phan.damapping.processor.validator.DASourceClassValidatorImpl;
import fr.phan.damapping.processor.validator.ValidationError;

import static com.google.common.collect.FluentIterable.from;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {

    private static final Set<ElementKind> SUPPORTED_ELEMENTKINDS = ImmutableSet.of(
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
        DASourceClass daSourceClass = javaxParsingService.parse(classElement);

        try {
            DASourceClassValidator checker = new DASourceClassValidatorImpl();
            checker.validate(daSourceClass);
        } catch (ValidationError e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), classElement);
            return;
        }

        SourceWriterDelegate sourceWriterDelegate = new JavaxSourceWriterDelegate(processingEnv, classElement);
        SourceGenerationService sourceGenerationService = new SourceGenerationServiceImpl(sourceWriterDelegate);
        sourceGenerationService.generateSourceFiles(new DefaultFileGeneratorContext(daSourceClass));
    }

}
