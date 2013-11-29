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
import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

/**
* DAMethod -
*
* @author Sébastien Lesaint
*/
class DAMethod extends AbstractImportVisitable {
    ElementKind kind;
    /*nom de la méthode/function*/
    DAName name;
    /*modifiers de la méthode (private, final, ...)*/
    Set<Modifier> modifiers;
    /*le type de retour de la méthode. Null si la méthode est un constructeur*/
    @Nullable
    DAType returnType; // TOIMPROVE : attention au cas des primitifs si on ajoute @MapperMethod !
    List<DAParameter> parameters;
    /*non utilisé tant que pas de @MapperMethod*/
    boolean mapperMethod;
    /*indique si cette méthode était annotée avec @MapperFactoryMethod*/
    boolean mapperFactoryMethod;

    public boolean isDefaultConstructor() {
        return isConstructor() && parameters.isEmpty();
    }

    public boolean isConstructor() {
        return kind == ElementKind.CONSTRUCTOR;
    }

    public boolean isGuavaFunction() {
        // TOIMPROVE, check more specific info in the model, can we know if method override from an interface ? we should check the parameter type and the return type
        return kind == ElementKind.METHOD && "apply".equals(name.getName());
    }

    @Override
    protected void visiteForMapper(ImportVisitor visitor) {
        if (isGuavaFunction()) {
            // guava function is not generated in Mapper interface because it is declared by implemented Function interface
            return;
        }
        if (isDefaultConstructor()) {
            // constructor is not generated in Mapper interface
            return;
        }
        for (DAParameter parameter : parameters) {
            visitor.addMapperImport(parameter.type.getImports());
        }
        if (returnType != null) {
            visitor.addMapperImport(returnType.getImports());
        }
    }

    @Override
    protected void visiteForMapperImpl(ImportVisitor visitor) {
        if (isDefaultConstructor()) {
            // constructor is not generated in MapperImpl class
            return;
        }
        for (DAParameter parameter : parameters) {
            visitor.addMapperImplImport(parameter.type.getImports());
        }
        if (returnType != null) {
            visitor.addMapperImplImport(returnType.getImports());
        }
    }

    @Override
    protected void visiteForMapperFactoryClass(ImportVisitor visitor) {
        // none
    }

    @Override
    protected void visiteForMapperFactoryInterface(ImportVisitor visitor) {
        // mapperFactoryMethod are exposed as methods of the MapperFactory
        if (mapperFactoryMethod) {
            for (DAParameter parameter : parameters) {
                visitor.addMapperFactoryInterfaceImport(parameter.type.getImports());
            }
        }
    }

    @Override
    protected void visiteForMapperFactoryImpl(ImportVisitor visitor) {
        // mapperFactoryMethod are exposed as methods of the MapperFactory
        if (isConstructor()&& mapperFactoryMethod) {
            for (DAParameter parameter : parameters) {
                visitor.addMapperFactoryImplImport(parameter.type.getImports());
            }
        }

        if (isGuavaFunction()) { // remplacer par isMapperMethod
            for (DAParameter parameter : parameters) {
                visitor.addMapperFactoryImplImport(parameter.type.getImports());
            }
        }
    }
}
