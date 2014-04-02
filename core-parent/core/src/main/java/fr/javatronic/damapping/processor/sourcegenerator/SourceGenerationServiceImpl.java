package fr.javatronic.damapping.processor.sourcegenerator;

import java.io.IOException;
import java.util.Set;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableSet;

import fr.javatronic.damapping.processor.model.InstantiationType;

/**
 * SourceGenerationService -
 *
 * @author Sébastien Lesaint
 */
public class SourceGenerationServiceImpl implements SourceGenerationService {
  private static final Set<InstantiationType> MAPPER_FACTORY_CLASS_INTANTIATIONTYPES =
      ImmutableSet.of(InstantiationType.CONSTRUCTOR, InstantiationType.SINGLETON_ENUM);
  private static final Set<InstantiationType> MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES =
      ImmutableSet.of(InstantiationType.CONSTRUCTOR_FACTORY, InstantiationType.STATIC_FACTORY);

  @Override
  public void generateSourceFiles(@Nonnull FileGeneratorContext context, @Nonnull SourceWriterDelegate delegate) throws IOException {
    // 1 - générer l'interface du Mapper
    generateMapperInterface(context, delegate);

    // 2 - générer la factory interface (si @MapperFactoryMethod)
    generateMapperFactoryInterface(context, delegate);
    generateMapperFactoryImpl(context, delegate);

    // 3 - generer la factory class (si pas de @MapperFactoryMethod)
    generateMapperFactoryClass(context, delegate);

    // 3 - générer l'implémentation du Mapper
    generateMapperImpl(context, delegate);
  }

  @Override
  public void generateMapperInterface(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException {
    delegate.generateFile(new MapperSourceGenerator(), context);
  }

  @Override
  public void generateMapperFactoryClass(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException {
    if (shouldGenerateMapperFactoryClass(context)) {
      delegate.generateFile(new MapperFactoryClassSourceGenerator(), context);
    }
  }

  @Override
  public boolean shouldGenerateMapperFactoryClass(FileGeneratorContext context) {
    return MAPPER_FACTORY_CLASS_INTANTIATIONTYPES.contains(context.getSourceClass().getInstantiationType());
  }

  @Override
  public void generateMapperFactoryInterface(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException {
    if (shouldGenerateMapperFactoryInterface(context)) {
      delegate.generateFile(new MapperFactoryInterfaceSourceGenerator(), context);
    }
  }

  @Override
  public void generateMapperFactoryImpl(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException {
    if (shouldGenerateMapperFactoryImpl(context)) {
      delegate.generateFile(new MapperFactoryImplSourceGenerator(), context);
    }
  }

  @Override
  public void generateMapperImpl(FileGeneratorContext context, SourceWriterDelegate delegate) throws IOException {
    if (shouldGenerateMapperImpl(context)) {
      delegate.generateFile(new MapperImplSourceGenerator(), context);
    }
  }

  @Override
  public boolean shouldGenerateMapperFactoryInterface(FileGeneratorContext context) {
    return MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES.contains(context.getSourceClass().getInstantiationType());
  }

  @Override
  public boolean shouldGenerateMapperImpl(FileGeneratorContext context) {
    return !shouldGenerateMapperFactoryInterface(context);
  }

  @Override
  public boolean shouldGenerateMapperFactoryImpl(FileGeneratorContext context) {
    return shouldGenerateMapperFactoryInterface(context);
  }
}
