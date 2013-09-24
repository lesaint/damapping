package com.ekino.lesaint.dozerannihilation.processor;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.annotation.MapperFactory;
import com.ekino.lesaint.dozerannihilation.annotation.MapperFactoryMethod;
import com.ekino.lesaint.dozerannihilation.processor.impl.AnnotationProcessor;
import com.ekino.lesaint.dozerannihilation.processor.impl.MapperAnnotationProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * DAAnnotationProcessor
 *
 * @author Sébastien Lesaint
 */
public class DAAnnotationProcessor implements Processor {

    private static final Set<String> SUPPORTED_OPTIONS = Collections.emptySet();
    private static final Set<String> SUPPORTED_ANNOTATION_TYPES =
            ImmutableSet.of(
                    Mapper.class.getCanonicalName(),
                    MapperFactory.class.getCanonicalName(),
                    MapperFactoryMethod.class.getCanonicalName()
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
        investigatingLogging(annotations, roundEnv);

        if (roundEnv.processingOver()) {
            return false;
        }

        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        Elements elementUtils = processingEnv.getElementUtils();

        for (TypeElement annotation : annotations) {
            AnnotationProcessor annotationProcessor = annotationProcessors.get(annotation.getQualifiedName().toString());
//            System.out.println("looking up AnnotationProcessor for " + annotation.getQualifiedName() + " in " + annotationProcessors + " found=" + annotationProcessor);
            if (annotationProcessor != null) {
                annotationProcessor.processAll(annotation, roundEnv);
            }
        }
        return true;
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
//            messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Traitement annotation " + te.getQualifiedName());
            System.err.println("Traitement annotation " + te.getQualifiedName());

            for (Element element : roundEnv.getElementsAnnotatedWith(te)) {
//                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "  Traitement element " + element.getSimpleName());
                System.err.println("  Traitement element " + element.getSimpleName());
            }
        }
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
