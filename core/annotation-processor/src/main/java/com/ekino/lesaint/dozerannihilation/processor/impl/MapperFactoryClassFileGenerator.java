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
        DASourceClass sourceClass = context.getSourceClass();

        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(sourceClass.packageName)
                .appendImports(context.getMapperFactoryClassImports())
                .appendWarningComment();

        // générer la factory
        //     -> nom du package
        //     -> nom de la classe (infère nom de la factory et nom du Mapper)
        //     -> type d'instantiation (si enum, le nom de la valeur d'enum à utiliser)
        DAClassWriter<DAFileWriter> classWriter = fileWriter.newClass(context.getMapperFactoryClassDAType())
                .start();
        if (sourceClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            classWriter.newProperty("instance", DATypeFactory.declared(sourceClass.type.qualifiedName.getName()))
                    .withModifier(ImmutableSet.of(Modifier.PRIVATE))
                    .withAnnotations(ImmutableList.of(DATypeFactory.from(Resource.class)))
                    .write();
        }

        DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter.newMethod("instance", DATypeFactory.declared(sourceClass.type.qualifiedName.getName()))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .start();
        switch (sourceClass.instantiationType) {
            case SINGLETON_ENUM:
                // TOIMPROVE générer le code de la factory dans le cas enum avec un nom d'enum dynamique
                methodWriter.newStatement()
                        .start()
                        .append("return ").append(sourceClass.type.simpleName).append(".INSTANCE")
                        .end();
                break;
            case CONSTRUCTOR:
                methodWriter.newStatement()
                        .start()
                        .append("return ").append("new ").append(sourceClass.type.simpleName).appendParamValues(Collections.<DAParameter>emptyList())
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
