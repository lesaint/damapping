package com.ekino.lesaint.dozerannihilation.apt;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.annotation.MapperFactory;
import com.ekino.lesaint.dozerannihilation.annotation.MapperFactoryMethod;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
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
                    Mapper.class.getName(),
                    MapperFactory.class.getName(),
                    MapperFactoryMethod.class.getName()
            );
    private ProcessingEnvironment processingEnvironment;

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
        this.processingEnvironment = processingEnvironment;
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE,
                String.format("typeElements [%s], RoundEnvironment  [%s]", typeElements, roundEnvironment)
        );

        // retourner true pour empêcher que l'annotation soit traitées par un autre Processor
        return false;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
