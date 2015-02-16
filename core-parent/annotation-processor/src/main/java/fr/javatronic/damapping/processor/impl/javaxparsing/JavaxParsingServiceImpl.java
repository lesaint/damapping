/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.processor.impl.javaxparsing.generics.GenericTypeContext;
import fr.javatronic.damapping.processor.impl.javaxparsing.generics.GenericTypeContextImpl;
import fr.javatronic.damapping.processor.impl.javaxparsing.model.JavaxDAMethod;
import fr.javatronic.damapping.processor.impl.javaxparsing.model.JavaxDASourceClass;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.function.ToGuavaFunctionOrMapperMethod;
import fr.javatronic.damapping.processor.model.impl.DAInterfaceImpl;
import fr.javatronic.damapping.processor.model.impl.DAMethodImpl;
import fr.javatronic.damapping.processor.model.impl.DASourceClassImpl;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

import static fr.javatronic.damapping.processor.model.impl.DAMethodImpl.makeGuavaFunctionApplyMethod;
import static fr.javatronic.damapping.processor.model.impl.DAMethodImpl.makeMapperMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;
import static fr.javatronic.damapping.util.Predicates.notNull;

/**
 * JavaxParsingService -
 *
 * @author Sébastien Lesaint
 */
public class JavaxParsingServiceImpl implements JavaxParsingService {
  @Nonnull
  private final ProcessingEnvironmentWrapper processingEnv;

  public JavaxParsingServiceImpl(@Nonnull ProcessingEnvironmentWrapper processingEnv) {
    this.processingEnv = checkNotNull(processingEnv);
  }

  @Nonnull
  @Override
  public ParsingResult parse(@Nonnull TypeElement classElement,
                             @Nullable Collection<DAType> generatedTypes) throws IOException {
    ReferenceScanResult scanResult = new ReferencesScanner(processingEnv, generatedTypes).scan(classElement);
    if (scanResult.hasUnresolved()) {
      return ParsingResult.later(classElement, null, scanResult.getUnresolved());
    }

    DAType type = null;
    try {
      JavaxExtractorImpl javaxExtractor = new JavaxExtractorImpl(processingEnv, scanResult);
      type = javaxExtractor.extractType(classElement.asType());
      DASourceClass daSourceClass = parseImpl(classElement, type, javaxExtractor);

      return ParsingResult.ok(classElement, daSourceClass);
    } catch (Exception e) {
      processingEnv.printMessage(Mapper.class, classElement, e);
      return ParsingResult.failed(classElement, type);
    }
  }

  @Nonnull
  private DASourceClass parseImpl(TypeElement classElement, DAType type, JavaxExtractor javaxExtractor) {
    DASourceClassImpl.Builder<?> builder;
    if (classElement.getKind() == ElementKind.ENUM) {
      builder = DASourceClassImpl.enumBuilder(type, javaxExtractor.extractEnumValues(classElement));
    }
    else if (classElement.getKind() == ElementKind.CLASS) {
      builder = DASourceClassImpl.classbuilder(type);
    }
    else {
      throw new IllegalArgumentException("Unsupported Kind of TypeElement, must be either CLASS or ENUM");
    }

    builder.withAnnotations(javaxExtractor.extractDAAnnotations(classElement));

    builder.withModifiers(
        from(classElement.getModifiers()).transform(javaxExtractor.toDAModifier()).toSet()
    );

    GenericTypeContext genericTypeContext = GenericTypeContextImpl.create(
        processingEnv.getTypeUtils().asDeclaredType(classElement.asType())
    );
    List<DAInterface> interfaces = retrieveInterfaces(classElement, javaxExtractor, genericTypeContext);
    builder.withInterfaces(interfaces);

    builder.withMethods(
        from(retrieveMethods(classElement, javaxExtractor, genericTypeContext))
            .transform(new JavaxToGuavaFunctionOrMapperMethod(interfaces))
            .toList()
    );

    return new JavaxDASourceClass(builder.build(), classElement);
  }

  private static class JavaxToGuavaFunctionOrMapperMethod extends ToGuavaFunctionOrMapperMethod<JavaxDAMethod> {

    public JavaxToGuavaFunctionOrMapperMethod(List<DAInterface> interfaces) {
      super(interfaces);
    }

    @Override
    @Nonnull
    protected DAMethod toMapperMethod(@Nonnull JavaxDAMethod daMethod) {
      return new JavaxDAMethod(makeMapperMethod(daMethod), daMethod.getMethodElement());
    }

    @Override
    @Nonnull
    protected DAMethod toGuavaFunction(@Nonnull JavaxDAMethod daMethod) {
      return new JavaxDAMethod(makeGuavaFunctionApplyMethod(daMethod), daMethod.getMethodElement());
    }
  }

  private List<DAInterface> retrieveInterfaces(final TypeElement classElement,
                                               final JavaxExtractor javaxExtractor,
                                               GenericTypeContext genericTypeContext) {
    List<TypeMirror> classHierarchyAsList = processingEnv.getTypeUtils().extractClassHierarchyAsList(classElement, true);

    List<DAInterface> res = new ArrayList<>();
    GenericTypeContext currentGenericTypeContext = genericTypeContext;
    for (TypeMirror typeMirror : classHierarchyAsList) {
      DeclaredType declaredType = processingEnv.getTypeUtils().asDeclaredType(typeMirror);
      currentGenericTypeContext = currentGenericTypeContext.subContext(declaredType);
      extractInterfaces(
          processingEnv.getElementUtils().asTypeElement(declaredType.asElement()),
          currentGenericTypeContext,
          javaxExtractor,
          res
      );
    }

    return res;
  }

