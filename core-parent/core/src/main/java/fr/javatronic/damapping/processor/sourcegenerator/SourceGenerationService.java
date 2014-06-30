package fr.javatronic.damapping.processor.sourcegenerator;

import java.io.IOException;
import javax.annotation.Nonnull;

/**
 * SourceGenerationService -
 *
 * @author Sébastien Lesaint
 */
public interface SourceGenerationService {

  void generateAll(@Nonnull GenerationContext generationContext,
                   @Nonnull SourceWriterDelegate delegate) throws IOException;

  void generate(@Nonnull GenerationContext generationContext,
                @Nonnull String key,
                @Nonnull SourceWriterDelegate delegate) throws IOException;

}
