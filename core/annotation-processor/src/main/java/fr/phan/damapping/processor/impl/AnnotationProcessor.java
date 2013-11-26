package fr.phan.damapping.processor.impl;

import java.io.IOException;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * AnnotationProcessor -
 *
 * @author lesaint
 */
public interface AnnotationProcessor {
    void processAll(TypeElement annotation, RoundEnvironment roundEnv);
}
