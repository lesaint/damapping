package com.ekino.lesaint.dozerannihilation.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {
    protected MapperAnnotationProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv, Mapper.class);
    }

    @Override
    protected void process(Element element, RoundEnvironment roundEnv) {
        Filer filer = processingEnv.getFiler();
        System.out.println("Processing " + element.getClass().getCanonicalName() + " in " + getClass().getCanonicalName());
    }
}
