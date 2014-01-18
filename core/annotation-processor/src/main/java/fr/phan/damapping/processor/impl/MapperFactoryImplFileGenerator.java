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

import fr.phan.damapping.processor.impl.writer.DAClassMethodWriter;
import fr.phan.damapping.processor.impl.writer.DAClassWriter;
import fr.phan.damapping.processor.impl.writer.DAFileWriter;
import fr.phan.damapping.processor.impl.writer.DAStatementWriter;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.factory.DATypeFactory;
import fr.phan.damapping.processor.model.predicate.DAMethodPredicates;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.lang.model.element.Modifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import static com.google.common.collect.FluentIterable.from;
import static fr.phan.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;

/**
 * MapperFactoryImplFileGenerator - Générateur du fichier source de la classe MapperFactoryImpl générée dans le cas
 * où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
 *
 * @author Sébastien Lesaint
 */
class MapperFactoryImplFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperFactoryImplDAType().getQualifiedName().getName();
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DASourceClass sourceClass = context.getSourceClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(sourceClass.getPackageName())
                .appendImports(context.getMapperFactoryImplImports())
                .appendWarningComment();

        DAClassWriter<DAFileWriter> classWriter = fileWriter.newClass(context.getMapperFactoryImplDAType())
                .withImplemented(ImmutableList.of(context.getMapperFactoryInterfaceDAType()))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start();

        appendFactoryMethods(context, classWriter);

        appendInnerClass(context, classWriter);

        classWriter.end();

        fileWriter.end();
    }

    private void appendFactoryMethods(FileGeneratorContext context, DAClassWriter<DAFileWriter> classWriter) throws IOException {
        DASourceClass sourceClass = context.getSourceClass();
        for (DAMethod method : Iterables.filter(sourceClass.getMethods(), DAMethodPredicates.isMapperFactoryMethod())) {
            String name = isConstructor().apply(method) ? "instanceByConstructor" : method.getName().getName();
            DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter
                    .newMethod(name, context.getMapperDAType())
                    .withAnnotations(ImmutableList.of(DATypeFactory.from(Override.class)))
                    .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                    .withParams(method.getParameters())
                    .start();
            DAStatementWriter<?> statementWriter = methodWriter.newStatement()
                    .start()
                    .append("return new ")
                    .append(context.getMapperImplDAType().getSimpleName())
                    .append("(");
            if (isConstructor().apply(method)) {
                statementWriter.append("new ").append(sourceClass.getType().getSimpleName())
                        .appendParamValues(method.getParameters());
            }
            else {
                statementWriter.append(sourceClass.getType().getSimpleName())
                        .append(".")
                        .append(method.getName()).appendParamValues(method.getParameters());
            }
            statementWriter
                    .append(")")
                    .end();

            methodWriter.end();
        }
    }

    private void appendInnerClass(FileGeneratorContext context, DAClassWriter<DAFileWriter> factortClassWriter) throws IOException {
        DAClassWriter<DAClassWriter<DAFileWriter>> mapperClassWriter = factortClassWriter
                .newClass(context.getMapperImplDAType())
                .withModifiers(ImmutableSet.of(Modifier.PRIVATE, Modifier.STATIC))
                .withImplemented(ImmutableList.of(context.getMapperDAType()))
                .start();

        // private final [SourceClassType] instance;
        mapperClassWriter.newProperty("instance", context.getSourceClass().getType())
                .withModifier(ImmutableSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .write();

        // constructor with instance parameter
        DAParameter parameter = DAParameter.builder(DANameFactory.from("instance"), context.getSourceClass().getType()).build();

        mapperClassWriter.newConstructor()
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .withParams(ImmutableList.of(parameter))
                .start()
                    .newStatement()
                    .start()
                    .append("this.instance = instance")
                    .end()
                .end();

        // mapper method(s)
        // implémentation de la méthode de mapping (Function.apply tant qu'on ne supporte pas @MapperMethod)
        DAMethod guavaMethod = from(context.getSourceClass().getMethods()).firstMatch(DAMethodPredicates.isGuavaFunction()).get();
        DAClassMethodWriter<?> methodWriter = mapperClassWriter.newMethod(guavaMethod.getName().getName(), guavaMethod.getReturnType())
                .withAnnotations(ImmutableList.<DAType>of(DATypeFactory.from(Override.class)))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .withParams(guavaMethod.getParameters())
                .start();

        // retourne le résultat de la méhode apply de l'instance de la classe @Mapper
        methodWriter.newStatement()
                .start()
                .append("return ")
                .append("instance")
                .append(".")
                .append(guavaMethod.getName())
                .appendParamValues(guavaMethod.getParameters())
                .end();

        methodWriter.end();

        mapperClassWriter.end();
    }

}
