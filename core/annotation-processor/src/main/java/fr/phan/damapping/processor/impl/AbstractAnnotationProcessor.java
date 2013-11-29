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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * AbstractAnnotationProcessor -
 *
 * @author lesaint
 */
public abstract class AbstractAnnotationProcessor<T extends Annotation> implements AnnotationProcessor {
    protected final ProcessingEnvironment processingEnv;
    private final Class<T> annotationType;

    protected AbstractAnnotationProcessor(ProcessingEnvironment processingEnv, Class<T> annotationType) {
        this.processingEnv = processingEnv;
        this.annotationType = annotationType;
    }

    @Override
    public void processAll(TypeElement annotation, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotationType);
//        System.out.println("Elements retrieved " + elementsAnnotatedWith);
        for (Element element : elementsAnnotatedWith) {
            try {
                process(element, roundEnv);
            } catch (IOException e) {
                e.printStackTrace();
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Processing of elements annotated with " + annotationType.getCanonicalName() + " failed for " + element.getSimpleName(),
                        element
                );
            }
        }
    }

    protected abstract void process(Element element, RoundEnvironment roundEnv) throws IOException;
}
