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
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.visitor.DAModelVisitor;

/**
 * MapperFactoryInterfaceImportsModelVisitor - Visitor building the list of imports for the MapperFactory interface
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryInterfaceImportsModelVisitor extends ImportListBuilder implements DAModelVisitor {
    @Override
    public void visit(DASourceClass daSourceClass) {
        addImport(daSourceClass.getType().getQualifiedName());
    }

    @Override
    public void visit(DAInterface daInterface) {
        // interfaces are not used in the Factory
    }

    @Override
    public void visit(DAMethod daMethod) {
        // mapperFactoryMethod are exposed as methods of the MapperFactory
        if (daMethod.isMapperFactoryMethod()) {
            for (DAParameter parameter : daMethod.getParameters()) {
                addImports(parameter.getType());
            }
        }
    }
}
