package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.util.Optional;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * JavaxUtilImpl -
 *
 * @author Sébastien Lesaint
 */
public class JavaxUtilImpl implements JavaxUtil {

  @Override
  @Nonnull
  public Optional<AnnotationMirror> getAnnotationMirror(Element classElement,
                                                        Class<? extends Annotation> annotationClass) {
    for (AnnotationMirror annotationMirror : classElement.getAnnotationMirrors()) {
      // TODO put test to identify AnnotationMirror by Class into a Predicate
      if (annotationClass.getCanonicalName().equals(annotationMirror.getAnnotationType().toString())) {
        return Optional.of(annotationMirror);
      }
    }
    return Optional.absent();
  }

  @Override
  @Nullable
  public String getEnumNameElementValue(AnnotationMirror annotationMirror, String elementName) {
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> elementValue : annotationMirror.getElementValues().entrySet()) {
      if (elementName.equals(elementValue.getKey().getSimpleName().toString())) {
        // VariableElement is the type return by getValue() representing an enum constant (see @AnnotationValue)
        VariableElement variableElement = (VariableElement) elementValue.getValue().getValue();
        return variableElement.getSimpleName().toString();
      }
    }
    return null;
  }
}
