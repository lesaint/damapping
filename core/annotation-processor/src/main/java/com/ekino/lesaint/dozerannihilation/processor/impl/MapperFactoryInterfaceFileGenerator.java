package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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
        return context.getMapperClass().type.qualifiedName.getName() + "MapperFactory";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAMapperClass daMapperClass = context.getMapperClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(daMapperClass.packageName)
                .appendImports(context.getMapperFactoryImports())
                .appendWarningComment();

        DAInterfaceWriter<DAFileWriter> interfaceWriter = fileWriter.newInterface(daMapperClass.type.simpleName + "MapperFactory")
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start();

        ImmutableList<DAMethod> mapperFactoryMethods = FluentIterable.from(daMapperClass.methods).filter(new Predicate<DAMethod>() {
            @Override
            public boolean apply(@Nullable DAMethod daMethod) {
                return daMethod != null && daMethod.mapperFactoryMethod;
            }
        }).toList();

        for (DAMethod method : mapperFactoryMethods) {
            interfaceWriter.newMethod(method.name.getName(), method.returnType)
                    .withParams(method.parameters);
        }

        interfaceWriter.end();
        fileWriter.end();
    }
}
