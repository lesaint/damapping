package fr.javatronic.damapping.processor.sourcegenerator;

import java.io.IOException;
import javax.annotation.Nonnull;

/**
 * SourceWriterDelegate -
 *
 * @author Sébastien Lesaint
 */
public interface SourceWriterDelegate {
  void generateFile(@Nonnull GeneratedFileDescriptor descriptor) throws IOException;
}
