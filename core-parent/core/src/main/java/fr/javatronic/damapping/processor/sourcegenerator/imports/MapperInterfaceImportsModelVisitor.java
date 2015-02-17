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
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.predicate.DAInterfacePredicates;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;

/**
 * MapperInterfaceImportsModelVisitor - Visitor building the list of imports for the Mapper interface
 *
 * @author Sébastien Lesaint
 */
public class MapperInterfaceImportsModelVisitor extends ImportListBuilder implements DAModelVisitor {

  @Override
  public void visit(DASourceClass daSourceClass) {
    addImports(daSourceClass.getType());
  }

  @Override
  public void visit(DAInterface daInterface) {
    // the only implemented interface will be Guava's Function interface
    if (!DAInterfacePredicates.isGuavaFunction().apply(daInterface)) {
      return;
    }
    addImports(daInterface.getType());
    for (DAType typeArg : daInterface.getType().getTypeArgs()) {
      addImports(typeArg);
    }
  }

  @Override
  public void visit(DAMethod daMethod) {
    if (daMethod.isMapperMethod()) {
      addImports(daMethod);
    }
    // mapper interface does not define any method of its own if inherits from Guava's Function interface
    // which imports are added via the DAInterface
    // mapper interface only defines implicit mapper method or the one annoted with @MapperMethod
  }

}
