package fr.javatronic.damapping.processor.sourcegenerator;


import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;

import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * GeneratedFileDescriptor -
 *
 * @author Sébastien Lesaint
 */
public class GeneratedFileDescriptorImpl implements GeneratedFileDescriptor {
  @Nonnull
  private final String key;
  @Nonnull
  private final DAType type;
  @Nonnull
  private final List<DAName> imports;
  @Nonnull
  private final SourceGenerator sourceGenerator;
  @Nonnull
  private final GenerationContext context;

  public GeneratedFileDescriptorImpl(@Nonnull String key, @Nonnull DAType type,
                                     @Nonnull List<DAName> imports,
                                     @Nonnull SourceGeneratorFactory sourceGeneratorFactory,
                                     @Nonnull GenerationContext context) {
    this.key = key;
    this.type = checkNotNull(type);
    this.imports = checkNotNull(imports);
    this.sourceGenerator = sourceGeneratorFactory.instance(this);
    this.context = checkNotNull(context);
  }

  @Override
  @Nonnull
  public String getKey() {
    return key;
  }

  @Override
  @Nonnull
  public DAType getType() {
    return type;
  }

  @Override
  @Nonnull
  public List<DAName> getImports() {
    return imports;
  }

  @Override
  @Nonnull
  public SourceGenerator getSourceGenerator() {
    return sourceGenerator;
  }

  @Override
  @Nonnull
  public GenerationContext getContext() {
    return context;
  }
}
