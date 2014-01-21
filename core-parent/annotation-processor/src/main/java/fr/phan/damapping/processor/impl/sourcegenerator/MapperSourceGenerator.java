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
package fr.phan.damapping.processor.impl.sourcegenerator;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import fr.phan.damapping.processor.impl.sourcegenerator.writer.DAFileWriter;
import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * MapperSourceGenerator -
 *
 * @author Sébastien Lesaint
 */
public class MapperSourceGenerator extends AbstractSourceGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getSourceClass().getType().getQualifiedName().getName() + "Mapper";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {

        // générer l'interface du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper)
        //     -> visibilite de la classe (protected ou public ?)
        //     -> liste des interfaces implémentées
        //     -> compute liste des imports à réaliser
        DASourceClass sourceClass = context.getSourceClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(sourceClass.getPackageName())
                .appendImports(context.getMapperImports())
                .appendWarningComment();

        fileWriter.newInterface(sourceClass.getType().getSimpleName() + "Mapper")
                .withModifiers(filterModifiers(sourceClass.getModifiers()))
                .withExtended(toDAType(sourceClass.getInterfaces())).start().end();

        bw.flush();
        bw.close();
    }

    private static List<DAType> toDAType(List<DAInterface> interfaces) {
        return FluentIterable.from(interfaces)
                .transform(new Function<DAInterface, DAType>() {
                    @Nullable
                    @Override
                    public DAType apply(@Nullable DAInterface daInterface) {
                        if (daInterface == null) {
                            return null;
                        }
                        return daInterface.getType();
                    }
                }).filter(Predicates.notNull())
                .toList();
    }

    private static Set<Modifier> filterModifiers(Set<Modifier> modifiers) {
        return FluentIterable.from(modifiers)
                .filter(
                        Predicates.not(
                                // an interface can not be final, will not compile
                                Predicates.equalTo(Modifier.FINAL)
                        )
                )
                .toSet();
    }
}