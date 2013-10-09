package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

/**
* DAMapperClass -
*
* @author Sébastien Lesaint
*/
class DAMapperClass implements ImportVisitable {
    final TypeElement classElement;
    DAName packageName;
    DAType type;
    Set<Modifier> modifiers;
    List<DAInterface> interfaces;
    List<DAMethod> methods;
    // specific to the class annoted with @Mapper
    InstantiationType instantiationType;

    DAMapperClass(TypeElement classElement) {
        this.classElement = classElement;
    }

    @Override
    public void visite(ImportVisitor visitor) {
        visitor.addMapperImport(type.qualifiedName);
        visitor.addMapperImplImport(type.qualifiedName);
        visitor.addMapperFactoryImport(type.qualifiedName);
        for (DAInterface daInterface : interfaces) {
            daInterface.visite(visitor);
        }
        for (DAMethod daMethod : methods) {
            daMethod.visite(visitor);
        }
    }

}
