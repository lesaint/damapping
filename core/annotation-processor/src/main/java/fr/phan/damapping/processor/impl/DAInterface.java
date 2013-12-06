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
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.ImportVisitor;

import com.google.common.base.Function;

/**
* DAInterface -
*
* @author Sébastien Lesaint
*/
class DAInterface extends AbstractImportVisitable {
    DAType type;

    public boolean isGuavaFunction() {
        return type.getQualifiedName() != null && Function.class.getCanonicalName().equals(type.getQualifiedName().getName());
    }

    @Override
    protected void visiteForMapper(ImportVisitor visitor) {
        visitor.addMapperImport(type.getImports());
        for (DAType typeArg : type.getTypeArgs()) {
            visitor.addMapperImport(typeArg.getImports());
        }
    }

    @Override
    protected void visiteForMapperImpl(ImportVisitor visitor) {
        // interface are declared directly only in Mapper
        // in MapperImpl there is no need to import them again since they are inherited from Mapper
    }

    @Override
    protected void visiteForMapperFactoryClass(ImportVisitor visitor) {
        // interfaces are not used in the Factory
    }

    @Override
    protected void visiteForMapperFactoryInterface(ImportVisitor visitor) {
        // interfaces are not used in MapperFactory interface
    }

    @Override
    protected void visiteForMapperFactoryImpl(ImportVisitor visitor) {
        // interfaces are not used in MapperFactory impl
    }
}
