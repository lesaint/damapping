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
import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAParameterPredicates;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAInterfaceWriter;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.constants.Jsr305Constants.NONNULL_ANNOTATION;
import static fr.javatronic.damapping.processor.model.constants.Jsr305Constants.NONNULL_TYPE;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperFactoryInterfaceSourceGenerator - Générateur du fichier source de l'interface MapperFactory générée dans le cas
 * où il existe au moins une méthode annotée avec @MapperFactory dans la class annotée avec @Mapper.
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryInterfaceSourceGenerator extends AbstractSourceGenerator {

  protected static final String MAPPER_FACTORY_CONSTRUCTOR_METHOD_NAME = "get";
  private static final Predicate<DAParameter> FILTER_OUT_PARAMETER_WITH_MAPPER_DEPENDENCY_ANNOTATION = Predicates.not(
      DAParameterPredicates.hasMapperDependencyAnnotation()
  );

  public MapperFactoryInterfaceSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                               @Nonnull ProcessorClasspathChecker classpathChecker) {
    super(descriptor, new SourceGeneratorSupport(), classpathChecker);
  }

  public MapperFactoryInterfaceSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                               @Nonnull SourceGeneratorSupport support,
                                               @Nonnull ProcessorClasspathChecker classpathChecker) {
    super(descriptor, support, classpathChecker);
  }

  @Override
  public void writeFile(@Nonnull BufferedWriter bw) throws IOException {
    DASourceClass sourceClass = descriptor.getContext().getSourceClass();
    DAFileWriter fileWriter = new DAFileWriter(bw);
    if (sourceClass.getPackageName() != null) {
      fileWriter.appendPackage(sourceClass.getPackageName());
    }
    fileWriter.appendImports(computeImports(descriptor))
              .appendGeneratedAnnotation(DAMAPPING_ANNOTATION_PROCESSOR_QUALIFIED_NAME);

    DAInterfaceWriter<DAFileWriter> interfaceWriter = fileWriter
        .newInterface(descriptor.getType().getSimpleName().getName())
        .withModifiers(DAModifier.PUBLIC)
        .start();

    DAType mapperClass = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "Mapper");
    for (DAMethod method : from(sourceClass.getMethods()).filter(DAMethodPredicates.isMapperFactoryMethod())) {
      String name = isConstructor().apply(method) ? MAPPER_FACTORY_CONSTRUCTOR_METHOD_NAME : method.getName().getName();
      interfaceWriter.newMethod(name, mapperClass)
                     .withAnnotations(computeMethodAnnotations())
                     .withParams(
                        from(method.getParameters())
                             .filter(FILTER_OUT_PARAMETER_WITH_MAPPER_DEPENDENCY_ANNOTATION)
                             .toList()
                     )
                     .write();
    }

    interfaceWriter.end();
    fileWriter.end();
  }

  private Collection<DAImport> computeImports(GeneratedFileDescriptor descriptor) {
    if (classpathChecker.isNonnullPresent()) {
      return support.appendImports(descriptor.getImports(), NONNULL_TYPE.getQualifiedName());
    }
    return descriptor.getImports();
  }

  private List<DAAnnotation> computeMethodAnnotations() {
    if (classpathChecker.isNonnullPresent()) {
      return Collections.singletonList(NONNULL_ANNOTATION);
    }
    return Collections.emptyList();
  }

}
