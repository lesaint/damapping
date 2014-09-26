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
import fr.javatronic.damapping.annotation.MapperFactory;
import fr.javatronic.damapping.processor.impl.AnnotationProcessor;
import fr.javatronic.damapping.processor.impl.MapperAnnotationProcessor;
import fr.javatronic.damapping.util.Maps;
import fr.javatronic.damapping.util.Sets;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
          MapperFactory.class.getCanonicalName()
      );
  private ProcessingEnvironment processingEnv;
  private Map<String, AnnotationProcessor> annotationProcessors = Maps.newHashMap();

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
    return SourceVersion.RELEASE_7;
  }

  @Override
  public void init(ProcessingEnvironment processingEnvironment) {
    this.processingEnv = processingEnvironment;
    this.annotationProcessors.put(Mapper.class.getCanonicalName(), new MapperAnnotationProcessor(processingEnv));
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    processNewElements(annotations, roundEnv);
    processPostponed(roundEnv.processingOver());

    return true;
  }

  private void processNewElements(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      AnnotationProcessor annotationProcessor = annotationProcessors.get(annotation.getQualifiedName().toString());
//            System.out.println("looking up AnnotationProcessor for " + annotation.getQualifiedName() + " in " +
// annotationProcessors + " found=" + annotationProcessor);
      if (annotationProcessor != null) {
        annotationProcessor.processNewElements(annotation, roundEnv);
      }
    }
  }

  private void processPostponed(boolean lastRound) {
    for (AnnotationProcessor annotationProcessor : annotationProcessors.values()) {
      annotationProcessor.processPostponed(lastRound);
    }
  }

  private void investigatingLogging(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
    // Diagnostic.Kind.WARNING and NOTE ne sont pas affichés dans la console maven, il faut utiliser
    // Diagnostic.Kind.MADATORY_WARNING pour être sûr d'afficher un message visible lorsque le compilateur
    // est lancé avec ses options par défaut. Le message est prefixé d'un "[WARNING] "
    // D'autre part, System.out.println fonctionne sous maven (et forcément System.err aussi) et affiche un message
    // lors du lancement du compilateur sans option particulière. Le message n'a pas de prefix.
    // passer le javac en debug ou verbose ne semble pas permettre l'affichage des Kind.NOTE ou WARNING

    System.out.println("System.out.println message");
    System.err.println("System.err.println message");
    Messager messager = processingEnv.getMessager();

    messager.printMessage(Diagnostic.Kind.NOTE, "Note level message");
    for (TypeElement te : typeElements) {
//            messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Traitement annotation " + te.getQualifiedName
// ());
      System.err.println("Traitement annotation " + te.getQualifiedName());

      for (Element element : roundEnv.getElementsAnnotatedWith(te)) {
//                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "  Traitement element " + element
// .getSimpleName());
        System.err.println("  Traitement element " + element.getSimpleName());
      }
    }
  }

  @Override
  public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror,
                                                       ExecutableElement executableElement, String s) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
