package fr.phan.damapping.processor.sourcegenerator;

import java.io.IOException;

/**
 * SourceWriterDelegate -
 *
 * @author: Sébastien Lesaint
 */
public interface SourceWriterDelegate {
  void generateFile(SourceGenerator sourceGenerator, FileGeneratorContext context) throws IOException;
}
