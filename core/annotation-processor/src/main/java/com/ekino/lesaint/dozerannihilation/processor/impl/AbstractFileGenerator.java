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

}
