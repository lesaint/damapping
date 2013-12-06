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

import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.ImportVisitor;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

/**
* DefaultImportVisitor -
*
* @author Sébastien Lesaint
*/
class DefaultImportVisitor implements ImportVisitor, DAImports {
    private final ImmutableList.Builder<DAName> mapperImports = ImmutableList.builder();
    private final ImmutableList.Builder<DAName> mapperImplImports = ImmutableList.builder();
    private final ImmutableList.Builder<DAName> mapperFactoryClassImports = ImmutableList.builder();
    private final ImmutableList.Builder<DAName> mapperFactoryInterfaceImports = ImmutableList.builder();
    private final ImmutableList.Builder<DAName> mapperFactoryImplImports = ImmutableList.builder();

    @Override
    public void addMapperImport(@Nullable DAName qualifiedDAName) {
        if (qualifiedDAName != null) {
            mapperImports.add(qualifiedDAName);
        }
    }

    @Override
    public void addMapperImport(@Nullable Iterable<DAName> qualifiedNames) {
        if (qualifiedNames != null) {
            mapperImports.addAll(qualifiedNames);
        }
    }

    @Override
    public void addMapperImplImport(@Nullable DAName qualifiedDAName) {
        if (qualifiedDAName != null) {
            mapperImplImports.add(qualifiedDAName);
        }
    }

    @Override
    public void addMapperImplImport(@Nullable Iterable<DAName> qualifiedNames) {
        if (qualifiedNames != null) {
            mapperImplImports.addAll(qualifiedNames);
        }
    }

    @Override
    public void addMapperFactoryClassImport(@Nullable DAName qualifiedDAName) {
        if (qualifiedDAName != null) {
            mapperFactoryClassImports.add(qualifiedDAName);
        }
    }

    @Override
    public void addMapperFactoryClassImport(@Nullable Iterable<DAName> qualifiedNames) {
        if (qualifiedNames != null) {
            mapperFactoryClassImports.addAll(qualifiedNames);
        }
    }

    @Override
    public void addMapperFactoryInterfaceImport(@Nullable DAName qualifiedDAName) {
        if (qualifiedDAName != null) {
            mapperFactoryInterfaceImports.add(qualifiedDAName);
        }
    }

    @Override
    public void addMapperFactoryInterfaceImport(@Nullable Iterable<DAName> qualifiedNames) {
        if (qualifiedNames != null) {
            mapperFactoryInterfaceImports.addAll(qualifiedNames);
        }
    }

    @Override
    public void addMapperFactoryImplImport(@Nullable DAName qualifiedDAName) {
        if (qualifiedDAName != null) {
            mapperFactoryImplImports.add(qualifiedDAName);
        }
    }

    @Override
    public void addMapperFactoryImplImport(@Nullable Iterable<DAName> qualifiedNames) {
        if (qualifiedNames != null) {
            mapperFactoryImplImports.addAll(qualifiedNames);
        }
    }

    @Override
    public List<DAName> getMapperImports() {
        return mapperImports.build();
    }

    @Override
    public List<DAName> getMapperImplImports() {
        return mapperImplImports.build();
    }

    @Override
    public List<DAName> getMapperFactoryClassImports() {
        return mapperFactoryClassImports.build();
    }

    @Override
    public List<DAName> getMapperFactoryInterfaceImports() {
        return mapperFactoryInterfaceImports.build();
    }

    @Override
    public List<DAName> getMapperFactoryImplImports() {
        return mapperFactoryImplImports.build();
    }
}
