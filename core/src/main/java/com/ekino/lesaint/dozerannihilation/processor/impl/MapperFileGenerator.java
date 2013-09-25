package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

/**
* MapperFileGenerator -
*
* @author Sébastien Lesaint
*/
class MapperFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperClass().type.qualifiedName.getName() + "Mapper";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {

        // générer l'interface du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper)
        //     -> visibilite de la classe (protected ou public ?)
        //     -> liste des interfaces implémentées
        //     -> compute liste des imports à réaliser
        DAMapperClass daMapperClass = context.getMapperClass();
        appendHeader(bw, daMapperClass, context.getMapperImports());
        for (Modifier modifier : daMapperClass.modifiers) {
            bw.append(modifier.toString()).append(" ");
        }
        bw.append("interface ");
        bw.append(daMapperClass.type.simpleName).append("Mapper");
        if (!daMapperClass.interfaces.isEmpty()) {
            bw.append(" extends ");
        }
        for (DAInterface anInterface : daMapperClass.interfaces) {
            bw.append(anInterface.type.simpleName);
            Iterator<DAType> iterator = anInterface.typeArgs.iterator();
            if (iterator.hasNext()) {
                bw.append("<");
                while (iterator.hasNext()) {
                    DAType arg = iterator.next();
                    bw.append(arg.simpleName);
                    if (iterator.hasNext()) {
                        bw.append(", ");
                    }
                }
                bw.append(">");
            }
        }
        bw.append(" {");
        bw.newLine();
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }
}
