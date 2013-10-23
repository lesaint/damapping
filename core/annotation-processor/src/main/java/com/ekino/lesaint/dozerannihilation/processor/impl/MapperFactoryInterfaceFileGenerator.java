package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;

/**
* MapperFactoryInterfaceFileGenerator - Générateur du fichier source de l'interface MapperFactory générée dans le cas
* où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
*
* @author Sébastien Lesaint
*/
class MapperFactoryInterfaceFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperFactoryInterfaceDAType().qualifiedName.getName();
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DASourceClass sourceClass = context.getSourceClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(sourceClass.packageName)
                .appendImports(context.getMapperFactoryInterfaceImports())
                .appendWarningComment();

        DAInterfaceWriter<DAFileWriter> interfaceWriter = fileWriter.newInterface(context.getMapperFactoryInterfaceDAType().simpleName.getName())
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start();

        DAType mapperClass = DATypeFactory.declared(sourceClass.type.qualifiedName + "Mapper");
        for (DAMethod method : Iterables.filter(sourceClass.methods, DAMethodPredicates.isMapperFactoryMethod())) {
            String name = method.isConstructor() ? "instanceByConstructor" : method.name.getName();
            interfaceWriter.newMethod(name, mapperClass)
                    .withParams(method.parameters).write();
        }

        interfaceWriter.end();
        fileWriter.end();
    }
}
