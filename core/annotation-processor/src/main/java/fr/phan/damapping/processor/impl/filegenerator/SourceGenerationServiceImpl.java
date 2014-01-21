package fr.phan.damapping.processor.impl.filegenerator;

import com.google.common.collect.ImmutableSet;
import fr.phan.damapping.processor.model.InstantiationType;

import java.io.IOException;
import java.util.Set;

/**
 * SourceGenerationService -
 *
 * @author: Sébastien Lesaint
 */
public class SourceGenerationServiceImpl implements SourceGenerationService {
    private static final Set<InstantiationType> MAPPER_FACTORY_CLASS_INTANTIATIONTYPES =
            ImmutableSet.of(InstantiationType.CONSTRUCTOR, InstantiationType.SINGLETON_ENUM);
    private static final Set<InstantiationType> MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES =
            ImmutableSet.of(InstantiationType.CONSTRUCTOR_FACTORY, InstantiationType.STATIC_FACTORY);

    private final SourceWriterDelegate delegate;

    public SourceGenerationServiceImpl(SourceWriterDelegate delegate) {
        this.delegate = delegate;
    }

    public void generateSourceFiles(DefaultFileGeneratorContext context) throws IOException {
        // 1 - générer l'interface du Mapper
        generateMapper(context);

        // 2 - générer la factory interface (si @MapperFactoryMethod)
        generateMapperFactoryInterface(context);
        generateMapperFactoryImpl(context);

        // 3 - generer la factory class (si pas de @MapperFactoryMethod)
        generateMapperFactoryClass(context);

        // 3 - générer l'implémentation du Mapper
        generateMapperImpl(context);
    }

    @Override
    public void generateMapper(FileGeneratorContext context) throws IOException {
        delegate.generateFile(new MapperFileGenerator(), context);
    }

    @Override
    public void generateMapperFactoryClass(FileGeneratorContext context) throws IOException {
        if (shouldGenerateMapperFactoryClass(context)) {
            delegate.generateFile(new MapperFactoryClassFileGenerator(), context);
        }
    }

    private boolean shouldGenerateMapperFactoryClass(FileGeneratorContext context) {
        return MAPPER_FACTORY_CLASS_INTANTIATIONTYPES.contains(context.getSourceClass().getInstantiationType());
    }

    @Override
    public void generateMapperFactoryInterface(FileGeneratorContext context) throws IOException {
        if (shouldGenerateMapperFactoryInterface(context)) {
            delegate.generateFile(new MapperFactoryInterfaceFileGenerator(), context);
        }
    }

    @Override
    public void generateMapperFactoryImpl(FileGeneratorContext context) throws IOException {
        if (shouldGenerateMapperFactoryInterface(context)) {
            delegate.generateFile(new MapperFactoryImplFileGenerator(), context);
        }
    }

    @Override
    public void generateMapperImpl(FileGeneratorContext context) throws IOException {
        if (!shouldGenerateMapperFactoryInterface(context)) {
            delegate.generateFile(new MapperImplFileGenerator(), context);
        }
    }

    private boolean shouldGenerateMapperFactoryInterface(FileGeneratorContext context) {
        return MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES.contains(context.getSourceClass().getInstantiationType());
    }
}
