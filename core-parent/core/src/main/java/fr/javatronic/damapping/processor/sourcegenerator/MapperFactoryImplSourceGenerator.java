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
package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassMethodWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAStatementWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import static com.google.common.collect.FluentIterable.from;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;

/**
 * MapperFactoryImplSourceGenerator - Générateur du fichier source de la classe MapperFactoryImpl générée dans le cas
 * où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryImplSourceGenerator extends AbstractSourceGenerator {

  @Override
  public void writeFile(@Nonnull BufferedWriter bw, @Nonnull GeneratedFileDescriptor descriptor) throws IOException {
    GeneratedFileDescriptor factoryInterfaceDescriptor = descriptor.getContext().getDescriptor(
        GenerationContext.MAPPER_FACTORY_INTERFACE_KEY
    );
    GeneratedFileDescriptor mapperInterfaceDescriptor = descriptor.getContext().getDescriptor(
        GenerationContext.MAPPER_INTERFACE_KEY
    );
    if (factoryInterfaceDescriptor == null || mapperInterfaceDescriptor == null) {
      return;
    }
    DAType mapperImpl = DATypeFactory.declared(descriptor.getContext().getDescriptor(
        GenerationContext.MAPPER_INTERFACE_KEY
    ).getType().getQualifiedName().getName() + "Impl"
    );

    DASourceClass sourceClass = descriptor.getContext().getSourceClass();
    DAFileWriter fileWriter = new DAFileWriter(bw)
        .appendPackage(sourceClass.getPackageName())
        .appendImports(descriptor.getImports())
        .appendWarningComment();

    DAClassWriter<DAFileWriter> classWriter = fileWriter
        .newClass(descriptor.getType())
        .withImplemented(
            ImmutableList.of(factoryInterfaceDescriptor.getType())
        )
        .withModifiers(ImmutableSet.of(DAModifier.PUBLIC))
        .start();

    appendFactoryMethods(sourceClass, mapperInterfaceDescriptor, mapperImpl, classWriter);

    appendInnerClass(mapperImpl, mapperInterfaceDescriptor, classWriter);

    classWriter.end();

    fileWriter.end();
  }

  private void appendFactoryMethods(DASourceClass sourceClass, GeneratedFileDescriptor mapperInterfaceDescriptor,
                                    DAType mapperImpl,
                                    DAClassWriter<DAFileWriter> classWriter)
      throws IOException {
    for (DAMethod method : Iterables.filter(sourceClass.getMethods(), DAMethodPredicates.isMapperFactoryMethod())) {
      String name = isConstructor().apply(method) ? "instanceByConstructor" : method.getName().getName();
      DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter
          .newMethod(name, mapperInterfaceDescriptor.getType())
          .withAnnotations(ImmutableList.of(DATypeFactory.from(Override.class)))
          .withModifiers(ImmutableSet.of(DAModifier.PUBLIC))
          .withParams(method.getParameters())
          .start();
      DAStatementWriter<?> statementWriter = methodWriter
          .newStatement()
          .start()
          .append("return new ")
          .append(mapperImpl.getSimpleName())
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

  private void appendInnerClass(DAType mapperImpl,
                                GeneratedFileDescriptor mapperInterfaceDescriptor,
                                DAClassWriter<DAFileWriter> factortClassWriter)
      throws IOException {
    DASourceClass sourceClass = mapperInterfaceDescriptor.getContext().getSourceClass();
    DAClassWriter<DAClassWriter<DAFileWriter>> mapperClassWriter = factortClassWriter
        .newClass(mapperImpl)
        .withModifiers(ImmutableSet.of(DAModifier.PRIVATE, DAModifier.STATIC))
        .withImplemented(ImmutableList.of(mapperInterfaceDescriptor.getType()))
        .start();

    // private final [SourceClassType] instance;
    mapperClassWriter.newProperty("instance", sourceClass.getType())
                     .withModifier(ImmutableSet.of(DAModifier.PRIVATE, DAModifier.FINAL))
                     .write();

    // constructor with instance parameter
    DAParameter parameter = DAParameter.builder(DANameFactory.from("instance"),
        sourceClass.getType()
    )
                                       .build();

    mapperClassWriter.newConstructor()
                     .withModifiers(ImmutableSet.of(DAModifier.PUBLIC))
                     .withParams(ImmutableList.of(parameter))
                     .start()
                     .newStatement()
                     .start()
                     .append("this.instance = instance")
                     .end()
                     .end();

    // mapper method(s)
    // implémentation de la méthode de mapping (Function.apply tant qu'on ne supporte pas @MapperMethod)
    DAMethod guavaMethod = from(sourceClass.getMethods()).firstMatch(
        DAMethodPredicates.isGuavaFunction()
    )
        .get();
    DAClassMethodWriter<?> methodWriter = mapperClassWriter
        .newMethod(guavaMethod.getName().getName(), guavaMethod.getReturnType())
        .withAnnotations(ImmutableList.<DAType>of(DATypeFactory.from(Override.class)))
        .withModifiers(ImmutableSet.of(DAModifier.PUBLIC))
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
