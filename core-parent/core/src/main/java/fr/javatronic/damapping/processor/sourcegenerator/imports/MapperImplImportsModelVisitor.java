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

import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;

import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isDefaultConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isGuavaFunctionApply;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * MapperImplImportsModelVisitor - Visitor building the list of imports for the MapperImpl class.
 *
 * @author Sébastien Lesaint
 */
public class MapperImplImportsModelVisitor extends ImportListBuilder implements DAModelVisitor {

  @Override
  public void visit(DASourceClass daSourceClass) {
    addImports(daSourceClass.getType());
    addImports(from(daSourceClass.getAnnotations()).filter(DAAnnotationPredicates.isScope()));
  }

  @Override
  public void visit(DAInterface daInterface) {
    // interface are declared directly only in Mapper
    // in MapperImpl there is no need to import them again since they are inherited from Mapper
  }

  @Override
  public void visit(DAMethod daMethod) {
    if (isDefaultConstructor().apply(daMethod)) {
      // constructor is not generated in MapperImpl class
      return;
    }
    if (isGuavaFunctionApply().apply(daMethod) || isMapperMethod().apply(daMethod)) {
      // mapper interface does not define any method of it own, only the mapper method
      // or the apply from Guava's Function interface which imports are added via the DAInterface
      addImports(daMethod);
    }
    if (isConstructor().apply(daMethod)) {
      addImports(daMethod);
    }
  }

}
