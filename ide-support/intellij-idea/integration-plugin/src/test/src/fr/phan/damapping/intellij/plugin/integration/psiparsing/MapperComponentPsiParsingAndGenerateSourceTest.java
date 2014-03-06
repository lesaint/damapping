package fr.phan.damapping.intellij.plugin.integration.psiparsing;

/**
 * MapperComponentPsiParsingAndGenerateSourceTest - Psi Parsing, validation and source generation unit test for
 * integration-test module : mapper-component
 *
 * @author Sébastien Lesaint
 */
public class MapperComponentPsiParsingAndGenerateSourceTest extends AbstractPsiParsingAndGenerateSourceTest {
  public MapperComponentPsiParsingAndGenerateSourceTest() {
    super("mapper-component");
  }

  public void testComponentFunction() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }
}
