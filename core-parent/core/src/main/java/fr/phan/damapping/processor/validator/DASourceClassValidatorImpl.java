package fr.phan.damapping.processor.validator;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.predicate.DAInterfacePredicates;
import fr.phan.damapping.processor.model.predicate.DAMethodPredicates;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;

/**
 * DASourceClassValidator -
 *
 * TODO add unit test coverage for DASourceClassValidatorImpl
 *
 * @author: Sébastien Lesaint
 */
public class DASourceClassValidatorImpl implements DASourceClassValidator {
    @Override
    public void validate(DASourceClass sourceClass) throws ValidationError {
        validateModifiers(sourceClass.getModifiers());
        validateInterfaces(sourceClass.getInterfaces());
        validateMethods(sourceClass.getMethods());

        // retrieve instantiation type from @Mapper annotation
        //  - CONSTRUCTOR : validate public/protected default constructor exists sinon erreur de compilation
        //  - SINGLETON_ENUM : validate @Mapper class is an enum + validate there is only one value sinon erreur de compilation
        //  - SPRING_COMPONENT : TOFINISH quelles vérifications sur la class si le InstantiationType est SPRING_COMPONENT ?
        validateInstantiationTypeRequirements(sourceClass);
    }

    @Override
    public void validateModifiers(Set<DAModifier> modifiers) throws ValidationError {
        // retrieve qualifiers of the class with @Mapper + make validate : must be public or protected sinon erreur de compilation
        if (modifiers.contains(DAModifier.PRIVATE)) {
            throw new ValidationError("Class annoted with @Mapper can not be private");
        }
    }

    // TODO make interface validate for Guava Function optional when supported @MapperFunction
    @Override
    public void validateInterfaces(List<DAInterface> interfaces) throws ValidationError {
        // rechercher si la classe Mapper implémente Function
        List<DAInterface> guavaFunctionInterfaces = from(interfaces)
                .filter(DAInterfacePredicates.isGuavaFunction())
                .toList();
        if (guavaFunctionInterfaces.size() > 1) {
            throw new ValidationError("Mapper implementing more than one Function interface is not supported");
        }
        if (guavaFunctionInterfaces.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit @MapperMethod
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
                hasOnlyOneEnumValue(daSourceClass.getClassElement());
                break;
            case CONSTRUCTOR_FACTORY:
                // TODO ajouter checks pour InstantiationType.CONSTRUCTOR_FACTORY (vérifier que pas d'autre méthode annotée avec @MapperFactoryMethod)
                break;
            case STATIC_FACTORY:
                // TODO ajouter checks pour InstantiationType.public_FACTORY (vérifier que pas de constructeur à paramètre)
                break;
            default:
                throw new IllegalArgumentException("Unsupported instantiationType " + daSourceClass.getInstantiationType());
        }
    }

    private void hasAccessibleConstructor(List<DAMethod> methods) throws ValidationError {
        Optional<DAMethod> accessibleConstructor = FluentIterable.from(methods)
                .filter(DAMethodPredicates.isConstructor())
                .filter(DAMethodPredicates.notPrivate())
                .first();

        if (!accessibleConstructor.isPresent()) {
            throw new ValidationError("Classe does not exposed an accessible default constructor");
        }
    }

    private void hasOnlyOneEnumValue(TypeElement classElement) throws ValidationError {
        if (classElement.getEnclosedElements() == null) {
            // this case can not occurs because it is enforced by the java compiler
            throw new ValidationError("Enum annoted wih @Mapper must have one value");
        }

        int res = from(classElement.getEnclosedElements())
                // enum values are VariableElement
                .filter(Predicates.instanceOf(VariableElement.class))
                .size();
        if (res != 1) {
            throw new ValidationError("Enum annoted with @Mapper must have just one value");
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
        if (guavaFunctionMethods.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit @MapperMethod
            throw new ValidationError("Mapper not having a apply method is not supported");
        }
    }
}
