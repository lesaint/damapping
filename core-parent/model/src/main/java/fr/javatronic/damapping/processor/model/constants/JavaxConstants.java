package fr.javatronic.damapping.processor.model.constants;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * JAVA_LANG_CONSTANTS -
 *
 * @author Sébastien Lesaint
 */
public final class JavaxConstants {
  public static final DAType NULLABLE_TYPE = DATypeFactory.from(Nullable.class);
  public static final DAAnnotation NULLABLE_ANNOTATION = new DAAnnotation(NULLABLE_TYPE);

  public static final DAType RESOURCE_TYPE = DATypeFactory.from(Resource.class);
  public static final DAAnnotation RESOURCE_ANNOTATION = new DAAnnotation(RESOURCE_TYPE);

  private JavaxConstants() {
    // prevents instantiation
  }

}
