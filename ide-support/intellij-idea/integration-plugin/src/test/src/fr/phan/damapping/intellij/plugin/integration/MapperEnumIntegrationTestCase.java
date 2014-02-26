package fr.phan.damapping.intellij.plugin.integration;

/**
 * MapperEnumIntegrationTestCase - Unit test for @Mapper classes in integration-test module "mapper-enum"
 *
 * @author Sébastien Lesaint
 */
public class MapperEnumIntegrationTestCase extends AbstractIntegrationTestCase {
  public MapperEnumIntegrationTestCase() {
    super("mapper-enum");
  }

  public void testEnumInstancedGuavaFunction() throws Exception {
    doTest();
  }
}
