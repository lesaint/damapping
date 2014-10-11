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

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.InstantiationType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassMethodWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAConstructorWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAStatementWriter;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicates;
import fr.javatronic.damapping.util.Sets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Resource;

import static fr.javatronic.damapping.processor.model.InstantiationType.CONSTRUCTOR;
import static fr.javatronic.damapping.processor.model.InstantiationType.SPRING_COMPONENT;
import static fr.javatronic.damapping.processor.model.constants.JavaxConstants.RESOURCE_ANNOTATION;
import static fr.javatronic.damapping.processor.model.function.DAParameterFunctions.toNamePrefixedWithThis;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperImplSourceGenerator -
 *
 * @author Sébastien Lesaint
 */
public class MapperImplSourceGenerator extends AbstractSourceGenerator {
  private static final String SPRING_COMPONENT_ANNOTATION_QUALIFIEDNAME = "org.springframework.stereotype.Component";

  private static final List<DAName> SPRING_COMPONENT_IMPORTS = Lists.of(
      DANameFactory.from(Resource.class.getCanonicalName()),
      DANameFactory.from(SPRING_COMPONENT_ANNOTATION_QUALIFIEDNAME)
  );

  private static final DAAnnotation SPRING_COMPONENT_DATYPE = new DAAnnotation(
      DATypeFactory.declared(SPRING_COMPONENT_ANNOTATION_QUALIFIEDNAME)
  );

