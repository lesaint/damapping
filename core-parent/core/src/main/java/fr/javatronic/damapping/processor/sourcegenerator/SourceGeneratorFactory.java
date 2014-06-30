package fr.javatronic.damapping.processor.sourcegenerator;

/**
* SourceGeneratorFactory -
*
* @author Sébastien Lesaint
*/
public interface SourceGeneratorFactory {
  SourceGenerator instance(GeneratedFileDescriptor descriptor);
}
