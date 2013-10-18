package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.lang.model.element.Modifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.springframework.stereotype.Component;

import static com.google.common.collect.FluentIterable.from;

/**
* MapperImplFileGenerator -
*
* @author Sébastien Lesaint
*/
class MapperImplFileGenerator extends AbstractFileGenerator {
    private static final List<DAName> SPRING_COMPONENT_IMPORTS = ImmutableList.of(
            DANameFactory.from(Resource.class.getCanonicalName()),
            DANameFactory.from(Component.class.getCanonicalName())
    );

    private static final DAType SPRING_COMPONENT_DATYPE = DATypeFactory.from(Component.class);

    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperClass().type.qualifiedName.getName() + "MapperImpl";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        // générer l'implémentation du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
        //     -> liste des méthodes mapper
        //     -> compute liste des imports à réaliser
        DAMapperClass daMapperClass = context.getMapperClass();

        // package + imports + comment
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(daMapperClass.packageName)
                .appendImports(computeMapperImplImports(context, daMapperClass))
                .appendWarningComment();

        // declaration de la class
        DAClassWriter<DAFileWriter> classWriter = fileWriter.newClass(daMapperClass.type.simpleName + "MapperImpl")
                .withAnnotations(computeAnnotations(daMapperClass))
                .withImplemented(computeImplemented(daMapperClass))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start();

        // instance de la class annotée @Mapper injectée via @Resource le cas échéant
        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            classWriter.newProperty("instance", DATypeFactory.declared(daMapperClass.packageName.getName() + "." + daMapperClass.type.simpleName))
                    .withAnnotations(ImmutableList.of(DATypeFactory.from(Resource.class)))
                    .withModifier(ImmutableSet.of(Modifier.PRIVATE))
                    .write();
        }

        // implémentation de la méthode de mapping (Function.apply tant qu'on ne supporte pas @MapperMethod)
        DAMethod guavaMethod = from(daMapperClass.methods).firstMatch(DAMethodPredicates.isGuavaFunction()).get();
        DAMethodWriter<?> methodWriter = classWriter.newMethod(guavaMethod.name.getName(), guavaMethod.returnType)
                .withAnnotations(ImmutableList.<DAType>of(DATypeFactory.from(Override.class)))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .withParams(guavaMethod.parameters)
                .start();

        // retourne le résultat de la méhode apply de l'instance de la classe @Mapper
        methodWriter.newStatement()
                .start()
                .append("return ")
                .append(computeInstanceObject(daMapperClass))
                .append(".")
                .append(guavaMethod.name)
                .appendParamValues(guavaMethod.parameters)
                .end();

        // clos la méthode
        methodWriter.end();

        // clos la classe
        classWriter.end();

        // clos le fichier
        fileWriter.end();
    }

    private String computeInstanceObject(DAMapperClass daMapperClass) {
        String instance;
        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            instance = "instance";
        }
        else {
            instance = daMapperClass.type.simpleName + "MapperFactory.instance()";
        }
        return instance;
    }

    private List<DAType> computeImplemented(DAMapperClass daMapperClass) {
        DAType mapperInterface = DATypeFactory.declared(daMapperClass.packageName.getName() + "." + daMapperClass.type.simpleName + "Mapper");
        return ImmutableList.of(mapperInterface);
    }

    private ImmutableList<DAType> computeAnnotations(DAMapperClass daMapperClass) {
        return daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT ? ImmutableList.<DAType>of(SPRING_COMPONENT_DATYPE) : null;
    }

    private List<DAName> computeMapperImplImports(FileGeneratorContext context, DAMapperClass daMapperClass) {
        return daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT ? ImmutableList.copyOf(Iterables.concat(context.getMapperImplImports(), SPRING_COMPONENT_IMPORTS)) : context.getMapperImplImports();
    }

}
