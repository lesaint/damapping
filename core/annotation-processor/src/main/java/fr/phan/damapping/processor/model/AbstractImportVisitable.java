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
package fr.phan.damapping.processor.model;

/**
* AbstractImportVisitable - Abstract implementation of ImportVisitable which provides methods to be implemented for
* each generated class in DA Mapping Framework.
*
* @author Sébastien Lesaint
*/
public abstract class AbstractImportVisitable implements ImportVisitable {

    @Override
    public void visite(ImportVisitor visitor) {
        visiteForMapper(visitor);
        visiteForMapperImpl(visitor);
        visiteForMapperFactoryClass(visitor);
        visiteForMapperFactoryInterface(visitor);
        visiteForMapperFactoryImpl(visitor);
    }

    protected abstract void visiteForMapper(ImportVisitor visitor);

    protected abstract void visiteForMapperImpl(ImportVisitor visitor);

    protected abstract void visiteForMapperFactoryClass(ImportVisitor visitor);

    protected abstract void visiteForMapperFactoryInterface(ImportVisitor visitor);

    protected abstract void visiteForMapperFactoryImpl(ImportVisitor visitor);
}
