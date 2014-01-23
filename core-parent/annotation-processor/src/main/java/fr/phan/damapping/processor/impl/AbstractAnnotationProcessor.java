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
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

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
  public void processAll(final TypeElement annotation, RoundEnvironment roundEnv) {
    Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotationType);
    for (Element element : elementsAnnotatedWith) {
      try {
        process(element, roundEnv);
      } catch (Exception e) {
        // In Maven, this message is actually not visible, I don't know yet about Oracle javac or Open JDK
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.ERROR,
            buildErrorMessage(e, annotation),
            element,
            getAnnotationMirror(annotation, element)
        );
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

  /**
   * Produit un message d'erreur indiquant que le traitement de l'annoation {@code annotation} a échoué avec
   * l'exception indiquée et précise la première ligne de la stacktrace si l'information est disponible.
   *
   * @param e          l'{@link Exception} capturée
   * @param annotation un {@link TypeElement} représentation la classe d'une annotation
   *
   * @return une {@link String}
   */
  private static String buildErrorMessage(Exception e, TypeElement annotation) {
    StackTraceElement[] stackTrace = e.getStackTrace();
    StringBuilder builder = new StringBuilder()
        .append("Processing of annotation ")
        .append(annotation.getSimpleName())
        .append(" : ")
        .append(e);
    if (stackTrace.length > 0) {
      builder.append(" at ").append(stackTrace[0]);
    }
    return builder.toString();
  }

  /**
   * Récupère l'AnnotationMirror sur l'Element spécifié qui correspond à l'annotation traitée par
   * l'AnnotationProcessor dont le TypeElement est spécifié.
   * </p>
   * Cela permet de connaitre la ligne dans les sources où se trouver l'annotation traitée et de contextualiser
   * encore plus finement le message d'erreur à la compilation.
   *
   * @param annotation un {@link TypeElement} représentation la classe d'une annotation
   * @param element    un {@link Element} sur lequel est posé l'annotation
   *
   * @return un {@link AnnotationMirror} ou {@code null}
   */
  @Nullable
  private AnnotationMirror getAnnotationMirror(final TypeElement annotation, final Element element) {
    Optional<? extends AnnotationMirror> annotationMirror = FluentIterable
        .from(element.getAnnotationMirrors())
        .filter(new Predicate<AnnotationMirror>() {
          @Override
          public boolean apply(@Nullable AnnotationMirror o) {
            return processingEnv.getTypeUtils().isSameType(o.getAnnotationType(), annotation.asType());
          }
        }
        ).first();
    if (annotationMirror.isPresent()) {
      return annotationMirror.get();
    }
    return null;
  }

  protected abstract void process(Element element, RoundEnvironment roundEnv) throws IOException;
}
