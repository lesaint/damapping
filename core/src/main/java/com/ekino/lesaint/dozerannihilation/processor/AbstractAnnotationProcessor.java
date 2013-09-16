package com.ekino.lesaint.dozerannihilation.processor;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

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
    public void processAll(RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotationType);
//        System.out.println("Elements retrieved " + elementsAnnotatedWith);
        for (Element element : elementsAnnotatedWith) {
            process(element, roundEnv);
        }
    }

    protected abstract void process(Element element, RoundEnvironment roundEnv);
}
