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

import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.InstantiationType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassMethodWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Sets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Resource;

/**
 * MapperFactoryClassSourceGenerator -
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryClassSourceGenerator extends AbstractSourceGenerator {

  public MapperFactoryClassSourceGenerator(GeneratedFileDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public void writeFile(@Nonnull BufferedWriter bw) throws IOException {
    DASourceClass sourceClass = descriptor.getContext().getSourceClass();

    DAFileWriter fileWriter = new DAFileWriter(bw)
        .appendPackage(sourceClass.getPackageName())
        .appendImports(descriptor.getImports())
        .appendWarningComment();

    // générer la factory
    //     -> nom du package
    //     -> nom de la classe (infère nom de la factory et nom du Mapper)
    //     -> type d'instantiation (si enum, le nom de la valeur d'enum à utiliser)
    DAClassWriter<DAFileWriter> classWriter = fileWriter.newClass(descriptor.getType())
                                                        .start();
    if (sourceClass.getInstantiationType() == InstantiationType.SPRING_COMPONENT) {
      classWriter.newProperty("instance", DATypeFactory.declared(sourceClass.getType().getQualifiedName().getName()))
                 .withModifier(Sets.of(DAModifier.PRIVATE))
                 .withAnnotations(Lists.of(DATypeFactory.from(Resource.class)))
                 .write();
    }

    DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter
        .newMethod("instance",
            DATypeFactory.declared(sourceClass.getType().getQualifiedName().getName())
        )
        .withModifiers(
            Sets.of(DAModifier.PUBLIC,
                DAModifier.STATIC
            )
        )
        .start();
    switch (sourceClass.getInstantiationType()) {
      case SINGLETON_ENUM:
        // TOIMPROVE générer le code de la factory dans le cas enum avec un nom d'enum dynamique
        methodWriter.newStatement()
                    .start()
                    .append("return ").append(sourceClass.getType().getSimpleName()).append(".INSTANCE")
                    .end();
        break;
      case CONSTRUCTOR:
        methodWriter.newStatement()
                    .start()
                    .append("return ")
                    .append("new ")
                    .append(sourceClass.getType().getSimpleName())
                    .appendParamValues(Collections.<DAParameter>emptyList())
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
