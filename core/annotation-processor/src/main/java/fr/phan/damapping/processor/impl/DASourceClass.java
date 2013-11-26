package fr.phan.damapping.processor.impl;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;
import com.google.common.base.Predicate;
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
        visitor.addMapperImport(type.qualifiedName);
        visitor.addMapperImplImport(type.qualifiedName);
        visitor.addMapperFactoryClassImport(type.qualifiedName);
        visitor.addMapperFactoryInterfaceImport(type.qualifiedName);
        visitor.addMapperFactoryImplImport(type.qualifiedName);
        for (DAInterface daInterface : interfaces) {
            daInterface.visite(visitor);
        }
        for (DAMethod daMethod : Iterables.filter(methods, DAMethodPredicates.isGuavaFunction())) {
            daMethod.visite(visitor);
        }
    }

}
