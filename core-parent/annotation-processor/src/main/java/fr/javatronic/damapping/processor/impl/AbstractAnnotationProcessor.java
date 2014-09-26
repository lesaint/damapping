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

import fr.javatronic.damapping.processor.impl.javaxparsing.ProcessingEnvironmentWrapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * AbstractAnnotationProcessor -
 *
 * @author lesaint
 */
public abstract class AbstractAnnotationProcessor<T extends Annotation> implements AnnotationProcessor {
  protected final ProcessingEnvironmentWrapper processingEnv;
  private final Class<T> annotationType;

  protected AbstractAnnotationProcessor(ProcessingEnvironment processingEnv, Class<T> annotationType) {
    this.processingEnv = new ProcessingEnvironmentWrapper(processingEnv);
    this.annotationType = annotationType;
  }

  @Override
  public void processNewElements(final TypeElement annotation, RoundEnvironment roundEnv) {
    Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotationType);
    for (Element element : elementsAnnotatedWith) {
      try {
        processNewElement(element, roundEnv);
      } catch (Exception e) {
        processingEnv.printMessage(annotation, element, e);
        throw new RuntimeException(
            String.format(
                "Exception occured while processing annotation @%s on %s",
                annotation.getSimpleName(),
                element.asType().toString()
            ),
            e
        );
      }
    }
  }

  protected abstract void processNewElement(Element element, RoundEnvironment roundEnv) throws IOException;

}
