package fr.phan.damapping.processor.impl;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.predicate.DAInterfacePredicates;
import fr.phan.damapping.processor.model.predicate.DAMethodPredicates;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;

/**
 * DASourceClassChecker -
 *
 * @author: Sébastien Lesaint
 */
public class DASourceClassCheckerImpl implements DASourceClassChecker {
    @Override
    public void checkModifiers(Set<Modifier> modifiers) throws CheckError {
        // retrieve qualifiers of the class with @Mapper + make check : must be public or protected sinon erreur de compilation
        if (modifiers.contains(Modifier.PRIVATE)) {
            throw new CheckError("Class annoted with @Mapper can not be private");
        }
    }

    // TODO make interface check for Guava Function optional when supported @MapperFunction
    @Override
    public void checkInterfaces(List<DAInterface> interfaces) throws CheckError {
        // rechercher si la classe Mapper implémente Function
        List<DAInterface> guavaFunctionInterfaces = from(interfaces)
                .filter(DAInterfacePredicates.isGuavaFunction())
                .toList();
        if (guavaFunctionInterfaces.size() > 1) {
            throw new CheckError("Mapper implementing more than one Function interface is not supported");
        }
        if (guavaFunctionInterfaces.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit @MapperMethod
            throw new CheckError("Mapper not implementing Function interface is not supported");
        }
    }

    @Override
    public void checkInstantiationTypeRequirements(DASourceClass daSourceClass) throws CheckError {
        // TODO vérifier qu'il n'y a pas d'usage illegal de @MapperFactoryMethod (ie. sur méthode non statique)
        switch (daSourceClass.getInstantiationType()) {
            case SPRING_COMPONENT:
                // requirements are enforced by Spring
                break;
            case CONSTRUCTOR:
                hasAccessibleConstructor(daSourceClass.getClassElement(), daSourceClass.getMethods());
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

    private void hasAccessibleConstructor(TypeElement classElement, List<DAMethod> methods) throws CheckError {
        Optional<DAMethod> accessibleConstructor = FluentIterable.from(methods)
                .filter(DAMethodPredicates.isConstructor())
                .filter(DAMethodPredicates.notPrivate())
                .first();

        if (!accessibleConstructor.isPresent()) {
            throw new CheckError("Classe does not exposed an accessible default constructor");
        }
    }

    private void hasOnlyOneEnumValue(TypeElement classElement) throws CheckError {
        if (classElement.getEnclosedElements() == null) {
            // this case can not occurs because it is enforced by the java compiler
            throw new CheckError("Enum annoted wih @Mapper must have one value");
        }

        int res = from(classElement.getEnclosedElements())
                // enum values are VariableElement
                .filter(Predicates.instanceOf(VariableElement.class))
                .size();
        if (res != 1) {
            throw new CheckError("Enum annoted with @Mapper must have just one value");
        }
    }

    @Override
    public void checkMethods(List<DAMethod> methods) throws CheckError {
        // rechercher une ou plusieurs méthodes annotées avec @MapperFunction
        // si classe @Mapper implémente Function, la rechercher en commençant par les méthodes annotées avec @MapperFunction
        // si aucune méthode trouvée => erreur  de compilation
        // TOIMPROVE : la récupération et les contrôles sur la méthode apply sont faibles
        if (methods.isEmpty()) {
            throw new CheckError("Class annoted with @Mapper must have at least one methode");
        }
        List<DAMethod> guavaFunctionMethods = from(methods).filter(DAMethodPredicates.isGuavaFunction()).toList();
        if (guavaFunctionMethods.size() > 1) {
            throw new CheckError("Mapper having more than one apply method is not supported");
        }
        if (guavaFunctionMethods.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit @MapperMethod
            throw new CheckError("Mapper not having a apply method is not supported");
        }
    }
}
