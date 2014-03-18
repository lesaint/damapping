package fr.javatronic.damapping.processor.impl.javaxparsing;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import com.google.common.base.Optional;

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
