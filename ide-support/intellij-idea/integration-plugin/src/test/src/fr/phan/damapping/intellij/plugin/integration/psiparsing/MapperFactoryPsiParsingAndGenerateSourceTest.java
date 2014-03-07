package fr.phan.damapping.intellij.plugin.integration.psiparsing;

/**
 * MapperFactoryPsiParsingAndGenerateSourceTest - Psi Parsing, validation and source generation unit test for
 * integration-test module : mapper-fatory
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryPsiParsingAndGenerateSourceTest extends AbstractPsiParsingAndGenerateSourceTest {
  public MapperFactoryPsiParsingAndGenerateSourceTest() {
    super("mapper-factory");
  }

  public void testConstructorWithParameter() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  public void testMultipleImplementationAsEnum() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  public void testMultiConstructorOnBusinessTypes() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }
}
