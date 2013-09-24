package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import java.util.Set;

/**
* DAParameter -
*
* @author Sébastien Lesaint
*/
class DAParameter {
    /*nom du paramètre*/
    Name name;
    DAType type;
    Set<Modifier> modifiers;
}
