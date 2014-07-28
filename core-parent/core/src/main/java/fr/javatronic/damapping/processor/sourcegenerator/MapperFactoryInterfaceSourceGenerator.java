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
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAInterfaceWriter;
import fr.javatronic.damapping.util.Sets;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperFactoryInterfaceSourceGenerator - Générateur du fichier source de l'interface MapperFactory générée dans le cas
 * où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryInterfaceSourceGenerator extends AbstractSourceGenerator {

  public MapperFactoryInterfaceSourceGenerator(GeneratedFileDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public void writeFile(@Nonnull BufferedWriter bw) throws IOException {
    DASourceClass sourceClass = descriptor.getContext().getSourceClass();
    DAFileWriter fileWriter = new DAFileWriter(bw)
        .appendPackage(sourceClass.getPackageName())
        .appendImports(descriptor.getImports())
        .appendWarningComment();

    DAInterfaceWriter<DAFileWriter> interfaceWriter = fileWriter
        .newInterface(descriptor.getType().getSimpleName().getName())
        .withModifiers(Sets.of(DAModifier.PUBLIC))
        .start();

    DAType mapperClass = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "Mapper");
    for (DAMethod method : from(sourceClass.getMethods()).filter(DAMethodPredicates.isMapperFactoryMethod()).toList()) {
      String name = isConstructor().apply(method) ? "instanceByConstructor" : method.getName().getName();
      interfaceWriter.newMethod(name, mapperClass).withParams(method.getParameters()).write();
    }

    interfaceWriter.end();
    fileWriter.end();
  }
}
