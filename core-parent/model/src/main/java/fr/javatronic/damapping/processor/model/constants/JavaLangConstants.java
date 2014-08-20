package fr.javatronic.damapping.processor.model.constants;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

/**
 * JAVA_LANG_CONSTANTS -
 *
 * @author Sébastien Lesaint
 */
public final class JavaLangConstants {
  private JavaLangConstants() {
    // prevents instantiation
  }

  public static final DAAnnotation OVERRIDE_ANNOTATION = new DAAnnotation(DATypeFactory.from(Override.class));

}
