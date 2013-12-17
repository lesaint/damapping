TODO for IntelliJ IDEA DAMapping integration plugin
===================================================

Must Have features (in order)
-----------------------------
- [ ] improve fluency of using DA Mapping in case of a tree of mappers by making generated Mapper
      classes accessible without building the whole project

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


