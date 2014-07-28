package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.model.InstantiationType;
import fr.javatronic.damapping.util.Sets;

import java.io.IOException;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * SourceGenerationService -
 *
 * @author Sébastien Lesaint
 */
public class SourceGenerationServiceImpl implements SourceGenerationService {
  private static final Set<InstantiationType> MAPPER_FACTORY_CLASS_INTANTIATIONTYPES =
      Sets.of(InstantiationType.CONSTRUCTOR, InstantiationType.SINGLETON_ENUM);
  private static final Set<InstantiationType> MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES =
      Sets.of(InstantiationType.CONSTRUCTOR_FACTORY, InstantiationType.STATIC_FACTORY);

  @Override
  public void generateAll(@Nonnull GenerationContext generationContext,
                          @Nonnull SourceWriterDelegate delegate) throws IOException {
    for (String key : generationContext.getDescriptorKeys()) {
      generate(generationContext, key, delegate);
    }
  }

  @Override
  public void generate(@Nonnull GenerationContext generationContext,
                       @Nonnull String key,
                       @Nonnull SourceWriterDelegate delegate) throws IOException {
    GeneratedFileDescriptor descriptor = generationContext.getDescriptor(key);
    if (descriptor == null) {
      return;
    }

    delegate.generateFile(descriptor);
  }

}
