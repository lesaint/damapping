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
package fr.javatronic.damapping.processor.sourcegenerator.imports;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;

import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isApplyWithSingleParam;

/**
 * MapperFactoryImplImportsModelVisitor - Visitor building the list of imports for the MapperFactoryImpl class
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryImplImportsModelVisitor extends ImportListBuilder implements DAModelVisitor {
  @Override
  public void visit(DASourceClass daSourceClass) {
    addImport(daSourceClass.getType().getQualifiedName());
  }

  @Override
  public void visit(DAInterface daInterface) {
    // interfaces are not used in MapperFactory impl
  }

  @Override
  public void visit(DAMethod daMethod) {
    // mapperFactoryMethod are exposed as methods of the MapperFactory
    if (isConstructor().apply(daMethod) || isApplyWithSingleParam().apply(daMethod)) {
      for (DAParameter parameter : daMethod.getParameters()) {
        addImports(parameter.getAnnotations());
        addImports(parameter.getType());
      }
    }

    if (isApplyWithSingleParam().apply(daMethod)) { // remplacer par isMapperMethod
      for (DAParameter parameter : daMethod.getParameters()) {
        addImports(parameter.getType());
      }
      addImport(daMethod.getReturnType());
      for (DAAnnotation daAnnotation : daMethod.getAnnotations()) {
        addImport(daAnnotation.getType());
      }
    }
  }
}
