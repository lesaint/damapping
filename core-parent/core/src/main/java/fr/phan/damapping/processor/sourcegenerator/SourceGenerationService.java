package fr.phan.damapping.processor.sourcegenerator;

import java.io.IOException;

/**
 * SourceGenerationService -
 *
 * @author: Sébastien Lesaint
 */
public interface SourceGenerationService {
  void generateSourceFiles(DefaultFileGeneratorContext context) throws IOException;

  void generateMapper(FileGeneratorContext context) throws IOException;

  void generateMapperFactoryClass(FileGeneratorContext context) throws IOException;

  void generateMapperFactoryInterface(FileGeneratorContext context) throws IOException;

  void generateMapperFactoryImpl(FileGeneratorContext context) throws IOException;

  void generateMapperImpl(FileGeneratorContext context) throws IOException;
}
