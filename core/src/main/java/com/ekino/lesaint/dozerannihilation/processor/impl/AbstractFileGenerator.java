package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import javax.annotation.Nullable;
import javax.lang.model.element.Name;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

/**
* AbstractFileGenerator -
*
* @author Sébastien Lesaint
*/
abstract class AbstractFileGenerator implements FileGenerator {

    protected static final String INDENT = "    ";

    protected void appendHeader(BufferedWriter bw, DAMapperClass daMapperClass, List<Name> mapperImports) throws IOException {
        List<Name> imports = filterImports(mapperImports, daMapperClass);

        bw.append("package ").append(daMapperClass.packageName).append(";");
        bw.newLine();
        bw.newLine();
        if (!imports.isEmpty()) {
            for (Name name : imports) {
                bw.append("import ").append(name).append(";");
                bw.newLine();
            }
            bw.newLine();
        }
        bw.append("// GENERATED CODE, DO NOT MODIFY, THIS WILL BE OVERRIDE");
        bw.newLine();
    }

    protected void appendFooter(BufferedWriter bw) throws IOException {
        bw.append("}");
        bw.newLine();
    }

    private List<Name> filterImports(List<Name> mapperImports, final DAMapperClass daMapperClass) {
        return from(mapperImports)
                .filter(
                        Predicates.not(
                                Predicates.or(
                                        // imports in the same package as the generated class (ie. the package of the Mapper class)
                                        new Predicate<Name>() {
                                            @Override
                                            public boolean apply(@Nullable Name qualifiedName) {
                                                if (qualifiedName == null) {
                                                    return true;
                                                }
                                                // FIXME ce predicate répond true à tort pour un sous package de daMapperClass.packageName
                                                String name = qualifiedName.toString();
                                                return name.substring(0, name.lastIndexOf(".")).equals(daMapperClass.packageName.toString());
                                            }
                                        },
                                        // imports from java itself
                                        new Predicate<Name>() {
                                            @Override
                                            public boolean apply(@Nullable Name qualifiedName) {
                                                return qualifiedName != null && qualifiedName.toString().startsWith("java.lang.");
                                            }
                                        }
                                )
                        )
                ).toList();
    }

}
