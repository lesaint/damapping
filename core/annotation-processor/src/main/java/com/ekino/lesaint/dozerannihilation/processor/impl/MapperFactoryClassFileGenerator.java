package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Resource;
import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;

/**
* MapperFactoryClassFileGenerator -
*
* @author Sébastien Lesaint
*/
class MapperFactoryClassFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperFactoryClassDAType().qualifiedName.getName();
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAMapperClass daMapperClass = context.getMapperClass();

        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(daMapperClass.packageName)
                .appendImports(context.getMapperFactoryImports())
                .appendWarningComment();

        // générer la factory
        //     -> nom du package
        //     -> nom de la classe (infère nom de la factory et nom du Mapper)
        //     -> type d'instantiation (si enum, le nom de la valeur d'enum à utiliser)
        DAClassWriter<DAFileWriter> classWriter = fileWriter.newClass(context.getMapperFactoryClassDAType())
                .start();
        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            classWriter.newProperty("instance", DATypeFactory.declared(daMapperClass.type.qualifiedName.getName()))
                    .withModifier(ImmutableSet.of(Modifier.PRIVATE))
                    .withAnnotations(ImmutableList.of(DATypeFactory.from(Resource.class)))
                    .write();
        }

        DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter.newMethod("instance", DATypeFactory.declared(daMapperClass.type.qualifiedName.getName()))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .start();
        switch (daMapperClass.instantiationType) {
            case SINGLETON_ENUM:
                // TOIMPROVE générer le code de la factory dans le cas enum avec un nom d'enum dynamique
                methodWriter.newStatement()
                        .start()
                        .append("return ").append(daMapperClass.type.simpleName).append(".INSTANCE")
                        .end();
                break;
            case CONSTRUCTOR:
                methodWriter.newStatement()
                        .start()
                        .append("return ").append("new ").append(daMapperClass.type.simpleName).appendParamValues(Collections.<DAParameter>emptyList())
                        .end();
                break;
            case SPRING_COMPONENT:
                methodWriter.newStatement()
                        .start()
                        .append("return ").append("instance")
                        .end();
                break;
        }

        methodWriter.end();

        classWriter.end();

        fileWriter.end();
    }
}
