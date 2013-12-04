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

import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import com.google.common.collect.Iterables;

/**
* DASourceClass - Représente la class annotée avec @Mapper
*
* @author Sébastien Lesaint
*/
class DASourceClass implements ImportVisitable {
    final TypeElement classElement;
    DAName packageName;
    DAType type;
    Set<Modifier> modifiers;
    List<DAInterface> interfaces;
    List<DAMethod> methods;
    // specific to the class annoted with @Mapper
    InstantiationType instantiationType;

    DASourceClass(TypeElement classElement) {
        this.classElement = classElement;
    }

    @Override
    public void visite(ImportVisitor visitor) {
        visitor.addMapperImport(type.getQualifiedName());
        visitor.addMapperImplImport(type.getQualifiedName());
        visitor.addMapperFactoryClassImport(type.getQualifiedName());
        visitor.addMapperFactoryInterfaceImport(type.getQualifiedName());
        visitor.addMapperFactoryImplImport(type.getQualifiedName());
        for (DAInterface daInterface : interfaces) {
            daInterface.visite(visitor);
        }
        for (DAMethod daMethod : Iterables.filter(methods, DAMethodPredicates.isGuavaFunction())) {
            daMethod.visite(visitor);
        }
    }

}
