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
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassMethodWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAConstructorWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAStatementWriter;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicate;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.constants.Jsr330Constants.INJECT_DAANNOTATION;
import static fr.javatronic.damapping.processor.model.constants.Jsr330Constants.INJECT_DANAME;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isGuavaFunctionApply;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Predicates.or;

/**
 * MapperImplSourceGenerator -
 *
 * @author Sébastien Lesaint
 */
public class MapperImplSourceGenerator extends AbstractSourceGenerator {

  private static final String DEDICATED_CLASS_INSTANCE_PROPERTY_NAME = "dedicatedInstance";
  private static final Predicate<DAMethod> IMPLEMENTED_METHOD = or(isGuavaFunctionApply(), isMapperMethod());

  public MapperImplSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                   @Nonnull ProcessorClasspathChecker classpathChecker) {
    this(descriptor, new SourceGeneratorSupport(), classpathChecker);
  }

  public MapperImplSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                   @Nonnull SourceGeneratorSupport support,
                                   @Nonnull ProcessorClasspathChecker classpathChecker) {
    super(descriptor, support, classpathChecker);
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

    switch (sourceClass.getInstantiationType()) {
      case CONSTRUCTOR:
        writeMapperWithConstructor(classWriter, sourceClass);
        break;
      case SINGLETON_ENUM:
        writeEnumMapper(classWriter, sourceClass);
        break;
      case CONSTRUCTOR_FACTORY:
      case STATIC_FACTORY:
      default:
        throw new IllegalArgumentException("Unsupported instantiationType " + sourceClass.getInstantiationType());
    }

    // clos la classe
    classWriter.end();

    // clos le fichier
    fileWriter.end();
  }

  private DAFileWriter packageImportAndComment(BufferedWriter bw, DASourceClass sourceClass) throws IOException {
    DAFileWriter fileWriter = new DAFileWriter(bw);
    if (sourceClass.getPackageName() != null) {
      fileWriter.appendPackage(sourceClass.getPackageName());
    }
    return fileWriter
        .appendImports(computeMapperImplImports(descriptor, sourceClass))
        .appendGeneratedAnnotation(DAMAPPING_ANNOTATION_PROCESSOR_QUALIFIED_NAME);
  }

  private List<DAImport> computeMapperImplImports(GeneratedFileDescriptor descriptor, DASourceClass daSourceClass) {
    Optional<DAMethod> constructor = from(daSourceClass.getAccessibleConstructors()).first();
    if (daSourceClass.getInjectableAnnotation().isPresent()
        && constructor.isPresent() && !constructor.get().getParameters().isEmpty()) {
        return support.appendImports(descriptor.getImports(), INJECT_DANAME);
    }
    return descriptor.getImports();
  }

  private DAClassWriter<DAFileWriter> classDeclaration(DAFileWriter fileWriter, DASourceClass sourceClass)
      throws IOException {
    return fileWriter
        .newClass(descriptor.getType())
        .withImplemented(computeImplemented(sourceClass))
        .withModifiers(DAModifier.PUBLIC)
        .withAnnotations(computeClassAnnotations(sourceClass))
        .start();
  }

  private List<DAAnnotation> computeClassAnnotations(@Nonnull DASourceClass sourceClass) {
    if (sourceClass.getAnnotations().isEmpty()) {
      return Collections.emptyList();
    }

    return from(sourceClass.getAnnotations()).filter(DAAnnotationPredicates.isScope()).toList();
  }

  private List<DAType> computeImplemented(DASourceClass daSourceClass) {
    DAType mapperInterface = DATypeFactory.declared(computeImplementedName(daSourceClass));
    return Collections.singletonList(mapperInterface);
  }

  private String computeImplementedName(DASourceClass daSourceClass) {
    if (daSourceClass.getPackageName() == null) {
      return daSourceClass.getType().getSimpleName() + "Mapper";
    }
    return daSourceClass.getPackageName().getName() + "." + daSourceClass.getType().getSimpleName() + "Mapper";
  }

  private void writeMapperWithConstructor(DAClassWriter<DAFileWriter> classWriter,
                                          DASourceClass sourceClass)
      throws IOException {
    DAMethod dedicatedClassConstructor = findSourceClassConstructor(sourceClass);

    appendDedicatedClassProperty(classWriter, sourceClass, dedicatedClassConstructor);

    appendConstructor(classWriter, sourceClass, dedicatedClassConstructor);

    appendMapperMethod(sourceClass, classWriter);
  }

  /**
   * Adds a property storing an instance of the dedicated class called {@link #DEDICATED_CLASS_INSTANCE_PROPERTY_NAME}.
   * If the dedicated class's constructor has no parameter, the property is initialized when it is declared, otherwise
   * it will be initialized in the MapperImpl's constructor.
   */
  private void appendDedicatedClassProperty(DAClassWriter<DAFileWriter> classWriter,
                                            DASourceClass sourceClass,
                                            DAMethod dedicatedClassConstructor) throws IOException {
    List<DAParameter> constructorParameters = dedicatedClassConstructor.getParameters();
    if (constructorParameters.isEmpty()) {
      classWriter.newInitializedProperty(DEDICATED_CLASS_INSTANCE_PROPERTY_NAME, sourceClass.getType())
                 .withModifiers(DAModifier.PRIVATE, DAModifier.FINAL)
                 .initialize()
                   .append("new ").appendType(sourceClass.getType())
                   .appendParamValues(Collections.<DAParameter>emptyList())
                   .end()
                 .end();
    }
    else {
      classWriter.newProperty(DEDICATED_CLASS_INSTANCE_PROPERTY_NAME, sourceClass.getType())
                 .withModifiers(DAModifier.PRIVATE, DAModifier.FINAL)
                 .write();
    }
  }

  /**
   * Apprends the constructor of the MapperImpl class for a dedicated class of
   * {@link fr.javatronic.damapping.processor.model.InstantiationType#CONSTRUCTOR} which constructor has at least one
   * parameter.
   */
  private void appendConstructor(DAClassWriter<DAFileWriter> classWriter,
                                 DASourceClass sourceClass,
                                 DAMethod dedicatedClassConstructor)
      throws IOException {
    if (dedicatedClassConstructor.getParameters().isEmpty()) {
      return;
    }

    // constructor with the same parameters as the source class constructor
    DAConstructorWriter<?> constructorWriter = classWriter.newConstructor()
                                                          .withAnnotations(computeConstructorAnnotations(sourceClass))
                                                          .withModifiers(DAModifier.PUBLIC)
                                                          .withParams(dedicatedClassConstructor.getParameters())
                                                          .start();

    constructorWriter.newStatement()
                     .start()
                     .append("this.")
                     .append(DEDICATED_CLASS_INSTANCE_PROPERTY_NAME)
                     .append(" = ")
                     .append("new ")
                     .append(sourceClass.getType().getSimpleName())
                     .appendParamValues(dedicatedClassConstructor.getParameters())
                     .end();

    constructorWriter.end();
  }

  private List<DAAnnotation> computeConstructorAnnotations(DASourceClass sourceClass) {
    if (sourceClass.getInjectableAnnotation().isPresent()) {
      return Collections.singletonList(INJECT_DAANNOTATION);
    }
    return null;
  }

  private void writeEnumMapper(DAClassWriter<DAFileWriter> classWriter, DASourceClass sourceClass) throws IOException {
    appendMapperMethod(sourceClass, classWriter);
  }

  private void appendMapperMethod(DASourceClass sourceClass, DAClassWriter<DAFileWriter> classWriter)
      throws IOException {
    for (DAMethod mapperMethod : from(sourceClass.getMethods()).filter(IMPLEMENTED_METHOD)) {
      appendMapperMethod(mapperMethod, sourceClass, classWriter);
    }
  }

  private void appendMapperMethod(DAMethod mapperMethod, DASourceClass sourceClass,
                                  DAClassWriter<DAFileWriter> classWriter) throws IOException {
    // déclaration de la méthode mapper
    DAClassMethodWriter<?> methodWriter = classWriter
        .newMethod(mapperMethod.getName().getName(), mapperMethod.getReturnType())
        .withAnnotations(support.computeOverrideMethodAnnotations(mapperMethod))
        .withModifiers(DAModifier.PUBLIC)
        .withParams(mapperMethod.getParameters())
        .start();

    // retourne le résultat de la méthode apply de l'instance de la classe @Mapper
    DAStatementWriter<?> statementWriter = methodWriter.newStatement().start();
    if (hasReturnType(mapperMethod)) {
      statementWriter.append("return ");
    }
    appendSourceClassReference(statementWriter, sourceClass);
    statementWriter.append(".")
                   .append(mapperMethod.getName())
                   .appendParamValues(mapperMethod.getParameters())
                   .end();

    // clos la méthode
    methodWriter.end();
  }

  private static boolean hasReturnType(DAMethod mapperMethod) {
    return mapperMethod.getReturnType() != null && mapperMethod.getReturnType().getKind() != DATypeKind.VOID;
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
        statementWriter.append("this.").append(DEDICATED_CLASS_INSTANCE_PROPERTY_NAME);
        break;
      default:
        throw new IllegalArgumentException("Unsupported instantiation type " + sourceClass.getInstantiationType());
    }
  }

  @Nonnull
  private DAMethod findSourceClassConstructor(DASourceClass sourceClass) {
    List<DAMethod> constructors = sourceClass.getAccessibleConstructors();
    if (constructors.size() == 0) {
      throw new IllegalStateException("DASourceClass has no constructor at all");
    }
    if (constructors.size() > 1) {
      throw new IllegalArgumentException("DASourceClass has more than one constructor");
    }
    return constructors.iterator().next();
  }
}
