TODO for IntelliJ IDEA DAMapping integration plugin
===================================================

Must Have features (in order)
-----------------------------
- [ ] improve fluency of using DA Mapping in case of a tree of mappers by making generated Mapper
      classes accessible without building the whole project
    - according to http://devnet.jetbrains.com/message/5464449#5464449, to make IDEA 'see' the generated Mapper interface
      for usage in the code, we must use the PsiAugmentProvider to which we will give a virtual Mapper interface to add
      to it's 'classPath' => see Lombok plugin
    - since we will also need the MapperImpl class for usage in the Spring contexts
    - Question : how will IDEA behave during build ? does it need Annotation processing to be enabled ?

Good to Have features (no order)
--------------------------------
* incentive on @Mapper annotation which will
    * report errors and suggest action to fix them
        * [ ] report missing guava function interface or missing @MapperFactoryMethod
            - [ ] suggest add Function interface, type could be infered if an apply method already exists, otherwise use prompt à-la incentive
                - From https://github.com/yole/comparisonChainGen/blob/master/src/org/jetbrains/comparisonChain/GenerateAction.java
                    - adding an implemented interface
                    - adding imports
                - See CompletionService in IDEA's Open API for the à-la incentive
            - [ ] suggest add @MapperFactoryMethod and display list of eligible existing methods (and only if there is at lease one eligible method)
        * [ ] missing public constructor or @Component annotation or method with @MapperFactoryMethod annotation
        * [ ] inner class can not be annoted with @Mapper
        * [ ] enum must have only one value
            - [ ] suggest to remove extra value(s) by displaying a dialog where use selects the value to keep
* incentive on @MapperFactoryMethod which will
   * report errors and suggest action to fix them
       * [ ] useless @MapperFactoryMethod on a constructor
           - [ ] suggest removing annotation
       * [ ] @MapperFactoryMethod on a non public method
           - [ ] suggest to remove annotation
           - [ ] suggest to make method public
* action on class to add @Mapper
    * this could launch a wizard, couldn't it not ?

TODOs
-----------
[X] add unit test of Psi to DAClass parsing by simply using the PsiClass in the PsiFile returned when adding a file to the fixture
  * source and test data from integration-test module can very easily be used
[X] ask on the forum how to unit test the ElementFinder with a fixture (how to trigger the call, how to setup the project)
[X] fix missing Psi parsing of generated parameters of implemented interfaces
[ ] add support for default package, currently treated (with bug) as a packageName with empty string
  [ ] make integration text for enum and for class
  [ ] fix Psi parsing also
  [ ] fix import filtering
[X] filter and transforms on Array from Psi model : use Arrays.asList() which does not create a new array but a ArrayList with the specified array as the underlying Array

