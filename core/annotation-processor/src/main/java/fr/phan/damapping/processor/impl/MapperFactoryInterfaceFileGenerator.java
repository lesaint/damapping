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

import java.io.BufferedWriter;
import java.io.IOException;
import javax.lang.model.element.Modifier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
* MapperFactoryInterfaceFileGenerator - Générateur du fichier source de l'interface MapperFactory générée dans le cas
* où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
*
* @author Sébastien Lesaint
*/
class MapperFactoryInterfaceFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperFactoryInterfaceDAType().qualifiedName.getName();
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DASourceClass sourceClass = context.getSourceClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(sourceClass.packageName)
                .appendImports(context.getMapperFactoryInterfaceImports())
                .appendWarningComment();

        DAInterfaceWriter<DAFileWriter> interfaceWriter = fileWriter.newInterface(context.getMapperFactoryInterfaceDAType().simpleName.getName())
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start();

        DAType mapperClass = DATypeFactory.declared(sourceClass.type.qualifiedName + "Mapper");
        for (DAMethod method : Iterables.filter(sourceClass.methods, DAMethodPredicates.isMapperFactoryMethod())) {
            String name = method.isConstructor() ? "instanceByConstructor" : method.name.getName();
            interfaceWriter.newMethod(name, mapperClass)
                    .withParams(method.parameters).write();
        }

        interfaceWriter.end();
        fileWriter.end();
    }
}
