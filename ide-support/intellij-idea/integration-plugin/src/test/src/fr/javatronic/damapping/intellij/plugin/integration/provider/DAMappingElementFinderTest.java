package fr.javatronic.damapping.intellij.plugin.integration.provider;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * DAMappingElementFinderTest -
 *
 * @author Sébastien Lesaint
 */
public class DAMappingElementFinderTest {
  @Test
  public void qualifiedName_to_mapper_class_qualifiedName() throws Exception {
    String qualifiedName = "com.acme.toto.EnumTestMapper";
    String mapperInterfaceExtenstion = "Mapper";
    assertTrue(qualifiedName.endsWith(mapperInterfaceExtenstion));
    assertEquals(qualifiedName.substring(0, qualifiedName.length() - mapperInterfaceExtenstion.length()), "com.acme.toto.EnumTest");
  }
}
