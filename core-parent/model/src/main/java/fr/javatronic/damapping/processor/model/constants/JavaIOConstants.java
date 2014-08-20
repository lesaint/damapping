package fr.javatronic.damapping.processor.model.constants;

import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import java.io.Serializable;

/**
 * JavaIOConstants -
 *
 * @author Sébastien Lesaint
 */
public final class JavaIOConstants {
  public static final DAType SERIALIZABLE_TYPE = DATypeFactory.from(Serializable.class);

  private JavaIOConstants() {
    // prevents instantiation
  }
}
