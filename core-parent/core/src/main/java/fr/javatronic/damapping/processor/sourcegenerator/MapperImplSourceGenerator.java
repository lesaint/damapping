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

import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.InstantiationType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassMethodWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAClassWriter;
import fr.javatronic.damapping.processor.sourcegenerator.writer.DAFileWriter;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicates;
import fr.javatronic.damapping.util.Sets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;

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

  private static final DAType SPRING_COMPONENT_DATYPE = DATypeFactory.declared(SPRING_COMPONENT_ANNOTATION_QUALIFIEDNAME);

  public MapperImplSourceGenerator(GeneratedFileDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public void writeFile(@Nonnull BufferedWriter bw) throws IOException {
    GeneratedFileDescriptor factoryClassdescriptor = descriptor.getContext().getDescriptor(GenerationContext.MAPPER_FACTORY_CLASS_KEY);

    // générer l'implémentation du Mapper
    //     -> nom de package
    //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
    //     -> liste des méthodes mapper
    //     -> compute liste des imports à réaliser
    DASourceClass sourceClass = descriptor.getContext().getSourceClass();

    // package + imports + comment
    DAFileWriter fileWriter = new DAFileWriter(bw)
        .appendPackage(sourceClass.getPackageName())
        .appendImports(computeMapperImplImports(descriptor, sourceClass))
        .appendWarningComment();

    // declaration de la class
    DAClassWriter<DAFileWriter> classWriter = fileWriter
        .newClass(descriptor.getType())
        .withAnnotations(computeAnnotations(sourceClass))
        .withImplemented(computeImplemented(sourceClass))
        .withModifiers(Sets.of(DAModifier.PUBLIC))
        .start();

    // instance de la class annotée @Mapper injectée via @Resource le cas échéant
    if (sourceClass.getInstantiationType() == InstantiationType.SPRING_COMPONENT) {
      DAType mapperType = DATypeFactory.declared(
          sourceClass.getPackageName().getName() + "." + sourceClass.getType().getSimpleName()
      );
      classWriter.newProperty("instance", mapperType)
                 .withAnnotations(Lists.of(DATypeFactory.from(Resource.class)))
                 .withModifier(Sets.of(DAModifier.PRIVATE))
                 .write();
    }

    // déclaration de la méthode mapper
    DAMethod mapperMethod = findMapperMethod(sourceClass);
    DAClassMethodWriter<?> methodWriter = classWriter
        .newMethod(mapperMethod.getName().getName(), mapperMethod.getReturnType())
        .withAnnotations(Lists.<DAType>of(DATypeFactory.from(Override.class)))
        .withModifiers(Sets.of(DAModifier.PUBLIC))
        .withParams(mapperMethod.getParameters())
        .start();

    // retourne le résultat de la méthode apply de l'instance de la classe @Mapper
    methodWriter.newStatement()
                .start()
                .append("return ")
                .append(computeInstanceObject(factoryClassdescriptor, sourceClass))
                .append(".")
                .append(mapperMethod.getName())
                .appendParamValues(mapperMethod.getParameters())
                .end();

    // clos la méthode
    methodWriter.end();

    // clos la classe
    classWriter.end();

    // clos le fichier
    fileWriter.end();
  }

  private DAMethod findMapperMethod(DASourceClass sourceClass) {
    return from(sourceClass.getMethods())
        .filter(Predicates.or(DAMethodPredicates.isGuavaFunctionApply(), DAMethodPredicates.isImpliciteMapperMethod()))
        .first()
        .get();
  }

  private String computeInstanceObject(@Nullable GeneratedFileDescriptor factoryClassDescriptor, DASourceClass sourceClass) {
    if (factoryClassDescriptor == null) {
      return "instance";
    }
    return factoryClassDescriptor.getType().getSimpleName() + ".instance()";
  }

  private List<DAType> computeImplemented(DASourceClass daSourceClass) {
    DAType mapperInterface = DATypeFactory.declared(
        daSourceClass.getPackageName().getName() + "." + daSourceClass.getType().getSimpleName() + "Mapper"
    );
    return Lists.of(mapperInterface);
  }

  private List<DAType> computeAnnotations(DASourceClass daSourceClass) {
    return daSourceClass.getInstantiationType() == InstantiationType.SPRING_COMPONENT ? Lists.<DAType>of(
        SPRING_COMPONENT_DATYPE
    ) : null;
  }

  private List<DAName> computeMapperImplImports(GeneratedFileDescriptor descriptor, DASourceClass daSourceClass) {
    if (daSourceClass.getInstantiationType() == InstantiationType.SPRING_COMPONENT) {
      List<DAName> res = Lists.copyOf(descriptor.getImports());
      res.addAll(SPRING_COMPONENT_IMPORTS);
      return res;
    }
    return descriptor.getImports();
  }

}
