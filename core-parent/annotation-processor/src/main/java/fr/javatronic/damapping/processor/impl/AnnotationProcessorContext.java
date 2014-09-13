package fr.javatronic.damapping.processor.impl;

import fr.javatronic.damapping.util.Maps;

import java.util.Map;
import javax.lang.model.element.Element;

/**
 * AnnotationProcessorContext -
 *
 * @author Sébastien Lesaint
 */
public class AnnotationProcessorContext {
  private Map<ProcessStatus, Element> processedElements = Maps.newHashMap();

  public void add(Element element, ProcessStatus status) {
    processedElements.put(status, element);
  }
}
