package fr.javatronic.damapping.processor.model;

import javax.annotation.concurrent.Immutable;

/**
 * DAAnnotation -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAAnnotation {
  private final DAType type;

  public DAAnnotation(DAType type) {
    this.type = type;
  }

  public DAType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAAnnotation that = (DAAnnotation) o;
    if (type == null ? that.type != null : !type.equals(that.type)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return type != null ? type.hashCode() : 0;
  }
}
