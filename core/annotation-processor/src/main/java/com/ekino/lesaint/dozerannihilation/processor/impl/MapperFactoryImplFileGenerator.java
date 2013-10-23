package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;

import static com.google.common.collect.FluentIterable.from;

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
        DASourceClass sourceClass = context.getSourceClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(sourceClass.packageName)
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
        DASourceClass sourceClass = context.getSourceClass();
        for (DAMethod method : Iterables.filter(sourceClass.methods, DAMethodPredicates.isMapperFactoryMethod())) {
            String name = method.isConstructor() ? "instanceByConstructor" : method.name.getName();
            DAClassMethodWriter<DAClassWriter<DAFileWriter>> methodWriter = classWriter
                    .newMethod(name, context.getMapperDAType())
                    .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                    .withParams(method.parameters)
                    .start();
            DAStatementWriter<?> statementWriter = methodWriter.newStatement()
                    .start()
                    .append("return new ConstructorWithParameterMapperImpl(");
            if (method.isConstructor()) {
                statementWriter.append("new ").append(sourceClass.type.simpleName)
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

        // private final [SourceClassType] instance;
        mapperClassWriter.newProperty("instance", context.getSourceClass().type)
                .withModifier(ImmutableSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .write();

        // constructor with instance parameter
        DAParameter parameter = new DAParameter();
        parameter.name = DANameFactory.from("instance");
        parameter.type = context.getSourceClass().type;

        mapperClassWriter.newConstructor()
                .withParams(ImmutableList.of(parameter))
                .start()
                    .newStatement()
                    .start()
                    .append("this.instance = instance")
                    .end()
                .end();

        // mapper method(s)
        // implémentation de la méthode de mapping (Function.apply tant qu'on ne supporte pas @MapperMethod)
        DAMethod guavaMethod = from(context.getSourceClass().methods).firstMatch(DAMethodPredicates.isGuavaFunction()).get();
        DAClassMethodWriter<?> methodWriter = mapperClassWriter.newMethod(guavaMethod.name.getName(), guavaMethod.returnType)
                .withAnnotations(ImmutableList.<DAType>of(DATypeFactory.from(Override.class)))
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .withParams(guavaMethod.parameters)
                .start();

        // retourne le résultat de la méhode apply de l'instance de la classe @Mapper
        methodWriter.newStatement()
                .start()
                .append("return ")
                .append("instance")
                .append(".")
                .append(guavaMethod.name)
                .appendParamValues(guavaMethod.parameters)
                .end();

        methodWriter.end();

        mapperClassWriter.end();
    }

    private void appendPrivateMapperImpl(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAName simpleName = context.getSourceClass().type.simpleName;
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