  private void extractInterfaces(TypeElement typeElement,
                                 GenericTypeContext genericTypeContext,
                                 JavaxExtractor javaxExtractor,
                                 List<DAInterface> res) {
    List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
    if (interfaces == null) {
      return;
    }
    res.addAll(from(interfaces).transform(new TypeMirrorToDAInterfaceWithGeneric(javaxExtractor, genericTypeContext)).toList());
    for (TypeMirror anInterface : interfaces) {
      DeclaredType declaredType = processingEnv.getTypeUtils().asDeclaredType(anInterface);
      TypeElement interfaceTypeElement = processingEnv.getElementUtils().asTypeElement(declaredType.asElement());

      if (interfaceTypeElement != null) {
        extractInterfaces(interfaceTypeElement, genericTypeContext, javaxExtractor, res);
        genericTypeContext = genericTypeContext.subContext(declaredType);
      }
    }
  }

  private static class TypeMirrorToDAInterfaceWithGeneric implements Function<TypeMirror, DAInterface> {
    private final SimpleTypeVisitor6<DAInterface, Void> daInterfaceExtractor;

    public TypeMirrorToDAInterfaceWithGeneric(final JavaxExtractor javaxExtractor, final GenericTypeContext genericTypeContext) {
      this.daInterfaceExtractor = new SimpleTypeVisitor6<DAInterface, Void>() {

        @Override
        public DAInterface visitDeclared(DeclaredType declaredType, Void aVoid) {
          return new DAInterfaceImpl(javaxExtractor.extractType(declaredType, genericTypeContext));
        }
      };
    }

    @Nullable
    @Override
    public DAInterface apply(@Nullable TypeMirror o) {
      return o.accept(daInterfaceExtractor, null);
    }
  }

  @Nonnull
  private List<JavaxDAMethod> retrieveMethods(final TypeElement classElement, final JavaxExtractor javaxExtractor,
                                              final GenericTypeContext genericTypeContext) {
    List<TypeMirror> classHierarchy = processingEnv.getTypeUtils().extractClassHierarchyAsList(classElement, true);

    List<JavaxDAMethod> res = Lists.of();
    GenericTypeContext currentGenericTypeContext = genericTypeContext;
    for (TypeMirror typeMirror : classHierarchy) {
      DeclaredType classType = processingEnv.getTypeUtils().asDeclaredType(typeMirror);
      TypeElement clazz = processingEnv.getElementUtils().asTypeElement(classType.asElement());
      if (clazz.getEnclosedElements() == null) {
        continue;
      }

      currentGenericTypeContext = currentGenericTypeContext.subContext(classType);
      for (JavaxDAMethod javaxDAMethod : from(clazz.getEnclosedElements())
          // methods are ExecutableElement
          .filter(ExecutableElement.class)
          // filter out super class constructors
          .filter(
              clazz.equals(classElement) ? Predicates.<ExecutableElement>alwaysTrue() : FilterOutConstructor.INSTANCE
          )
          // transform into object of the DAModel
          .transform(new ExecutableElementToJavaxDAMethod(javaxExtractor, classElement, currentGenericTypeContext))
          .filter(notNull())) {
        res.add(javaxDAMethod);
      }
    }

    return res;
  }

  private static enum FilterOutConstructor implements Predicate<ExecutableElement> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable ExecutableElement executableElement) {
      return executableElement != null && executableElement.getKind() != ElementKind.CONSTRUCTOR;
    }
  }

  private static String uncapitalize(String str) {
    if (str == null || str.length() == 0) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str.length());
    sb.append(Character.toLowerCase(str.charAt(0)));
    sb.append(str.substring(1));
    return sb.toString();
  }

  private DAMethodImpl.Builder daMethodBuilder(ExecutableElement element) {
    if (element.getKind() == ElementKind.METHOD) {
      return DAMethodImpl.methodBuilder();
    }
    if (element.getKind() == ElementKind.CONSTRUCTOR) {
      return DAMethodImpl.constructorBuilder();
    }
    throw new IllegalArgumentException(
        String.format(
            "Kind %s of element %s is not supported to build a DAMethod from", element.getKind(),
            element
        )
    );
  }

  private class ExecutableElementToJavaxDAMethod implements Function<ExecutableElement, JavaxDAMethod> {
    private final JavaxExtractor javaxExtractor;
    private final TypeElement classElement;
    private final GenericTypeContext genericTypeContext;

    public ExecutableElementToJavaxDAMethod(JavaxExtractor javaxExtractor, TypeElement classElement,
                                            GenericTypeContext genericTypeContext) {
      this.javaxExtractor = javaxExtractor;
      this.classElement = classElement;
      this.genericTypeContext = genericTypeContext;
    }

    @Nullable
    @Override
    public JavaxDAMethod apply(@Nullable ExecutableElement methodElement) {
      if (methodElement == null) {
        return null;
      }

      DAMethodImpl.Builder builder = daMethodBuilder(methodElement);
      DAMethodImpl.Builder res = builder
          .withAnnotations(javaxExtractor.extractDAAnnotations(methodElement))
          .withModifiers(javaxExtractor.extractModifiers(methodElement))
          .withParameters(javaxExtractor.extractParameters(methodElement, genericTypeContext));
      if (methodElement.getKind() == ElementKind.CONSTRUCTOR) {
        res.withName(DANameFactory.from(uncapitalize(classElement.getSimpleName().toString())));
        res.withReturnType(javaxExtractor.extractType(classElement.asType(), genericTypeContext));
      }
      else {
        res.withName(JavaxDANameFactory.from(methodElement.getSimpleName()));
        res.withReturnType(javaxExtractor.extractReturnType(methodElement, genericTypeContext));
      }
      return new JavaxDAMethod(res.build(), methodElement);
    }
  }
}
