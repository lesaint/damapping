package fr.phan.damapping.processor.impl.filegenerator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;

/**
* JavaxSourceWriterDelegate -
*
* @author: Sébastien Lesaint
*/
public class JavaxSourceWriterDelegate implements SourceWriterDelegate {
    private final ProcessingEnvironment processingEnv;

    public JavaxSourceWriterDelegate(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    @Override
    public void generateFile(FileGenerator fileGenerator, FileGeneratorContext context) throws IOException {
        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                fileGenerator.fileName(context),
                context.getSourceClass().getClassElement()
        );
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "generating " + jfo.toUri());

        fileGenerator.writeFile(new BufferedWriter(jfo.openWriter()), context);
    }
}
