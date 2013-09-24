package com.ekino.lesaint.dozerannihilation.processor.impl;

/**
* ImportVisitable -
*
* @author Sébastien Lesaint
*/
interface ImportVisitable {
    void visite(ImportVisitor visitor);
}
