package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.util.Optional;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

/**
 * JavaxUtil -
 *
 * @author Sébastien Lesaint
 */
public interface JavaxUtil {
  @Nonnull
  Optional<AnnotationMirror> getAnnotationMirror(Element classElement,
                                                 Class<? extends Annotation> annotationClass);

  @Nullable
  String getEnumNameElementValue(AnnotationMirror annotationMirror, String elementName);
}
