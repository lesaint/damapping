package fr.javatronic.damapping.processor.impl;

import fr.javatronic.damapping.processor.sourcegenerator.GeneratedFileDescriptor;
import fr.javatronic.damapping.processor.sourcegenerator.SourceWriterDelegate;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * JavaxSourceWriterDelegate -
 *
 * @author Sébastien Lesaint
 */
public class JavaxSourceWriterDelegate implements SourceWriterDelegate {
  private final ProcessingEnvironment processingEnv;
  private final TypeElement contextElement;

  public JavaxSourceWriterDelegate(ProcessingEnvironment processingEnv, TypeElement contextElement) {
    this.processingEnv = processingEnv;
    this.contextElement = contextElement;
  }

  @Override
  public void generateFile(@Nonnull GeneratedFileDescriptor descriptor) throws IOException {
    JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
        descriptor.getType().getQualifiedName().getName(),
        contextElement
    );
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "generating " + jfo.toUri());

    descriptor.getSourceGenerator().writeFile(new BufferedWriter(jfo.openWriter()), descriptor);
  }
}
