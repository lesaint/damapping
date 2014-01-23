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
package fr.phan.damapping.processor.sourcegenerator.imports;

import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.visitor.DAModelVisitor;

/**
 * MapperImportsModelVisitor - Visitor building the list of imports for the Mapper interface
 *
 * @author Sébastien Lesaint
 */
public class MapperImportsModelVisitor extends ImportListBuilder implements DAModelVisitor {

  @Override
  public void visit(DASourceClass daSourceClass) {
    addImport(daSourceClass.getType().getQualifiedName());
  }

  @Override
  public void visit(DAInterface daInterface) {
    addImports(daInterface.getType());
    for (DAType typeArg : daInterface.getType().getTypeArgs()) {
      addImports(typeArg);
    }
  }

  @Override
  public void visit(DAMethod daMethod) {
    // mapper interface does not define any method of it own, only the one
    // inherited from Guava's Function interface which imports are added via the DAInterface
    // TOIMPROVE : when supporting @MapperMethod, we will need to add imports from this method
  }

}
