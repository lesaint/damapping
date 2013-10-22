package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
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
        DAMapperClass daMapperClass = context.getMapperClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(daMapperClass.packageName)
                .appendImports(context.getMapperFactoryImports())
                .appendWarningComment();

        DAInterfaceWriter<DAFileWriter> interfaceWriter = fileWriter.newInterface(context.getMapperFactoryInterfaceDAType().simpleName.getName())
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start();

        DAType mapperClass = DATypeFactory.declared(daMapperClass.type.qualifiedName + "Mapper");
        for (DAMethod method : Iterables.filter(daMapperClass.methods, DAMethodPredicates.isMapperFactoryMethod())) {
            String name = method.isConstructor() ? "instanceByConstructor" : method.name.getName();
            interfaceWriter.newMethod(name, mapperClass)
                    .withParams(method.parameters).write();
        }

        interfaceWriter.end();
        fileWriter.end();
    }
}
