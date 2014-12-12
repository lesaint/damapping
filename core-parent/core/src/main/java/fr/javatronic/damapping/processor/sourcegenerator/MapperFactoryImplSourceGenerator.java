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

import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.constants.JavaLangConstants;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.impl.DAParameterImpl;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassMethodWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAStatementWriter;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicate;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isGuavaFunctionApply;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Predicates.or;

/**
 * MapperFactoryImplSourceGenerator - Générateur du fichier source de la classe MapperFactoryImpl générée dans le cas
 * où il existe au moins une méthode annotée avec @MapperFactory dans la class annotée avec @Mapper.
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryImplSourceGenerator extends AbstractSourceGenerator {

  private static final Predicate<DAMethod> IMPLEMENTED_MAPPER_METHOD = or(isGuavaFunctionApply(), isMapperMethod());

  public MapperFactoryImplSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor) {
    super(descriptor, new SourceGeneratorSupport());
  }

  public MapperFactoryImplSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                          @Nonnull SourceGeneratorSupport support) {
    super(descriptor, support);
  }

  @Override
  public void writeFile(@Nonnull BufferedWriter bw) throws IOException {
    GeneratedFileDescriptor factoryInterfaceDescriptor = descriptor.getContext().getDescriptor(
        GenerationContext.MAPPER_FACTORY_INTERFACE_KEY
    );
    GeneratedFileDescriptor mapperInterfaceDescriptor = descriptor.getContext().getDescriptor(
        GenerationContext.MAPPER_INTERFACE_KEY
    );
    if (factoryInterfaceDescriptor == null || mapperInterfaceDescriptor == null) {
      return;
    }
    DAType mapperImpl = DATypeFactory.declared(
        descriptor.getContext()
                  .getDescriptor(GenerationContext.MAPPER_INTERFACE_KEY)
                  .getType()
                  .getQualifiedName().getName() + "Impl"
    );

    DASourceClass sourceClass = descriptor.getContext().getSourceClass();
    DAFileWriter fileWriter = new DAFileWriter(bw);
    if (sourceClass.getPackageName() != null) {
        fileWriter.appendPackage(sourceClass.getPackageName());
    }
    fileWriter.appendImports(descriptor.getImports())
        .appendGeneratedAnnotation(DAMAPPING_ANNOTATION_PROCESSOR_QUALIFIED_NAME);

    DAClassWriter<DAFileWriter> classWriter = fileWriter
        .newClass(descriptor.getType())
        .withImplemented(
            Lists.of(factoryInterfaceDescriptor.getType())
        )
        .withModifiers(DAModifier.PUBLIC)
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
    for (DAMethod method : from(sourceClass.getMethods()).filter(DAMethodPredicates.isMapperFactoryMethod()).toList()) {
      String name = isConstructor().apply(method) ? "instanceByConstructor" : method.getName().getName();
      DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter
          .newMethod(name, mapperInterfaceDescriptor.getType())
          .withAnnotations(Lists.of(JavaLangConstants.OVERRIDE_ANNOTATION))
          .withModifiers(DAModifier.PUBLIC)
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
        .withModifiers(DAModifier.PRIVATE, DAModifier.STATIC)
        .withImplemented(Lists.of(mapperInterfaceDescriptor.getType()))
        .start();

    // private final [SourceClassType] instance;
    mapperClassWriter.newProperty("instance", sourceClass.getType())
                     .withModifiers(DAModifier.PRIVATE, DAModifier.FINAL)
                     .write();

    // constructor with instance parameter
    DAParameter parameter = DAParameterImpl.builder(DANameFactory.from("instance"), sourceClass.getType())
                                           .build();

    mapperClassWriter.newConstructor()
                     .withModifiers(DAModifier.PUBLIC)
                     .withParams(Lists.of(parameter))
                     .start()
                     .newStatement()
                     .start()
                     .append("this.instance = instance")
                     .end()
                     .end();

    // mapper method(s)
    // implémentation de la méthode de mapping (Function.apply tant qu'on ne supporte pas @MapperMethod)
    for (DAMethod mapperMethod : from(sourceClass.getMethods()).filter(IMPLEMENTED_MAPPER_METHOD)) {
      appendMapperMethod(mapperMethod, mapperClassWriter);
    }

    mapperClassWriter.end();
  }

  private void appendMapperMethod(DAMethod mapperMethod, DAClassWriter<DAClassWriter<DAFileWriter>> mapperClassWriter)
      throws IOException {
    DAClassMethodWriter<?> methodWriter = mapperClassWriter
        .newMethod(mapperMethod.getName().getName(), mapperMethod.getReturnType())
        .withAnnotations(support.computeOverrideMethodAnnotations(mapperMethod))
        .withModifiers(DAModifier.PUBLIC)
        .withParams(mapperMethod.getParameters())
        .start();

    // retourne le résultat de la méhode apply de l'instance de la classe @Mapper
    methodWriter.newStatement()
                .start()
                .append("return ")
                .append("instance")
                .append(".")
                .append(mapperMethod.getName())
                .appendParamValues(mapperMethod.getParameters())
                .end();

    methodWriter.end();
  }

}
