/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.factory.DATypeFactory;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.InstantiationType;

import java.io.BufferedWriter;
import java.io.IOException;
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
        return context.getSourceClass().getType().getQualifiedName().getName() + "MapperImpl";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        // générer l'implémentation du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
        //     -> liste des méthodes mapper
        //     -> compute liste des imports à réaliser
        DASourceClass sourceClass = context.getSourceClass();

        // package + imports + comment
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(sourceClass.getPackageName())
                .appendImports(computeMapperImplImports(context, sourceClass))
                .appendWarningComment();

        // declaration de la class
        DAClassWriter<DAFileWriter> classWriter = fileWriter.newClass(context.getMapperImplDAType())
                .withAnnotations(computeAnnotations(sourceClass))
                .withImplemented(computeImplemented(sourceClass))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start();

        // instance de la class annotée @Mapper injectée via @Resource le cas échéant
        if (sourceClass.getInstantiationType() == InstantiationType.SPRING_COMPONENT) {
            classWriter.newProperty("instance", DATypeFactory.declared(sourceClass.getPackageName().getName() + "." + sourceClass.getType().getSimpleName()))
                    .withAnnotations(ImmutableList.of(DATypeFactory.from(Resource.class)))
                    .withModifier(ImmutableSet.of(Modifier.PRIVATE))
                    .write();
        }

        // implémentation de la méthode de mapping (Function.apply tant qu'on ne supporte pas @MapperMethod)
        DAMethod guavaMethod = from(sourceClass.getMethods()).firstMatch(DAMethodPredicates.isGuavaFunction()).get();
        DAClassMethodWriter<?> methodWriter = classWriter.newMethod(guavaMethod.getName().getName(), guavaMethod.getReturnType())
                .withAnnotations(ImmutableList.<DAType>of(DATypeFactory.from(Override.class)))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .withParams(guavaMethod.getParameters())
                .start();

        // retourne le résultat de la méhode apply de l'instance de la classe @Mapper
        methodWriter.newStatement()
                .start()
                .append("return ")
                .append(computeInstanceObject(context))
                .append(".")
                .append(guavaMethod.getName())
                .appendParamValues(guavaMethod.getParameters())
                .end();

        // clos la méthode
        methodWriter.end();

        // clos la classe
        classWriter.end();

        // clos le fichier
        fileWriter.end();
    }

    private String computeInstanceObject(FileGeneratorContext context) {
        String instance;
        if (context.getSourceClass().getInstantiationType() == InstantiationType.SPRING_COMPONENT) {
            instance = "instance";
        }
        else {
            instance = context.getMapperFactoryClassDAType().getSimpleName() + ".instance()";
        }
        return instance;
    }

    private List<DAType> computeImplemented(DASourceClass daSourceClass) {
        DAType mapperInterface = DATypeFactory.declared(daSourceClass.getPackageName().getName() + "." + daSourceClass.getType().getSimpleName() + "Mapper");
        return ImmutableList.of(mapperInterface);
    }

    private ImmutableList<DAType> computeAnnotations(DASourceClass daSourceClass) {
        return daSourceClass.getInstantiationType() == InstantiationType.SPRING_COMPONENT ? ImmutableList.<DAType>of(SPRING_COMPONENT_DATYPE) : null;
    }

    private List<DAName> computeMapperImplImports(FileGeneratorContext context, DASourceClass daSourceClass) {
        return daSourceClass.getInstantiationType() == InstantiationType.SPRING_COMPONENT ? ImmutableList.copyOf(Iterables.concat(context.getMapperImplImports(), SPRING_COMPONENT_IMPORTS)) : context.getMapperImplImports();
    }

}
