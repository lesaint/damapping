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

import javax.annotation.Nullable;

/**
* ImportVisitor -
*
* @author Sébastien Lesaint
*/
public interface ImportVisitor {
    void addMapperImport(@Nullable DAName qualifiedName);
    void addMapperImport(@Nullable Iterable<DAName> qualifiedNames);
    void addMapperImplImport(@Nullable DAName qualifiedName);
    void addMapperImplImport(@Nullable Iterable<DAName> qualifiedNames);
    void addMapperFactoryClassImport(@Nullable DAName qualifiedName);
    void addMapperFactoryClassImport(@Nullable Iterable<DAName> qualifiedNames);
    void addMapperFactoryInterfaceImport(@Nullable DAName qualifiedName);
    void addMapperFactoryInterfaceImport(@Nullable Iterable<DAName> qualifiedNames);
    void addMapperFactoryImplImport(@Nullable DAName qualifiedName);
    void addMapperFactoryImplImport(@Nullable Iterable<DAName> qualifiedNames);
}
