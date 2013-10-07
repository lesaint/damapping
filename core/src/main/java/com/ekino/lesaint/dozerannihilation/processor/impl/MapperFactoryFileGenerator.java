package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.springframework.stereotype.Component;

/**
* MapperFactoryFileGenerator -
*
* @author Sébastien Lesaint
*/
class MapperFactoryFileGenerator extends AbstractFileGenerator {
    private static final List<DAName> SPRING_COMPONENT_EXTRA_IMPORTS = ImmutableList.of(
            DANameFactory.from(Component.class.getCanonicalName()),
            DANameFactory.from(Resource.class.getCanonicalName())
    );

    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperClass().type.qualifiedName.getName() + "MapperFactory";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAMapperClass daMapperClass = context.getMapperClass();
        List<DAName> imports = context.getMapperFactoryImports();
        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            appendHeader(bw, daMapperClass, ImmutableList.copyOf(Iterables.concat(imports, SPRING_COMPONENT_EXTRA_IMPORTS)));
        }
        else {
            appendHeader(bw, daMapperClass, imports);
        }

        // générer la factory
        //     -> nom du package
        //     -> nom de la classe (infère nom de la factory et nom du Mapper)
        //     -> type d'instantiation (si enum, le nom de la valeur d'enum à utiliser)
        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            bw.append("@Component");
            bw.newLine();
        }
        bw.append("class ").append(daMapperClass.type.simpleName).append("MapperFactory").append(" {");
        bw.newLine();
        bw.newLine();
        if (daMapperClass.instantiationType == InstantiationType.SPRING_COMPONENT) {
            bw.append(INDENT).append("@Resource");
            bw.newLine();
            bw.append(INDENT).append("private ").append(daMapperClass.type.simpleName).append(" instance").append(";");
            bw.newLine();
            bw.newLine();
        }
        bw.append(INDENT).append("public static ").append(daMapperClass.type.simpleName).append(" instance() {");
        bw.newLine();
        switch (daMapperClass.instantiationType) {
            case SINGLETON_ENUM:
                // TOIMPROVE générer le code de la factory dans le cas enum avec un nom d'enum dynamique
                bw.append(INDENT).append(INDENT).append("return ").append(daMapperClass.type.simpleName).append(".INSTANCE;");
                break;
            case CONSTRUCTOR:
                bw.append(INDENT).append(INDENT).append("return new ").append(daMapperClass.type.simpleName).append("();");
                break;
            case SPRING_COMPONENT:
                bw.append(INDENT).append(INDENT).append("return instance;");
                break;
        }
        bw.newLine();
        bw.append(INDENT).append("}");
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }
}
