package fr.phan.damapping.processor.impl.javaxparsing;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * JavaxUtil -
 *
 * @author: Sébastien Lesaint
 */
public interface JavaxUtil {
    @Nonnull
    Optional<AnnotationMirror> getAnnotationMirror(Element classElement,
                                                   Class<? extends Annotation> annotationClass);

    @Nullable
    String getEnumNameElementValue(AnnotationMirror annotationMirror, String elementName);
}
