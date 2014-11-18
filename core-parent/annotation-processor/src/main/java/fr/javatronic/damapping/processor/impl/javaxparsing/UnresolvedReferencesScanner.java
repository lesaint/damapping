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

import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.util.Maps;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementScanner6;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
* UnresolvedReferencesScanner -
*
* @author Sébastien Lesaint
*/
class UnresolvedReferencesScanner {
  @Nonnull
  private final JavaxExtractorImpl javaxExtractor;
  @Nonnull
  private final Map<String, DAType> generatedTypesBySimpleName;

  public UnresolvedReferencesScanner(@Nonnull JavaxExtractorImpl javaxExtractor,
                                     @Nullable Collection<DAType> generatedTypes) {
    this.javaxExtractor = checkNotNull(javaxExtractor);
    this.generatedTypesBySimpleName = indexBySimpleName(generatedTypes);
  }

  private Map<String, DAType> indexBySimpleName(Collection<DAType> generatedTypes) {
    if (generatedTypes == null || generatedTypes.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, DAType> res = Maps.newHashMap();
    for (DAType generatedType : generatedTypes) {
      // FIXME there can be more than one generatedType with a specific simpleName, we should have a Set avec value instead of a single DAType
      res.put(generatedType.getSimpleName().getName(), generatedType);
    }
    return res;
  }

  public UnresolvedTypeScanResult scan(TypeElement typeElement) {
    UnresolvedTypeScanResult scanResult = new UnresolvedTypeScanResult();
    ElementVisitor<Void, UnresolvedTypeScanResult> visitor = instanceVisitor();
    typeElement.accept(visitor, scanResult);
    return scanResult;
  }

  private ElementVisitor<Void, UnresolvedTypeScanResult> instanceVisitor() {
    // TODO : switch Visitor implementation according to source level
    return new ElementScanner6<Void, UnresolvedTypeScanResult>() {
      @Override
      public Void scan(Element e, UnresolvedTypeScanResult scanResult) {
        processElement(e, scanResult);
        return null;
      }

      @Override
      public Void visitPackage(PackageElement e, UnresolvedTypeScanResult scanResult) {
        processElement(e, scanResult);
        return super.visitPackage(e, scanResult);
      }

      @Override
      public Void visitType(TypeElement e, UnresolvedTypeScanResult scanResult) {
        processElement(e, scanResult);
        return super.visitType(e, scanResult);
      }

      @Override
      public Void visitVariable(VariableElement e, UnresolvedTypeScanResult scanResult) {
        processElement(e, scanResult);
        return super.visitVariable(e, scanResult);
      }

      @Override
      public Void visitExecutable(ExecutableElement e, UnresolvedTypeScanResult scanResult) {
        processElement(e, scanResult);
        return super.visitExecutable(e, scanResult);
      }

      @Override
      public Void visitTypeParameter(TypeParameterElement e, UnresolvedTypeScanResult scanResult) {
        processElement(e, scanResult);
        return super.visitTypeParameter(e, scanResult);
      }
    };
  }

  private void processElement(Element e, UnresolvedTypeScanResult unresolvedTypeScanResult) {
    if (e.asType().getKind() == TypeKind.ERROR) {
      DAType errorDaType = javaxExtractor.extractType(e.asType());

      Optional<DAType> fixedResolution = findGeneratedType(errorDaType);
      if (fixedResolution.isPresent()) {
        // FIXME here can not just return the type from the generatedTypes map
        // current DAType may have specific typeArguments, bounds
        // we must take only the kind and qualified name from the DAType found in fixedResolution
        unresolvedTypeScanResult.addFixed(fixedResolution.get());
      }
      else {
        unresolvedTypeScanResult.getUnresolved().put(e, errorDaType);
      }
    }
  }

  @Nonnull
  private Optional<DAType> findGeneratedType(DAType unresolvedType) {
    if (generatedTypesBySimpleName.isEmpty()) {
      return Optional.absent();
    }

    String simpleName = unresolvedType.getSimpleName().getName();
    DAType soucrceClassDAType = generatedTypesBySimpleName.get(simpleName);
    if (soucrceClassDAType == null) {
      return Optional.absent();
    }
    return Optional.of(DATypeFactory.declared(soucrceClassDAType.getPackageName() + "." + unresolvedType.getSimpleName()));
  }
}
