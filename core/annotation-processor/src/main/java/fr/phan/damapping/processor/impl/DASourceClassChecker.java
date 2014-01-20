package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DASourceClass;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Set;

/**
 * DASourceClassChecker -
 *
 * @author: Sébastien Lesaint
 */
public interface DASourceClassChecker {
    void checkModifiers(Set<Modifier> modifiers) throws CheckError;

    // TODO make interface check for Guava Function optional when supported @MapperFunction
    void checkInterfaces(List<DAInterface> interfaces) throws CheckError;

    void checkInstantiationTypeRequirements(DASourceClass daSourceClass) throws CheckError;

    void checkMethods(List<DAMethod> methods) throws CheckError;
}
