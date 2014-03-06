package fr.phan.damapping.processor.model;

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
}
