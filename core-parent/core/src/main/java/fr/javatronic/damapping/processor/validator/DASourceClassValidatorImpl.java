package fr.javatronic.damapping.processor.validator;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAInterfacePredicates;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.util.Optional;

import java.util.List;
import java.util.Set;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * DASourceClassValidator -
 * <p/>
 * TODO add unit test coverage for DASourceClassValidatorImpl
 *
 * @author Sébastien Lesaint
 */
public class DASourceClassValidatorImpl implements DASourceClassValidator {
  @Override
  public void validate(DASourceClass sourceClass) throws ValidationError {
    validateAnnotations(sourceClass.getAnnotations());
    validateModifiers(sourceClass.getModifiers());
    validateInterfaces(sourceClass.getInterfaces());
    validateMethods(sourceClass.getMethods());

    // retrieve instantiation type from @Mapper annotation
    //  - CONSTRUCTOR : validate public/protected default constructor exists sinon erreur de compilation
    //  - SINGLETON_ENUM : validate @Mapper class is an enum + validate there is only one value sinon erreur de
    // compilation
    //  - SPRING_COMPONENT : TOFINISH quelles vérifications sur la class si le InstantiationType est SPRING_COMPONENT ?
    validateInstantiationTypeRequirements(sourceClass);
  }

  private void validateAnnotations(List<DAAnnotation> annotations) throws ValidationError {
    List<DAAnnotation> mapperAnnotations = from(annotations).filter(DAAnnotationPredicates.isMapper()).toList();
    if (mapperAnnotations.size() > 1) {
      throw new ValidationError("Mapper with more than one @Mapper annotation is not supported");
    }
    if (mapperAnnotations.isEmpty()) {
      throw new ValidationError("Mapper without @Mapper annotation is not supported");
    }
  }

  @Override
  public void validateModifiers(Set<DAModifier> modifiers) throws ValidationError {
    // retrieve qualifiers of the class with @Mapper + make validate : must be public or protected sinon erreur de
    // compilation
    if (modifiers.contains(DAModifier.PRIVATE)) {
      throw new ValidationError("Class annoted with @Mapper can not be private");
    }
  }

  // TODO make interface validate for Guava Function optional when supported @MapperFunction
  @Override
  public void validateInterfaces(List<DAInterface> interfaces) throws ValidationError {
    // rechercher si la classe Mapper implémente Function
    List<DAInterface> guavaFunctionInterfaces =
        from(interfaces).filter(DAInterfacePredicates.isGuavaFunction()).toList();
    if (guavaFunctionInterfaces.size() > 1) {
      throw new ValidationError("Mapper implementing more than one Function interface is not supported");
    }
    if (guavaFunctionInterfaces.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit
      // @MapperMethod
      throw new ValidationError("Mapper not implementing Function interface is not supported");
    }
  }

  @Override
  public void validateInstantiationTypeRequirements(DASourceClass daSourceClass) throws ValidationError {
    // TODO vérifier qu'il n'y a pas d'usage illegal de @MapperFactoryMethod (ie. sur méthode non statique)
    switch (daSourceClass.getInstantiationType()) {
      case SPRING_COMPONENT:
        // requirements are enforced by Spring
        break;
      case CONSTRUCTOR:
        hasAccessibleConstructor(daSourceClass.getMethods());
        break;
      case SINGLETON_ENUM:
        hasOnlyOneEnumValue(daSourceClass);
        break;
      case CONSTRUCTOR_FACTORY:
        // TODO ajouter checks pour InstantiationType.CONSTRUCTOR_FACTORY (vérifier que pas d'autre méthode annotée
        // avec @MapperFactoryMethod)
        break;
      case STATIC_FACTORY:
        // TODO ajouter checks pour InstantiationType.public_FACTORY (vérifier que pas de constructeur à paramètre)
        break;
      default:
        throw new IllegalArgumentException("Unsupported instantiationType " + daSourceClass.getInstantiationType());
    }
  }

  private void hasAccessibleConstructor(List<DAMethod> methods) throws ValidationError {
    Optional<DAMethod> accessibleConstructor = from(methods)
        .filter(DAMethodPredicates.isConstructor())
        .filter(DAMethodPredicates.notPrivate())
        .first();

    if (!accessibleConstructor.isPresent()) {
      throw new ValidationError("Classe does not exposed an accessible default constructor");
    }
  }

  private void hasOnlyOneEnumValue(DASourceClass daSourceClass) throws ValidationError {
    if (daSourceClass.getEnumValues().size() != 1) {
      throw new ValidationError("Enum annoted wih @Mapper must have one value");
    }
  }

  @Override
  public void validateMethods(List<DAMethod> methods) throws ValidationError {
    // rechercher une ou plusieurs méthodes annotées avec @MapperFunction
    // si classe @Mapper implémente Function, la rechercher en commençant par les méthodes annotées avec @MapperFunction
    // si aucune méthode trouvée => erreur  de compilation
    // TOIMPROVE : la récupération et les contrôles sur la méthode apply sont faibles
    if (methods.isEmpty()) {
      throw new ValidationError("Class annoted with @Mapper must have at least one methode");
    }
    List<DAMethod> guavaFunctionMethods = from(methods).filter(DAMethodPredicates.isGuavaFunction()).toList();
    if (guavaFunctionMethods.size() > 1) {
      throw new ValidationError("Mapper having more than one apply method is not supported");
    }
    if (guavaFunctionMethods.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit
      // @MapperMethod
      throw new ValidationError("Mapper not having a apply method is not supported");
    }
  }
}
