package fr.phan.damapping.processor.impl.filegenerator;

import fr.phan.damapping.processor.impl.filegenerator.FileGenerator;
import fr.phan.damapping.processor.impl.filegenerator.FileGeneratorContext;

import java.io.IOException;

/**
* SourceWriterDelegate -
*
* @author: Sébastien Lesaint
*/
public interface SourceWriterDelegate {
    void generateFile(FileGenerator fileGenerator, FileGeneratorContext context) throws IOException;
}
