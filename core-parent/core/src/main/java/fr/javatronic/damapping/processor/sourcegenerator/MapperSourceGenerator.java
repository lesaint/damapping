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

import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import static fr.javatronic.damapping.processor.util.FluentIterableProxy.toList;
import static fr.javatronic.damapping.processor.util.FluentIterableProxy.toSet;

/**
 * MapperSourceGenerator -
 *
 * @author Sébastien Lesaint
 */
public class MapperSourceGenerator extends AbstractSourceGenerator {

  @Override
  public void writeFile(@Nonnull BufferedWriter bw, @Nonnull GeneratedFileDescriptor descriptor) throws IOException {
    // générer l'interface du Mapper
    //     -> nom de package
    //     -> nom de la classe (infère nom du Mapper)
    //     -> visibilite de la classe (protected ou public ?)
    //     -> liste des interfaces implémentées
    //     -> compute liste des imports à réaliser
    DASourceClass sourceClass = descriptor.getContext().getSourceClass();
    DAFileWriter fileWriter = new DAFileWriter(bw)
        .appendPackage(sourceClass.getPackageName())
        .appendImports(descriptor.getImports())
        .appendWarningComment();

    fileWriter.newInterface(descriptor.getType().getSimpleName().getName())
              .withModifiers(filterModifiers(sourceClass.getModifiers()))
              .withExtended(toDAType(sourceClass.getInterfaces())).start().end();

    bw.flush();
    bw.close();
  }

  private static List<DAType> toDAType(List<DAInterface> interfaces) {
    return toList(
        FluentIterable.from(interfaces)
                      .transform(new Function<DAInterface, DAType>() {
                        @Nullable
                        @Override
                        public DAType apply(@Nullable DAInterface daInterface) {
                          if (daInterface == null) {
                            return null;
                          }
                          return daInterface.getType();
                        }
                      }
                      ).filter(Predicates.notNull())
    );
  }

  private static Set<DAModifier> filterModifiers(Set<DAModifier> modifiers) {
    return toSet(
        FluentIterable.from(modifiers)
                      .filter(
                          Predicates.not(
                              // an interface can not be final, will not compile
                              Predicates.equalTo(DAModifier.FINAL)
                          )
                      )
    );
  }
}
