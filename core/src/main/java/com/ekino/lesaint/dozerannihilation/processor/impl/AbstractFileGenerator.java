package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

/**
* AbstractFileGenerator -
*
* @author Sébastien Lesaint
*/
abstract class AbstractFileGenerator implements FileGenerator {

    protected static final String INDENT = "    ";

    protected void appendHeader(BufferedWriter bw, DAMapperClass daMapperClass, List<DAName> mapperImports) throws IOException {
        List<DAName> imports = filterImports(mapperImports, daMapperClass);

        bw.append("package ").append(daMapperClass.packageName.toString()).append(";");
        bw.newLine();
        bw.newLine();
        if (!imports.isEmpty()) {
            for (DAName name : imports) {
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

    protected void appendType(BufferedWriter bw, DAType type) throws IOException {
        bw.append(type.simpleName);
        appendTypeArgs(bw, type.typeArgs);
        if (type.isArray()) {
            bw.append("[]");
        }
    }

    protected void appendTypeArgs(BufferedWriter bw, List<DAType> typeArgs) throws IOException {
        if (!typeArgs.isEmpty()) {
            Iterator<DAType> iterator = typeArgs.iterator();
            bw.append("<");
            while (iterator.hasNext()) {
                DAType arg = iterator.next();
                appendType(bw, arg);
                if (iterator.hasNext()) {
                    bw.append(", ");
                }
            }
            bw.append(">");
        }
    }

    private List<DAName> filterImports(List<DAName> mapperImports, final DAMapperClass daMapperClass) {
        List<DAName> res = Lists.newArrayList(
        from(mapperImports)
                .filter(
                        Predicates.not(
                                Predicates.or(
                                        // imports in the same package as the generated class (ie. the package of the Mapper class)
                                        new Predicate<DAName>() {
                                            @Override
                                            public boolean apply(@Nullable DAName qualifiedName) {
                                                if (qualifiedName == null) {
                                                    return true;
                                                }
                                                // FIXME ce predicate répond true à tort pour un sous package de daMapperClass.packageName
                                                String name = qualifiedName.toString();
                                                return name.substring(0, name.lastIndexOf(".")).equals(daMapperClass.packageName.toString());
                                            }
                                        },
                                        // imports from java itself
                                        new Predicate<DAName>() {
                                            @Override
                                            public boolean apply(@Nullable DAName qualifiedName) {
                                                return qualifiedName != null && qualifiedName.toString().startsWith("java.lang.");
                                            }
                                        }
                                )
                        )
                ).toSet()
        );
        Collections.sort(res);
        return res;
    }

}
