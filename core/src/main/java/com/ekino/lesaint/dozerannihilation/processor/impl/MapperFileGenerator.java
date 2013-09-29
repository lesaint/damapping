package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

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
        for (Modifier modifier : filterModifiers(daMapperClass.modifiers)) {
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
                    if (arg.isArray()) {
                        bw.append("[]");
                    }
                    if (iterator.hasNext()) {
                        bw.append(", ");
                    }
                }
                bw.append(">");
            }
            if (anInterface.type.isArray()) {
                bw.append("[]");
            }
        }
        bw.append(" {");
        bw.newLine();
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }

    private static Set<Modifier> filterModifiers(Set<Modifier> modifiers) {
        return FluentIterable.from(modifiers)
                .filter(
                        Predicates.not(
                                // an interface can not be final, will not compile
                                Predicates.equalTo(Modifier.FINAL)
                        )
                )
                .toSet();
    }
}
