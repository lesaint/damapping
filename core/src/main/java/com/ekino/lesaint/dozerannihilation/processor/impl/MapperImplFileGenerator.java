package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.springframework.stereotype.Component;

import static com.google.common.collect.FluentIterable.from;

/**
* MapperImplFileGenerator -
*
* @author Sébastien Lesaint
*/
class MapperImplFileGenerator extends AbstractFileGenerator {
    private static final List<DAName> SPRING_COMPONENT_IMPORTS = ImmutableList.of(
            DANameFactory.from(Resource.class.getCanonicalName()),
            DANameFactory.from(Component.class.getCanonicalName())
    );

    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperClass().type.qualifiedName.getName() + "MapperImpl";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        // générer l'implémentation du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
        //     -> liste des méthodes mapper
        //     -> compute liste des imports à réaliser
        DAMapperClass daMapperClass = context.getMapperClass();
        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            appendHeader(bw, daMapperClass, ImmutableList.copyOf(Iterables.concat(context.getMapperImplImports(), SPRING_COMPONENT_IMPORTS)));
        }
        else {
            appendHeader(bw, daMapperClass, context.getMapperImplImports());
        }

        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            bw.append("@Component");
            bw.newLine();
        }
        bw.append("public class ").append(daMapperClass.type.simpleName).append("MapperImpl").append(" implements ").append(daMapperClass.type.simpleName).append("Mapper").append(" {");
        bw.newLine();

        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            bw.append(INDENT).append("@Resource");
            bw.newLine();
            bw.append(INDENT).append("private ").append(daMapperClass.type.simpleName).append(" instance;");
            bw.newLine();
        }
        bw.newLine();

        DAInterface guavaInterface = from(daMapperClass.interfaces).firstMatch(DAInterfacePredicates.isGuavaFunction()).get();
        DAMethod guavaMethod = from(daMapperClass.methods).firstMatch(DAMethodPredicates.isGuavaFunction()).get();

        bw.append(INDENT).append("@Override");
        bw.newLine();
        bw.append(INDENT).append("public ");
        appendType(bw, guavaMethod.returnType);
        bw.append(" ").append(guavaMethod.name).append("(");
        Iterator<DAType> typeArgsIterator = guavaInterface.type.typeArgs.iterator();
        Iterator<DAParameter> parametersIterator = guavaMethod.parameters.iterator();
        while (hasNext(typeArgsIterator, parametersIterator)) {
            appendType(bw, typeArgsIterator.next());
            bw.append(" ").append(parametersIterator.next().name);
            if (hasNext(typeArgsIterator, parametersIterator)) {
                bw.append(", ");
            }
        }
        bw.append(")").append(" {");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("return ");
        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            bw.append("instance");
        }
        else {
            bw.append(daMapperClass.type.simpleName).append("MapperFactory").append(".instance()");
        }
        bw.append(".").append(guavaMethod.name).append("(");
        parametersIterator = guavaMethod.parameters.iterator();
        while (parametersIterator.hasNext()) {
            bw.append(parametersIterator.next().name);
            if (parametersIterator.hasNext()) {
                bw.append(", ");
            }
        }
        bw.append(");");
        bw.newLine();
        bw.append(INDENT).append("}");
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }

    private boolean hasNext(Iterator<DAType> typeArgsIterator, Iterator<DAParameter> parametersIterator) {
        return typeArgsIterator.hasNext() && parametersIterator.hasNext();
    }
}
