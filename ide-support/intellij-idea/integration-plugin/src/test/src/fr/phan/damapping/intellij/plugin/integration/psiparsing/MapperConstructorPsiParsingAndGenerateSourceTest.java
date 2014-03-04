package fr.phan.damapping.intellij.plugin.integration.psiparsing;

/**
 * MapperConstructorPsiParsingAndGenerateSourceTest - Psi Parsing, validation and source generation unit test for
 * integration-test module : mapper-constructor
 *
 * @author Sébastien Lesaint
 */
public class MapperConstructorPsiParsingAndGenerateSourceTest extends AbstractPsiParsingAndGenerateSourceTest {
  public MapperConstructorPsiParsingAndGenerateSourceTest() {
    super("mapper-constructor");
  }

  public void testConstructorInstancedGuavaFunction() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }
}
