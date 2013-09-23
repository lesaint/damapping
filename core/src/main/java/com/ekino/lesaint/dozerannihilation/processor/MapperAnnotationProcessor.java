package com.ekino.lesaint.dozerannihilation.processor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import com.google.common.base.Optional;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {
    protected MapperAnnotationProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv, Mapper.class);
    }

    @Override
    protected void process(Element element, RoundEnvironment roundEnv) throws IOException {
        if (element.getKind() != ElementKind.CLASS) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Type annoted with @Mapper annotation is not class " + element);
            return;
        }

        TypeElement classElement = (TypeElement) element;
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
        InstantiationType instantiationType = getInstantiationType(classElement);
        System.out.println("Processing " + classElement.getQualifiedName() + " in " + getClass().getCanonicalName());

        // retrieve name of the package of the class with @Mapper

        // retrieve name of the class with @Mapper

        // retrieve qualifiers of the class with @Mapper + make check : must be public or protected sinon erreur de compilation

        // retrieve instantiation type from @Mapper annotation
        //  - CONSTRUCTOR : check public/protected default constructor exists sinon erreur de compilation
        //  - SINGLETON_ENUM : check @Mapper class is an enum + check there is only one value sinon erreur de compilation
        //  - SPRING_COMPONENT : TOFINISH quelles vérifications sur la class si le InstantiationType est SPRING_COMPONENT ?

        // retrieve interfaces implemented (directly and if any) by the class with @Mapper (+ their generics)
        // chercher si l'une d'elles est Function (Guava)

        // pour le moment, on ne traite pas les classes abstraites implémentées par la class @Mapper ni les interfaces
        // implémentées indirectement

        // rechercher une ou plusieurs méthodes annontées avec @MapperFunction
        // si classe @Mapper implémente Function, la rechercher en commençant par les méthodes annotées avec @MapperFunction
        // si aucune méthode trouvée => erreur  de compilation

        // 1 - générer l'interface du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper)
        //     -> visibilite de la classe (protected ou public ?)
        //     -> liste des interfaces implémentées
        //     -> compute liste des imports à réaliser

        // 2 - générer la factory
        //     -> nom du package
        //     -> nom de la classe (infère nom de la factory et nom du Mapper)
        //     -> type d'instantiation (si enum, le nom de la valeur d'enum à utiliser)

        // 3 - générer l'implémentation du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
        //     -> liste des méthodes mapper
        //     -> compute liste des imports à réaliser

        // TODO switch on the various InstantiationType values

//        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(classElement.getQualifiedName() + "Mapper", element);
//        System.out.println("generating " + jfo.toUri());
//        BufferedWriter bw = new BufferedWriter(jfo.openWriter());
//        bw.append("package ");
//        bw.append(packageElement.getQualifiedName());
//        bw.append(";");
//        bw.newLine();
//        bw.append("import com.google.common.base.Function;");
//        bw.newLine();
//        bw.append("public interface ");
//        bw.append(classElement.getSimpleName() + "Mapper");
//        bw.append(" implements Function<");
//        bw.flush();
//        bw.close();
    }

    private InstantiationType getInstantiationType(TypeElement classElement) {
        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(classElement, Mapper.class);
        if (!annotationMirror.isPresent()) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ); TODO add an error message if expected Annotation is not found on element
            return null;
        }

        String enumValue = getEnumNameElementValue(annotationMirror.get(), "value");

        // TODO quel est le comportement s'il n'y a pas de valeur explicite ? on récupère null et on doit aller récupérer la valeur par défaut dans l'AnnotationMiror ou la valeur par défaut est automatiquement inférée ?

        return InstantiationType.valueOf(enumValue);
    }

    private static Optional<AnnotationMirror> getAnnotationMirror(TypeElement classElement,
                                                                  Class<? extends Annotation> annotationClass) {
        for (AnnotationMirror annotationMirror : classElement.getAnnotationMirrors()) {
            if (annotationClass.getCanonicalName().equals(annotationMirror.getAnnotationType().toString())) { // TODO put test to identify AnnotationMirror by Class into a Predicate
                return Optional.of(annotationMirror);
            }
        }
        return Optional.absent();
    }

    private static String getEnumNameElementValue(AnnotationMirror annotationMirror, String elementName) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> elementValue : annotationMirror.getElementValues().entrySet()) {
            if (elementName.equals(elementValue.getKey().getSimpleName().toString())) {
                // VariableElement is the type return by getValue() representing an enum constant (see @AnnotationValue)
                VariableElement variableElement = (VariableElement) elementValue.getValue().getValue();
                return variableElement.getSimpleName().toString();
            }
        }
        return null;
    }
}
