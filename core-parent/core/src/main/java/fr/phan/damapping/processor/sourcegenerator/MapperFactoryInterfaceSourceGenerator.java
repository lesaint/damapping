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
package fr.phan.damapping.processor.sourcegenerator;

import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.factory.DATypeFactory;
import fr.phan.damapping.processor.model.predicate.DAMethodPredicates;
import fr.phan.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.phan.damapping.processor.sourcegenerator.writer.DAInterfaceWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import static fr.phan.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;

/**
 * MapperFactoryInterfaceSourceGenerator - Générateur du fichier source de l'interface MapperFactory générée dans le cas
 * où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryInterfaceSourceGenerator extends AbstractSourceGenerator {
  @Override
  public String fileName(FileGeneratorContext context) {
    return context.getMapperFactoryInterfaceDAType().getQualifiedName().getName();
  }

  @Override
  public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
    DASourceClass sourceClass = context.getSourceClass();
    DAFileWriter fileWriter = new DAFileWriter(bw)
        .appendPackage(sourceClass.getPackageName())
        .appendImports(context.getMapperFactoryInterfaceImports())
        .appendWarningComment();

    DAInterfaceWriter<DAFileWriter> interfaceWriter = fileWriter
        .newInterface(context.getMapperFactoryInterfaceDAType().getSimpleName().getName())
        .withModifiers(ImmutableSet.of(DAModifier.PUBLIC))
        .start();

    DAType mapperClass = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "Mapper");
    for (DAMethod method : Iterables.filter(sourceClass.getMethods(), DAMethodPredicates.isMapperFactoryMethod())) {
      String name = isConstructor().apply(method) ? "instanceByConstructor" : method.getName().getName();
      interfaceWriter.newMethod(name, mapperClass).withParams(method.getParameters()).write();
    }

    interfaceWriter.end();
    fileWriter.end();
  }
}
