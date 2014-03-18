package fr.javatronic.damapping.intellij.plugin.integration.psiparsing;

/**
 * MapperEnumPsiParsingAndGenerateSourceTest - Psi Parsing, validation and source generation unit test for
 * integration-test module : mapper-enum
 *
 * @author Sébastien Lesaint
 */
public class MapperEnumPsiParsingAndGenerateSourceTest extends AbstractPsiParsingAndGenerateSourceTest {

  public MapperEnumPsiParsingAndGenerateSourceTest() {
    super("mapper-enum");
  }

  /**
   * @Mapper class : EnumInstancedGuavaFunction.
   */
  public void testEnumInstancedGuavaFunction() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  /**
   * @Mapper class : GenericsEverywhere.
   */
  public void testGenericsEverywhere() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  /**
   * @Mapper class : ReturnArray.
   */
  public void testReturnArray() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

}
