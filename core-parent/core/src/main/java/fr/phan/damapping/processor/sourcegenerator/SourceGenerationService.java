package fr.phan.damapping.processor.sourcegenerator;

import java.io.IOException;
import javax.annotation.Nonnull;

/**
 * SourceGenerationService -
 *
 * @author: Sébastien Lesaint
 */
public interface SourceGenerationService {
  void generateSourceFiles(@Nonnull FileGeneratorContext context, @Nonnull SourceWriterDelegate delegate) throws IOException;

  void generateMapper(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException;

  void generateMapperFactoryClass(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException;

  void generateMapperFactoryInterface(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException;

  void generateMapperFactoryImpl(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException;

  void generateMapperImpl(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException;
}
