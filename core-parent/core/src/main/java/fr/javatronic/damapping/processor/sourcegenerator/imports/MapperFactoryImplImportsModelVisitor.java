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
package fr.javatronic.damapping.processor.sourcegenerator.imports;

import fr.javatronic.damapping.annotation.MapperFactory;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;
import fr.javatronic.damapping.util.Predicate;

import javax.annotation.Nullable;

import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isGuavaFunctionApply;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperMethod;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperFactoryMethod;

/**
 * MapperFactoryImplImportsModelVisitor - Visitor building the list of imports for the MapperFactoryImpl class
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryImplImportsModelVisitor extends ImportListBuilder implements DAModelVisitor {
  @Override
  public void visit(DASourceClass daSourceClass) {
    addImports(daSourceClass.getType());
  }

  @Override
  public void visit(DAInterface daInterface) {
    // interfaces are not used in MapperFactory impl
  }

  @Override
  public void visit(DAMethod daMethod) {
    if (isMapperFactoryMethod().apply(daMethod)) {
      addImports(daMethod, DAMaethodImportFilters.from(new Predicate<DAAnnotation>() {
        @Override
        public boolean apply(@Nullable DAAnnotation daAnnotation) {
          if (daAnnotation == null) {
            return false;
          }
          return !MapperFactory.class.getName().equals(daAnnotation.getType().getQualifiedName().getName());
        }
      }));
    }
    if (isGuavaFunctionApply().apply(daMethod) || isMapperMethod().apply(daMethod)) {
      addImports(daMethod);
    }
//    // mapperFactoryMethod are exposed as methods of the MapperFactory
//    if (isConstructor().apply(daMethod) || isApplyWithSingleParam().apply(daMethod)) {
//      for (DAParameter parameter : daMethod.getParameters()) {
//        addImports(parameter.getAnnotations());
//        addImports(parameter.getType());
//      }
//    }
//
//    if (isApplyWithSingleParam().apply(daMethod)) { // remplacer par isMapperMethod
//      for (DAParameter parameter : daMethod.getParameters()) {
//        addImports(parameter.getType());
//      }
//      addImport(daMethod.getReturnType());
//      for (DAAnnotation daAnnotation : daMethod.getAnnotations()) {
//        addImport(daAnnotation.getType());
//      }
//    }
  }
}
