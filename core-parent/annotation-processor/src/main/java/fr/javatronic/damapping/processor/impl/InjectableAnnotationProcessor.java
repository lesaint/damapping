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

import fr.javatronic.damapping.annotation.Injectable;
import fr.javatronic.damapping.annotation.Mapper;

import java.io.IOException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.tools.Diagnostic;

/**
 * InjectableAnnotationProcessor -
 *
 * @author Sébastien Lesaint
 */
public class InjectableAnnotationProcessor extends AbstractAnnotationProcessor<Injectable> {

  public InjectableAnnotationProcessor(ProcessingEnvironment processingEnv) {
    super(processingEnv, Injectable.class);
  }

  @Override
  protected void processNewElement(Element element, RoundEnvironment roundEnv) throws IOException {
    if (element.getKind() == ElementKind.INTERFACE) {
      processingEnv.getMessager().printMessage(
          Diagnostic.Kind.ERROR, "@Injectable is not valid on an interface", element
      );
    }
    if (element.getAnnotation(Mapper.class) == null) {
      processingEnv.getMessager().printMessage(
          Diagnostic.Kind.ERROR, "@Injectable must be used on a class or enum also annotated with @Mapper", element
      );
    }
  }

  @Override
  public void processPostponed(boolean lastRound) {
    //no implementation
  }
}
