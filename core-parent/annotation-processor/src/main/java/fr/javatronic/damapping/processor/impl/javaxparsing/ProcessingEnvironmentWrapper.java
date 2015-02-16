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
package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.processor.ProcessorClasspathChecker;
import fr.javatronic.damapping.processor.impl.ElementsProcessorClasspathChecker;
import fr.javatronic.damapping.util.FluentIterable;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Preconditions;
import fr.javatronic.damapping.util.Predicate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * ProcessingEnvironmentWrapper - a Wrapper around {@link ProcessingEnvironment} that provides extra methods.
 *
 * @author Sébastien Lesaint
 */
public class ProcessingEnvironmentWrapper {
  @Nonnull
  private final ProcessingEnvironment processingEnvironment;
  @Nonnull
  private final ElementUtils elementUtils;
  @Nonnull
  private final TypeUtils typeUtils;
  @Nonnull
  private final ProcessorClasspathChecker classpathChecker;

  public ProcessingEnvironmentWrapper(@Nonnull ProcessingEnvironment processingEnvironment) {
    this.processingEnvironment = Preconditions.checkNotNull(processingEnvironment);
    this.elementUtils = ElementUtilsFactory.from(processingEnvironment.getElementUtils());
    this.typeUtils = new TypeUtilsImpl(processingEnvironment.getTypeUtils());
    this.classpathChecker = new ElementsProcessorClasspathChecker(processingEnvironment.getElementUtils());
  }

  /**
   * @return the underlying {@link ProcessingEnvironment} provided by the compiler.
   */
  @Nonnull
  public ProcessingEnvironment getProcessingEnvironment() {
    return processingEnvironment;
  }

  /**
   * @return a {@link ProcessorClasspathChecker}
   */
  @Nonnull
  public ProcessorClasspathChecker getClasspathChecker() {
    return classpathChecker;
  }

  public void printMessage(TypeElement annotation, Element element, Exception e) {
    // In Maven, this message is actually not visible, I don't know yet about Oracle javac or Open JDK
    processingEnvironment.getMessager().printMessage(
        Diagnostic.Kind.ERROR,
        buildErrorMessage(e, annotation.getSimpleName().toString()),
        element,
        getAnnotationMirror(annotation, element)
    );
  }

  public void printMessage(Class<? extends Annotation> annotationClass, Element element, Exception e) {
    StringWriter stringWriter = new StringWriter();
    e.printStackTrace(new PrintWriter(stringWriter));
    stringWriter.flush();
    processingEnvironment.getMessager().printMessage(
        Diagnostic.Kind.ERROR,
        buildErrorMessage(e, annotationClass.getSimpleName()) + '\n' + stringWriter.toString(),
        element,
        getAnnotationMirror(annotationClass, element)
    );
  }

  /**
   * Produit un message d'erreur indiquant que le traitement de l'annoation {@code annotation} a échoué avec
   * l'exception indiquée et précise la première ligne de la stacktrace si l'information est disponible.
   *
   * @param e                    l'{@link Exception} capturée
   * @param annotationSimpleName un {@link TypeElement} représentation la classe d'une annotation
   *
   * @return une {@link String}
   */
  private static String buildErrorMessage(Exception e, String annotationSimpleName) {
    StackTraceElement[] stackTrace = e.getStackTrace();
    StringBuilder builder = new StringBuilder()
        .append("Processing of annotation ")
        .append(annotationSimpleName)
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
            return getTypeUtils().isSameType(o.getAnnotationType(), annotation.asType());
          }
        }
        ).first();
    if (annotationMirror.isPresent()) {
      return annotationMirror.get();
    }
    return null;
  }

  @Nullable
  private AnnotationMirror getAnnotationMirror(final Class<? extends Annotation> annotationClass,
                                               final Element element) {
    Optional<? extends AnnotationMirror> annotationMirror = FluentIterable
        .from(element.getAnnotationMirrors())
        .filter(new Predicate<AnnotationMirror>() {
          @Override
          public boolean apply(@Nullable AnnotationMirror o) {
            String qualifiedName = ((QualifiedNameable) (o.getAnnotationType().asElement())).getQualifiedName()
                                                                                            .toString();
            return qualifiedName.equals(annotationClass.getName());
          }
        }
        ).first();
    if (annotationMirror.isPresent()) {
      return annotationMirror.get();
    }
    return null;
  }

  public TypeUtils getTypeUtils() {
    return typeUtils;
  }

  public ElementUtils getElementUtils() {
    return elementUtils;
  }

  public Messager getMessager() {
    return processingEnvironment.getMessager();
  }

}
