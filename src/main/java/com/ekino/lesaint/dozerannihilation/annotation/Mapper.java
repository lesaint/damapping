package com.ekino.lesaint.dozerannihilation.annotation;

/**
 * Mapper -
 *
 * @author Sébastien Lesaint
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface Mapper {

    InstantiationMethod value() default InstantiationMethod.ENUM_SINGLETON;
}