  public MapperImplSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor) {
    this(descriptor, new SourceGeneratorSupport());
  }

  public MapperImplSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                   @Nonnull SourceGeneratorSupport support) {
    super(descriptor, support);
  }

  @Override
  public void writeFile(@Nonnull BufferedWriter bw) throws IOException {
    // générer l'implémentation du Mapper
    //     -> nom de package
    //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
    //     -> liste des méthodes mapper
    //     -> compute liste des imports à réaliser
    DASourceClass sourceClass = descriptor.getContext().getSourceClass();

    // package + imports + comment
    DAFileWriter fileWriter = packageImportAndComment(bw, sourceClass);

    // declaration de la class
    DAClassWriter<DAFileWriter> classWriter = classDeclaration(fileWriter, sourceClass);

    Optional<DAMethod> sourceClassConstructor = findSourceClassConstructor(sourceClass);
    if (sourceClass.getInstantiationType() == CONSTRUCTOR
        && sourceClassConstructor.isPresent() && !sourceClassConstructor.get().getParameters().isEmpty()) {
       writeMapperWithConstructor(classWriter, sourceClass, sourceClassConstructor.get());
    }
    else if (sourceClass.getInstantiationType() == SPRING_COMPONENT) {
      writeSpringComponentMapper(classWriter, sourceClass);
    }
    else {
      writeSimpleMapper(classWriter, sourceClass);
    }

    // clos la classe
    classWriter.end();

    // clos le fichier
    fileWriter.end();
  }

  private DAFileWriter packageImportAndComment(BufferedWriter bw, DASourceClass sourceClass) throws IOException {
    return new DAFileWriter(bw)
        .appendPackage(sourceClass.getPackageName())
        .appendImports(computeMapperImplImports(descriptor, sourceClass))
        .appendGeneratedAnnotation(DAMAPPING_ANNOTATION_PROCESSOR_QUALIFIED_NAME);
  }

  private DAClassWriter<DAFileWriter> classDeclaration(DAFileWriter fileWriter, DASourceClass sourceClass)
      throws IOException {
    return fileWriter
        .newClass(descriptor.getType())
        .withAnnotations(computeAnnotations(sourceClass))
        .withImplemented(computeImplemented(sourceClass))
        .withModifiers(Sets.of(DAModifier.PUBLIC))
        .start();
  }

  private void writeMapperWithConstructor(DAClassWriter<DAFileWriter> classWriter, DASourceClass sourceClass,
                                          DAMethod sourceClassConstructor) throws IOException {
    List<DAParameter> parameters = sourceClassConstructor.getParameters();

    // create a final property for each parameter of the source class constructor
    for (DAParameter daParameter : parameters) {
      classWriter.newProperty(daParameter.getName().getName(), daParameter.getType())
                 .withModifiers(Sets.of(DAModifier.PRIVATE, DAModifier.FINAL))
                 .write();
    }

    // constructor with the same parameters as the source class constructor
    DAConstructorWriter<?> constructorWriter = classWriter.newConstructor()
                                                          .withModifiers(Sets.of(DAModifier.PUBLIC))
                                                          .withParams(parameters)
                                                          .start();
    for (DAParameter daParameter : parameters) {
      constructorWriter.newStatement().start()
                       .append("this.").append(daParameter.getName()).append(" = ").append(daParameter.getName()).end();
    }
    constructorWriter.end();


    // mapper method
    appendMapperMethod(sourceClass, classWriter);
  }

  private void writeSpringComponentMapper(DAClassWriter<DAFileWriter> classWriter, DASourceClass sourceClass)
      throws IOException {
    // instance de la class annotée @Mapper injectée via @Resource le cas échéant
    DAType mapperType = DATypeFactory.declared(
        sourceClass.getPackageName().getName() + "." + sourceClass.getType().getSimpleName()
    );
    classWriter.newProperty("instance", mapperType)
               .withAnnotations(Lists.of(RESOURCE_ANNOTATION))
               .withModifiers(Sets.of(DAModifier.PRIVATE))
               .write();

    // mapper method
    appendMapperMethod(sourceClass, classWriter);
  }

  private void writeSimpleMapper(DAClassWriter<DAFileWriter> classWriter, DASourceClass sourceClass)
      throws IOException {
    // class just implements the mapper method
    appendMapperMethod(sourceClass, classWriter);
  }

  private void appendMapperMethod(DASourceClass sourceClass, DAClassWriter<DAFileWriter> classWriter)
      throws IOException {
    // déclaration de la méthode mapper
    DAMethod mapperMethod = findMapperMethod(sourceClass);
    DAClassMethodWriter<?> methodWriter = classWriter
        .newMethod(mapperMethod.getName().getName(), mapperMethod.getReturnType())
        .withAnnotations(support.computeOverrideMethodAnnotations(mapperMethod))
        .withModifiers(Sets.of(DAModifier.PUBLIC))
        .withParams(mapperMethod.getParameters())
        .start();

    // retourne le résultat de la méthode apply de l'instance de la classe @Mapper
    DAStatementWriter<?> statementWriter = methodWriter.newStatement().start().append("return ");
    appendSourceClassReference(statementWriter, sourceClass);
    statementWriter.append(".")
                   .append(mapperMethod.getName())
                   .appendParamValues(mapperMethod.getParameters())
                   .end();

    // clos la méthode
    methodWriter.end();
  }

  private DAMethod findMapperMethod(DASourceClass sourceClass) {
    return from(sourceClass.getMethods())
        .filter(Predicates.or(DAMethodPredicates.isGuavaFunctionApply(), DAMethodPredicates.isImpliciteMapperMethod()))
        .first()
        .get();
  }

  private void appendSourceClassReference(DAStatementWriter<?> statementWriter, DASourceClass sourceClass)
      throws IOException {
    switch (sourceClass.getInstantiationType()) {
      case SINGLETON_ENUM:
        statementWriter.append(sourceClass.getType().getSimpleName())
                       .append(".")
                       .append(sourceClass.getEnumValues().iterator().next().getName());
        break;
      case CONSTRUCTOR:
        appendConstructorCall(statementWriter, sourceClass);
        break;
      case SPRING_COMPONENT:
        statementWriter.append("instance");
        break;
      default:
        throw new IllegalArgumentException("Unsupported instantiation type " + sourceClass.getInstantiationType());
    }
  }

  private void appendConstructorCall(DAStatementWriter<?> statementWriter, DASourceClass sourceClass)
      throws IOException {
    statementWriter.append("new ");
    statementWriter.append(sourceClass.getType().getSimpleName());
    statementWriter.appendParamValues(
        findSourceClassConstructor(sourceClass).get().getParameters(),
        toNamePrefixedWithThis()
    );
  }

  private Optional<DAMethod> findSourceClassConstructor(DASourceClass sourceClass) {
    if (sourceClass.getInstantiationType() == InstantiationType.SINGLETON_ENUM) {
      return Optional.absent();
    }
    List<DAMethod> constructors = from(sourceClass.getMethods()).filter(DAMethodPredicates.isConstructor()).toList();
    if (constructors.size() == 0) {
      throw new IllegalStateException("DASourceClass has no constructor at all");
    }
    if (constructors.size() > 1) {
      throw new IllegalArgumentException("DASourceClass has more than one constructor");
    }
    return Optional.of(constructors.iterator().next());
  }

  private List<DAType> computeImplemented(DASourceClass daSourceClass) {
    DAType mapperInterface = DATypeFactory.declared(
        daSourceClass.getPackageName().getName() + "." + daSourceClass.getType().getSimpleName() + "Mapper"
    );
    return Lists.of(mapperInterface);
  }

  private List<DAAnnotation> computeAnnotations(DASourceClass daSourceClass) {
    if (daSourceClass.getInstantiationType() == SPRING_COMPONENT) {
      return Lists.of(SPRING_COMPONENT_DATYPE);
    }
    return null;
  }

  private List<DAName> computeMapperImplImports(GeneratedFileDescriptor descriptor, DASourceClass daSourceClass) {
    if (daSourceClass.getInstantiationType() == SPRING_COMPONENT) {
      List<DAName> res = Lists.copyOf(descriptor.getImports());
      res.addAll(SPRING_COMPONENT_IMPORTS);
      return res;
    }
    return descriptor.getImports();
  }
}
