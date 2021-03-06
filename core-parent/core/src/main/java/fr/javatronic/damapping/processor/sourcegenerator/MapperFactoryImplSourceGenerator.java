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
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.constants.DAMappingConstants;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.function.DAAnnotationFunctions;
import fr.javatronic.damapping.processor.model.impl.DAParameterImpl;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassMethodWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAConstructorWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAStatementWriter;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.constants.JavaLangConstants.OVERRIDE_ANNOTATION;
import static fr.javatronic.damapping.processor.model.constants.Jsr305Constants.NONNULL_ANNOTATION;
import static fr.javatronic.damapping.processor.model.constants.Jsr305Constants.NONNULL_TYPE;
import static fr.javatronic.damapping.processor.model.constants.Jsr330Constants.INJECT_DAANNOTATION;
import static fr.javatronic.damapping.processor.model.constants.Jsr330Constants.INJECT_DANAME;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.hasMapperDependencyParameters;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isGuavaFunctionApply;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperFactoryMethod;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperMethod;
import static fr.javatronic.damapping.processor.model.predicate.DAParameterPredicates.hasMapperDependencyAnnotation;
import static fr.javatronic.damapping.processor.sourcegenerator.MapperFactoryInterfaceSourceGenerator.MAPPER_FACTORY_CONSTRUCTOR_METHOD_NAME;
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
  private static final Predicate<DAAnnotation> REMOVE_MAPPER_DEPENDENCY_ANNOTATION = Predicates.not(
      Predicates.compose(
          Predicates.equalTo(DAMappingConstants.MAPPER_DEPENDENCY_DATYPE),
          DAAnnotationFunctions.toType()
      )
  );

  public MapperFactoryImplSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                          @Nonnull ProcessorClasspathChecker classpathChecker) {
    super(descriptor, new SourceGeneratorSupport(), classpathChecker);
  }

  public MapperFactoryImplSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                          @Nonnull SourceGeneratorSupport support,
                                          @Nonnull ProcessorClasspathChecker classpathChecker) {
    super(descriptor, support, classpathChecker);
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
    DASourceClass sourceClass = descriptor.getContext().getSourceClass();

    DAType mapperImpl = DATypeFactory.declared(
        descriptor.getContext()
                  .getDescriptor(GenerationContext.MAPPER_INTERFACE_KEY)
                  .getType()
                  .getQualifiedName().getName() + "Impl"
    );
    List<DAParameter> mapperDependencyParameters = computeMapperDependencies(sourceClass);

    DAFileWriter fileWriter = new DAFileWriter(bw);
    if (sourceClass.getPackageName() != null) {
      fileWriter.appendPackage(sourceClass.getPackageName());
    }
    fileWriter.appendImports(computeImports(sourceClass, mapperDependencyParameters, descriptor))
              .appendGeneratedAnnotation(DAMAPPING_ANNOTATION_PROCESSOR_QUALIFIED_NAME);

    DAClassWriter<DAFileWriter> classWriter = fileWriter
        .newClass(descriptor.getType())
        .withImplemented(Lists.of(factoryInterfaceDescriptor.getType()))
        .withModifiers(DAModifier.PUBLIC)
        .start();

    appendProperties(mapperDependencyParameters, classWriter);

    appendConstructor(sourceClass, mapperDependencyParameters, classWriter);

    appendFactoryMethods(sourceClass, mapperInterfaceDescriptor, mapperImpl, classWriter);

    appendInnerClass(mapperImpl, mapperInterfaceDescriptor, classWriter);

    classWriter.end();

    fileWriter.end();
  }

  private Collection<DAImport> computeImports(DASourceClass sourceClass,
                                              List<DAParameter> mapperDependencyParameters,
                                              GeneratedFileDescriptor descriptor) {
    List<DAImport> res = descriptor.getImports();
    if (sourceClass.getInjectableAnnotation().isPresent() && !mapperDependencyParameters.isEmpty()) {
      res = support.appendImports(res, INJECT_DANAME);
    }
    if (classpathChecker.isNonnullPresent()) {
      return support.appendImports(res, NONNULL_TYPE.getQualifiedName());
    }
    return res;
  }

  /**
   * Creates a property for each DAParameter in the list of parameters identified as dependencies of the
   * MapperFactoryImpl class (same name, same type and same annotations except {@code MapperDependency}).
   * <p>
   * Dependencies of the MapperFactory interface are identified by the
   * {@link fr.javatronic.damapping.annotation.MapperDependency} annotation.
   * </p>
   */
  private void appendProperties(List<DAParameter> mapperDependencyParameters, DAClassWriter<DAFileWriter> classWriter)
      throws IOException {
    for (DAParameter parameter : mapperDependencyParameters) {
      classWriter.newProperty(parameter.getName().getName(), parameter.getType())
                 .withModifiers(DAModifier.PRIVATE, DAModifier.FINAL)
                 .withAnnotations(
                     from(parameter.getAnnotations()).filter(REMOVE_MAPPER_DEPENDENCY_ANNOTATION).toList()
                 )
                 .write();
    }

  }

  /**
   * MapperFactoryImpl class does not have a constructor unless there is at least one mapperDependency parameter
   * see {@link fr.javatronic.damapping.annotation.MapperDependency}).
   * <p>
   * In such case, the constructor will have one parameter for each mapper dependency parameter (same name, same type
   * and same annotations except {@code MapperDependency}) and it will initialized the corresponding properties with
   * their values.
   * </p>
   */
  private void appendConstructor(DASourceClass sourceClass,
                                 List<DAParameter> mapperDependencyParameters,
                                 DAClassWriter<DAFileWriter> classWriter) throws IOException {
    if (mapperDependencyParameters.isEmpty()) {
      return;
    }

    DAConstructorWriter<DAClassWriter<DAFileWriter>> constructorWriter = classWriter.newConstructor();
    constructorWriter.withModifiers(DAModifier.PUBLIC)
                     .withParams(
                         from(mapperDependencyParameters)
                             .transform(RemoveMapperDependencyAnnotation.INSTANCE)
                             .toList()
                     )
                     .withAnnotations(computeConstructorAnnotations(sourceClass))
                     .start();

    for (DAParameter mapperDependencyParameter : mapperDependencyParameters) {
      constructorWriter.newStatement()
                       .start()
                       .append("this.")
                       .append(mapperDependencyParameter.getName())
                       .append(" = ")
                       .append(mapperDependencyParameter.getName())
                       .end();
    }

    constructorWriter.end();
  }

  private static List<DAAnnotation> computeConstructorAnnotations(DASourceClass sourceClass) {
    if (sourceClass.getInjectableAnnotation().isPresent()) {
      return Collections.singletonList(INJECT_DAANNOTATION);
    }
    return null;
  }


  @Nonnull
  private static List<DAParameter> computeMapperDependencies(@Nonnull DASourceClass sourceClass) {
    Optional<DAMethod> mapperWithDependencyMethod = from(sourceClass.getMethods())
        .filter(isMapperFactoryMethod())
        .filter(hasMapperDependencyParameters())
        .first();
    if (!mapperWithDependencyMethod.isPresent()) {
      return Collections.emptyList();
    }

    return from(mapperWithDependencyMethod.get().getParameters())
        .filter(hasMapperDependencyAnnotation())
        .toList();
  }

  private static enum RemoveMapperDependencyAnnotation implements Function<DAParameter, DAParameter> {
    INSTANCE;

    @Override
    public DAParameter apply(DAParameter daParameter) {
      List<DAAnnotation> filterAnnotations = removeMapperDependencyAnnotation(daParameter.getAnnotations());
      if (filterAnnotations.size() == daParameter.getAnnotations().size()) {
        return daParameter;
      }

      return DAParameterImpl.builder(daParameter.getName(), daParameter.getType())
                            .withModifiers(daParameter.getModifiers())
                            .withAnnotations(filterAnnotations)
                            .build();
    }

    @Nonnull
    private static List<DAAnnotation> removeMapperDependencyAnnotation(@Nonnull List<DAAnnotation> annotations) {
      if (annotations.isEmpty()) {
        return annotations;
      }

      return from(annotations)
          .filter(
              Predicates.not(
                  Predicates.compose(Predicates.equalTo(DAMappingConstants.MAPPER_DEPENDENCY_DATYPE),
                      DAAnnotationFunctions
                          .toType()
                  )
              )
          )
          .toList();
    }
  }

  private void appendFactoryMethods(DASourceClass sourceClass,
                                    GeneratedFileDescriptor mapperInterfaceDescriptor,
                                    DAType mapperImpl,
                                    DAClassWriter<DAFileWriter> classWriter)
      throws IOException {
    for (DAMethod method : from(sourceClass.getMethods()).filter(isMapperFactoryMethod()).toList()) {
      String name = isConstructor().apply(method) ? MAPPER_FACTORY_CONSTRUCTOR_METHOD_NAME : method.getName().getName();
      DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter
          .newMethod(name, mapperInterfaceDescriptor.getType())
          .withAnnotations(computeFactoryMethodAnnotations())
          .withModifiers(DAModifier.PUBLIC)
          .withParams(removeMapperDependencyParams(method.getParameters()))
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

  private List<DAAnnotation> computeFactoryMethodAnnotations() {
    if (classpathChecker.isNonnullPresent()) {
      return Lists.of(OVERRIDE_ANNOTATION, NONNULL_ANNOTATION);
    }
    return Collections.singletonList(OVERRIDE_ANNOTATION);
  }

  @Nonnull
  private List<DAParameter> removeMapperDependencyParams(@Nonnull List<DAParameter> parameters) {
    return from(parameters)
        .filter(Predicates.not(hasMapperDependencyAnnotation()))
        .toList();
  }

  private void appendInnerClass(DAType mapperImpl,
                                GeneratedFileDescriptor mapperInterfaceDescriptor,
                                DAClassWriter<DAFileWriter> factoryClassWriter)
      throws IOException {
    DASourceClass sourceClass = mapperInterfaceDescriptor.getContext().getSourceClass();
    DAClassWriter<DAClassWriter<DAFileWriter>> mapperClassWriter = factoryClassWriter
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
    DAStatementWriter<?> statementWriter = methodWriter.newStatement().start();
    if (mapperMethod.getReturnType() != null && mapperMethod.getReturnType().getKind() != DATypeKind.VOID) {
      statementWriter.append("return ");
    }
    statementWriter
        .append("instance")
        .append(".")
        .append(mapperMethod.getName())
        .appendParamValues(mapperMethod.getParameters())
        .end();

    methodWriter.end();
  }
}
