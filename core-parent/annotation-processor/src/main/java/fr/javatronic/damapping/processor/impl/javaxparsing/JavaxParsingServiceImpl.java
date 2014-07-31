package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Predicates;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * JavaxParsingService -
 *
 * @author Sébastien Lesaint
 */
public class JavaxParsingServiceImpl implements JavaxParsingService {
  private final ProcessingEnvironment processingEnv;
  private final JavaxExtractor javaxExtractor;

  public JavaxParsingServiceImpl(ProcessingEnvironment processingEnv,
                                 JavaxExtractor javaxExtractor) {
    this.processingEnv = processingEnv;
    this.javaxExtractor = javaxExtractor;
  }

  public JavaxParsingServiceImpl(ProcessingEnvironment processingEnv) {
    this(processingEnv, new JavaxExtractorImpl(processingEnv.getTypeUtils()));
  }

  @Nonnull
  public DASourceClass parse(TypeElement classElement) {
    DAType type = javaxExtractor.extractType(classElement.asType());

    DASourceClass.Builder<?> builder;
    if (classElement.getKind() == ElementKind.ENUM) {
      builder = DASourceClass.enumBuilder(type, javaxExtractor.extractEnumValues(classElement));
    }
    else if (classElement.getKind() == ElementKind.CLASS) {
      builder = DASourceClass.classbuilder(type);
    }
    else {
      throw new IllegalArgumentException("Unsupported Kind of TypeElement, must be either CLASS or ENUM");
    }

    // retrieve name of the package of the class with @Mapper
    builder.withPackageName(retrievePackageName(classElement));

    builder.withAnnotations(javaxExtractor.extractDAAnnotations(classElement));

    builder.withModifiers(
        from(classElement.getModifiers()).transform(javaxExtractor.toDAModifier()).toSet()
    );

    // retrieve interfaces implemented (directly and if any) by the class with @Mapper (+ their generics)
    // chercher si l'une d'elles est Function (Guava)
    List<DAInterface> interfaces = retrieveInterfaces(classElement);
    builder.withInterfaces(interfaces);

    // pour le moment, on ne traite pas les classes abstraites implémentées par la class @Mapper ni les interfaces
    // implémentées indirectement

    List<DAMethod> methods = retrieveMethods(classElement);
    builder.withMethods(methods);
    return builder.build();
  }

  @Override
  @Nonnull
  public List<DAMethod> retrieveMethods(final TypeElement classElement) {
    if (classElement.getEnclosedElements() == null) {
      return Collections.emptyList();
    }

    return from(classElement.getEnclosedElements())
        // methods are ExecutableElement
        .filter(Predicates.instanceOf(ExecutableElement.class))
            // transform
        .transform(new Function<Element, DAMethod>() {
          @Nullable
          @Override
          public DAMethod apply(@Nullable Element o) {
            if (o == null) {
              return null;
            }

            ExecutableElement methodElement = (ExecutableElement) o;
            DAMethod.Builder builder = daMethodBuilder(methodElement);
            DAMethod.Builder res = builder
                .withAnnotations(javaxExtractor.extractDAAnnotations(methodElement))
                .withModifiers(javaxExtractor.extractModifiers(methodElement))
                .withParameters(javaxExtractor.extractParameters(methodElement));
            if (o.getKind() == ElementKind.CONSTRUCTOR) {
              res.withName(DANameFactory.from(uncapitalize(classElement.getSimpleName().toString())));
              res.withReturnType(javaxExtractor.extractType(classElement.asType()));
            }
            else {
              res.withName(JavaxDANameFactory.from(o.getSimpleName()));
              res.withReturnType(javaxExtractor.extractReturnType(methodElement));
            }
            return res.build();
          }
        }
        )
        .filter(Predicates.notNull())
        .toList();
  }
  public static String uncapitalize(String str) {
    if (str == null || str.length() == 0) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str.length());
    sb.append(Character.toLowerCase(str.charAt(0)));
    sb.append(str.substring(1));
    return sb.toString();
  }

  private DAMethod.Builder daMethodBuilder(ExecutableElement element) {
    if (element.getKind() == ElementKind.METHOD) {
      return DAMethod.methodBuilder();
    }
    if (element.getKind() == ElementKind.CONSTRUCTOR) {
      return DAMethod.constructorBuilder();
    }
    throw new IllegalArgumentException(
        String.format(
            "Kind %s of element %s is not supported to build a DAMethod from", element.getKind(),
            element
        )
    );
  }

  @Override
  public List<DAInterface> retrieveInterfaces(final TypeElement classElement) {
    List<? extends TypeMirror> interfaces = classElement.getInterfaces();
    if (interfaces == null) {
      return Collections.emptyList();
    }

    return from(interfaces).transform(new Function<TypeMirror, DAInterface>() {
      @Nullable
      @Override
      public DAInterface apply(@Nullable TypeMirror o) {
        // TOIMPROVE : le filtrage des interfaces de la classe annotée avec @Mapper sur DeclaredType est-il pertinent ?
        if (!(o instanceof DeclaredType)) {
          processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
              "Interface is not a DeclaredType, not supported", classElement
          );
          return null;
        }

        return new DAInterface(javaxExtractor.extractType(o));
      }
    }
    ).filter(Predicates.notNull()).toList();
  }

  @Override
  public DAName retrievePackageName(TypeElement classElement) {
    PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
    return JavaxDANameFactory.from(packageElement.getQualifiedName());
  }

}
