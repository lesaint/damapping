package com.ekino.lesaint.dozerannihilation.processor;

import javax.annotation.processing.RoundEnvironment;

/**
 * AnnotationProcessor -
 *
 * @author lesaint
 */
public interface AnnotationProcessor {
    void processAll(RoundEnvironment roundEnv);
}
