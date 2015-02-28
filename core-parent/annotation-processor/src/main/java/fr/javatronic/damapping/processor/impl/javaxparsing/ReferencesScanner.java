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

import fr.javatronic.damapping.processor.impl.javaxparsing.element.ElementImports;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Maps;
import fr.javatronic.damapping.util.Optional;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner6;

import static fr.javatronic.damapping.processor.impl.javaxparsing.visitor.QualifiedNameExtractor.QUALIFIED_NAME_EXTRACTOR;

/**
* ReferencesScanner -
*
* @author Sébastien Lesaint
*/
public class ReferencesScanner {
  @Nonnull
  private final ProcessingEnvironmentWrapper processingEnvironment;
  @Nonnull
  private final Map<String, DAType> generatedTypesByQualifiedName;

  public ReferencesScanner(@Nonnull ProcessingEnvironmentWrapper processingEnvironment,
                           @Nullable Collection<DAType> generatedTypes) {
    this.processingEnvironment = processingEnvironment;
    this.generatedTypesByQualifiedName = indexByQualifiedName(generatedTypes);
  }

  private Map<String, DAType> indexByQualifiedName(Collection<DAType> generatedTypes) {
    if (generatedTypes == null || generatedTypes.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, DAType> res = Maps.newHashMap();
    for (DAType generatedType : generatedTypes) {
      if (generatedType.getQualifiedName() == null) {
        continue;
      }
      // FIXME there can be more than one generatedType with a specific simpleName, we should have a Set avec value instead of a single DAType
      res.put(generatedType.getQualifiedName().getName(), generatedType);
    }
    return res;
  }

  public ReferenceScanResult scan(TypeElement typeElement) throws IOException {
    ElementImports imports = processingEnvironment.getElementUtils().findImports(typeElement);
    ReferenceScanResult scanResult = new ReferenceScanResult(imports);
    ElementVisitor<Void, ReferenceScanResult> visitor = instanceVisitor(imports);
    typeElement.accept(visitor, scanResult);
    return scanResult;
  }

  private ElementVisitor<Void, ReferenceScanResult> instanceVisitor(@Nonnull final ElementImports imports) {
    ElementScanner6<Void, ReferenceScanResult> scanResultElementScanner = new ElementScanner6<Void, ReferenceScanResult>() {
      @Override
      public Void scan(Element e, ReferenceScanResult scanResult) {
        visitAnnotations(e, scanResult);
        e.accept(this, scanResult);
        return null;
      }

      private void visitAnnotations(Element e, ReferenceScanResult scanResult) {
        for (AnnotationMirror annotationMirror : e.getAnnotationMirrors()) {
          processingEnvironment.getTypeUtils().asElement(annotationMirror.getAnnotationType()).accept(this, scanResult);
        }
      }

      @Override
      public Void visitPackage(PackageElement e, ReferenceScanResult scanResult) {
        visitAnnotations(e, scanResult);
        processElement(e, imports, scanResult);
        return super.visitPackage(e, scanResult);
      }

      @Override
      public Void visitType(TypeElement e, ReferenceScanResult scanResult) {
        processElement(e, imports, scanResult);
        return super.visitType(e, scanResult);
      }

      @Override
      public Void visitVariable(VariableElement e, ReferenceScanResult scanResult) {
        processElement(e, imports, scanResult);
        return super.visitVariable(e, scanResult);
      }

      @Override
      public Void visitExecutable(ExecutableElement e, ReferenceScanResult scanResult) {
        processElement(e, imports, scanResult);
        for (VariableElement variableElement : e.getParameters()) {
          variableElement.accept(this, scanResult);
        }
        return super.visitExecutable(e, scanResult);
      }

      @Override
      public Void visitTypeParameter(TypeParameterElement e, ReferenceScanResult scanResult) {
        processElement(e, imports, scanResult);
        if (e.getGenericElement() != null) {
          e.getGenericElement().accept(this, scanResult);
        }
        return super.visitTypeParameter(e, scanResult);
      }

      @Override
      public Void visitUnknown(Element e, ReferenceScanResult scanResult) {
        processElement(e, imports, scanResult);
        return super.visitUnknown(e, scanResult);
      }
    };
    return scanResultElementScanner;
  }

  private void processElement(@Nonnull Element e, @Nonnull ElementImports imports, @Nonnull ReferenceScanResult referenceScanResult) {
    TypeMirror typeMirror = e.asType();
    if (typeMirror.getKind() != TypeKind.ERROR) {
      // only unresolved types are processed
      return;
    }

    Element typeElement = processingEnvironment.getTypeUtils().asElement(typeMirror);
    if (referenceScanResult.isUnresolved(typeElement)) {
      // if element is already identified as unresolved, skip trying to fix the resolution
      return;
    }

    Optional<DAType> fixedResolution = findGeneratedType(typeElement, imports);
    if (fixedResolution.isPresent()) {
      // FIXME here can not just return the type from the generatedTypes map
      // current DAType may have specific typeArguments, bounds
      // we must take only the kind and qualified name from the DAType found in fixedResolution
      referenceScanResult.addFixed(fixedResolution.get());
    }
    else {
      referenceScanResult.addUnresolved(typeElement);
    }
  }

  @Nonnull
  private Optional<DAType> findGeneratedType(Element element, ElementImports imports) {
    if (generatedTypesByQualifiedName.isEmpty()) {
      return Optional.absent();
    }

    Name qualifiedName = element.accept(QUALIFIED_NAME_EXTRACTOR, null);
    // qualified reference to Type in code
    if (qualifiedName != null && !qualifiedName.contentEquals(element.getSimpleName())) {
      return Optional.fromNullable(generatedTypesByQualifiedName.get(qualifiedName.toString()));
    }

    // implicit reference in code
    Optional<String> qualifiedNameFromImport = imports.findBySimpleName(element.getSimpleName().toString());
    if (qualifiedNameFromImport.isPresent()) {
       return Optional.fromNullable(generatedTypesByQualifiedName.get(qualifiedNameFromImport.get().toString()));
    }

    return Optional.absent();
  }
}
