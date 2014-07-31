package fr.javatronic.damapping.processor.validator;

import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DASourceClass;

import java.util.List;
import java.util.Set;

/**
 * DASourceClassValidator - Class responsible for validating part or all of a DASourceClass instance before processing
 * to generate classes.
 *
 * @author Sébastien Lesaint
 */
public interface DASourceClassValidator {
  void validate(DASourceClass sourceClass) throws ValidationError;

  void validateModifiers(Set<DAModifier> modifiers) throws ValidationError;

  void validateInstantiationTypeRequirements(DASourceClass daSourceClass) throws ValidationError;

  void validateMethods(List<DAMethod> methods) throws ValidationError;
}
