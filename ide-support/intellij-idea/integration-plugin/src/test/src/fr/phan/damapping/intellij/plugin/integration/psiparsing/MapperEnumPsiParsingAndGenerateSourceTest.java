package fr.phan.damapping.intellij.plugin.integration.psiparsing;

/**
 * MapperEnumPsiParsingAndGenerateSourceTest -
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
