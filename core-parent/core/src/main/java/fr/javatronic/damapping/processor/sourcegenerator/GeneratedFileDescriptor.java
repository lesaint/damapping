package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * GeneratedFileDescriptor -
 *
 * @author Sébastien Lesaint
 */
public interface GeneratedFileDescriptor {
  @Nonnull
  String getKey();

  @Nonnull
  DAType getType();

  @Nonnull
  List<DAName> getImports();

  @Nonnull
  SourceGenerator getSourceGenerator();

  @Nonnull
  GenerationContext getContext();
}
