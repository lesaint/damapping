package fr.phan.damapping.processor.impl;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
* DAParameter -
*
* @author Sébastien Lesaint
*/
class DAParameter {
    /*nom du paramètre*/
    DAName name;
    DAType type;
    Set<Modifier> modifiers;
}
