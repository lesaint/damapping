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
package fr.javatronic.damapping.processor;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.processor.impl.AnnotationProcessor;
import fr.javatronic.damapping.processor.impl.GeneratedAnnotationProcessor;
import fr.javatronic.damapping.processor.impl.MapperAnnotationProcessor;
import fr.javatronic.damapping.processor.impl.javaxparsing.ProcessingContext;
import fr.javatronic.damapping.util.Maps;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Sets;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * DAAnnotationProcessor
 *
 * @author Sébastien Lesaint
 */
public class DAAnnotationProcessor implements Processor {

  private static final Set<String> SUPPORTED_OPTIONS = Collections.emptySet();
  private static final Set<String> SUPPORTED_ANNOTATION_TYPES =
      Sets.of(
          Mapper.class.getCanonicalName(),
          Generated.class.getCanonicalName()
      );
  private ProcessingEnvironment processingEnv;
  private final ProcessingContext processingContext = new ProcessingContext();
  private AnnotationProcessor mapperAnnotation;
  private AnnotationProcessor generatedAnnotation;

  @Override
  public Set<String> getSupportedOptions() {
    return SUPPORTED_OPTIONS;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return SUPPORTED_ANNOTATION_TYPES;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public void init(ProcessingEnvironment processingEnvironment) {
    this.processingEnv = processingEnvironment;
    this.mapperAnnotation = new MapperAnnotationProcessor(processingEnv, processingContext);
    this.generatedAnnotation = new GeneratedAnnotationProcessor(processingEnv, processingContext);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    Optional<? extends TypeElement> generated = getTypeElement(annotations, Generated.class.getCanonicalName());
    Optional<? extends TypeElement> mapper = getTypeElement(annotations, Mapper.class.getCanonicalName());

    // process @Generated types
    if (generated.isPresent()) {
      generatedAnnotation.processNewElements(generated.get(), roundEnv);
      mapperAnnotation.processPostponed(roundEnv.processingOver());
    }

    // process @Mapper types
    if (mapper.isPresent()) {
      mapperAnnotation.processNewElements(mapper.get(), roundEnv);
    }

    return true;
  }

  private static <T extends TypeElement> Optional<T> getTypeElement(Set<T> annotations, final String canonicalName) {
    return from(annotations).firstMatch(new Predicate<T>() {
      @Override
      public boolean apply(@Nullable T o) {
        return o.getQualifiedName().contentEquals(canonicalName);
      }
    });
  }

  @Override
  public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror,
                                                       ExecutableElement executableElement, String s) {
    return null;
  }
}
