package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;

/**
 * DAFileWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAFileWriter extends DAWriter {
    private final BufferedWriter bw;
    private DAName packageName;

    public DAFileWriter(BufferedWriter bw) {
        this.bw = bw;
    }

    public DAFileWriter appendPackage(@Nonnull DAName packageName) throws IOException {
        this.packageName = Preconditions.checkNotNull(packageName, "PackageName can not be null");
        bw.append("package ").append(packageName).append(";");
        bw.newLine();
        bw.newLine();
        return this;
    }

    public DAFileWriter appendImports(Collection<DAName> mapperImports) throws IOException {
        if (mapperImports.isEmpty()) {
            return this;
        }

        List<DAName> imports = filterAndSortImports(mapperImports, packageName);
        if (imports.isEmpty()) {
            return this;
        }

        for (DAName name : imports) {
            bw.append("import ").append(name).append(";");
            bw.newLine();
        }
        bw.newLine();
        return this;
    }

    private List<DAName> filterAndSortImports(Collection<DAName> mapperImports, @Nullable DAName packageName) {
        Predicate<DAName> notDisplayed;
        if (packageName == null) {
            notDisplayed = JavaLangDANamePredicate.INSTANCE;
        }
        else {
            notDisplayed = Predicates.or(
                    // imports in the same package as the generated class (ie. the package of the Mapper class)
                    new PackagePredicate(packageName),
                    // imports from java itself
                    JavaLangDANamePredicate.INSTANCE
            );
        }

        List<DAName> res = Lists.newArrayList(
                from(mapperImports)
                        .filter(
                                Predicates.not(
                                        notDisplayed
                                )
                        ).toSet()
        );
        Collections.sort(res);
        return res;
    }

    public DAFileWriter appendWarningComment() throws IOException {
        bw.append("// GENERATED CODE, DO NOT MODIFY, THIS WILL BE OVERRIDE");
        bw.newLine();
        return this;
    }

    public DAClassWriter startClass(String name, @Nullable Set<Modifier> modifiers, @Nullable List<DAType> annotations) throws IOException {
        return new DAClassWriter(bw, 1).withModifiers(modifiers).withAnnotations(annotations).startClass(name);
    }

    private static enum JavaLangDANamePredicate implements Predicate<DAName> {
        INSTANCE;

        @Override
        public boolean apply(@Nullable DAName qualifiedName) {
            return qualifiedName != null && qualifiedName.toString().startsWith("java.lang.");
        }
    }

    private static class PackagePredicate implements Predicate<DAName> {
        private final DAName packageName;

        public PackagePredicate(DAName packageName) {
            this.packageName = packageName;
        }

        @Override
        public boolean apply(@Nullable DAName qualifiedName) {
            if (qualifiedName == null) {
                return true;
            }
            // FIXME ce predicate répond true à tort pour un sous package de daMapperClass.packageName
            String name = qualifiedName.toString();
            return name.substring(0, name.lastIndexOf(".")).equals(packageName.toString());
        }
    }
}
