/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.ProcessorClasspathChecker;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAInterfaceWriter;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperSourceGenerator -
 *
 * @author Sébastien Lesaint
 */
public class MapperSourceGenerator extends AbstractSourceGenerator {

  private static final Predicate<DAModifier> NOT_FINAL = Predicates.not(Predicates.equalTo(DAModifier.FINAL));

  public MapperSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                               @Nonnull ProcessorClasspathChecker classpathChecker) {
    super(descriptor, new SourceGeneratorSupport(), classpathChecker);
  }

  public MapperSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                               @Nonnull SourceGeneratorSupport support,
                               @Nonnull ProcessorClasspathChecker classpathChecker) {
    super(descriptor, support, classpathChecker);
  }

  @Override
  public void writeFile(@Nonnull BufferedWriter bw) throws IOException {
    // générer l'interface du Mapper
    //     -> nom de package
    //     -> nom de la classe (infère nom du Mapper)
    //     -> visibilite de la classe (protected ou public ?)
    //     -> liste des interfaces implémentées
    //     -> compute liste des imports à réaliser
    DASourceClass sourceClass = descriptor.getContext().getSourceClass();
    DAFileWriter fileWriter = new DAFileWriter(bw);
    if (sourceClass.getPackageName() != null) {
        fileWriter.appendPackage(sourceClass.getPackageName());
    }
    fileWriter.appendImports(descriptor.getImports())
        .appendGeneratedAnnotation(DAMAPPING_ANNOTATION_PROCESSOR_QUALIFIED_NAME);

    DAInterfaceWriter<DAFileWriter> interfaceWriter = fileWriter
        .newInterface(descriptor.getType().getSimpleName().getName())
        .withModifiers(filterModifiers(sourceClass.getModifiers()))
        .withExtended(toDAType(sourceClass.getInterfaces())).start();

    // declare mapper methods
    for (DAMethod mapperMethod : findMapperMethod(sourceClass)) {
      interfaceWriter.newMethod(mapperMethod.getName().getName(), mapperMethod.getReturnType())
          .withAnnotations(computeMapperMethodAnnotations(mapperMethod))
          .withParams(mapperMethod.getParameters())
          .write();
    }

    interfaceWriter.end();

    bw.flush();
    bw.close();
  }

  private List<DAAnnotation> computeMapperMethodAnnotations(DAMethod mapperMethod) {
    if (mapperMethod.isGuavaFunctionApplyMethod()) {
      return support.computeOverrideMethodAnnotations(mapperMethod);
    }
    return mapperMethod.getAnnotations();
  }

  private Iterable<DAMethod> findMapperMethod(DASourceClass sourceClass) {
    return from(sourceClass.getMethods()).filter(DAMethodPredicates.isMapperMethod());
  }

  private static List<DAType> toDAType(List<DAInterface> interfaces) {
    return
        from(interfaces)
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
            .toList();
  }

  private static Set<DAModifier> filterModifiers(Set<DAModifier> modifiers) {
    // an interface can not be final, will not compile
    return from(modifiers).filter(NOT_FINAL).toSet();
  }
}
