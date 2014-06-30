package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.model.DASourceClass;

import javax.annotation.Nonnull;

/**
 * GenerationContextComputer -
 *
 * @author Sébastien Lesaint
 */
public interface GenerationContextComputer {
  @Nonnull
  GenerationContext compute(@Nonnull DASourceClass sourceClass);
}
