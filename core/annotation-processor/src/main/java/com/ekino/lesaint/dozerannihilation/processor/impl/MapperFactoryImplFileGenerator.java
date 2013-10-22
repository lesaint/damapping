package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * MapperFactoryImplFileGenerator - Générateur du fichier source de la classe MapperFactoryImpl générée dans le cas
 * où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
 *
 * @author Sébastien Lesaint
 */
class MapperFactoryImplFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperFactoryImplDAType().qualifiedName.getName();
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAMapperClass daMapperClass = context.getMapperClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(daMapperClass.packageName)
                .appendImports(context.getMapperFactoryImports())
                .appendWarningComment();

        DAClassWriter<DAFileWriter> classWriter = fileWriter.newClass(context.getMapperFactoryImplDAType())
                .withImplemented(ImmutableList.of(context.getMapperFactoryInterfaceDAType()))
                .start();

        appendFactoryMethods(context, classWriter);

        appendInnerClass(context, classWriter);

        classWriter.end();

        fileWriter.end();
    }

    private void appendFactoryMethods(FileGeneratorContext context, DAClassWriter<DAFileWriter> classWriter) throws IOException {
        DAMapperClass mapperClass = context.getMapperClass();
        for (DAMethod method : Iterables.filter(mapperClass.methods, DAMethodPredicates.isMapperFactoryMethod())) {
            String name = method.isConstructor() ? "instanceByConstructor" : method.name.getName();
            DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter.newMethod(name, context.getMapperClass().type)
                    .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                    .withParams(method.parameters)
                    .start();
            DAStatementWriter<?> statementWriter = methodWriter.newStatement()
                    .start()
                    .append("return new ConstructorWithParameterMapperImpl(");
            if (method.isConstructor()) {
                statementWriter.append("new ").append(mapperClass.type.simpleName)
                        .appendParamValues(method.parameters);
            }
            else {
                throw new IllegalArgumentException("MapperFactoryMethod not a constructor not supported yet");
            }
            statementWriter
                    .append(")")
                    .end();

            methodWriter.end();
        }
    }

    private void appendInnerClass(FileGeneratorContext context, DAClassWriter<DAFileWriter> factortClassWriter) throws IOException {
        DAClassWriter<DAClassWriter<DAFileWriter>> mapperClassWriter = factortClassWriter
                .newClass(context.getMapperImplDAType())
                .withModifiers(ImmutableSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL))
                .withImplemented(ImmutableList.of(context.getMapperDAType()))
                .start();

        mapperClassWriter.newProperty("instance", context.getMapperClass().type)
                .withModifier(ImmutableSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .write();

        //mapperClassWriter.newMethod(StringUtils.uncapitalize(daMapperClass.type.simpleName + "MapperImpl"), )

        mapperClassWriter.end();
    }

    private void appendPrivateMapperImpl(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAName simpleName = context.getMapperClass().type.simpleName;
        bw.append(INDENT).append("private static class ").append(simpleName).append("MapperImpl").append(" implements ").append(simpleName).append("Mapper").append(" {");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("private final ").append(simpleName).append(" instance;");
        bw.newLine();
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("public ").append(simpleName).append("MapperImpl").append("(").append(simpleName).append(" instance").append(") {");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append(INDENT).append("this.instance = instance;");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("}");
        bw.newLine();
        bw.newLine();
//        bw.append(INDENT).append(INDENT).append("public ")
        bw.append(INDENT).append("}");
    }
}
