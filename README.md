DA Mapping Framework
====================

A little history and context
----------------------------
DA Mapping Framework is yet another bean mapping framework but it is entitled to handle some issues encountered with
quite a bunch of mapping frameworks I had the opportunity to either use or study.

Most mapping framework focus on the ease of writing the mapping code, most of the time by somehow generating automagically
the mapping for you. In this process, they often miss a key element of the life cycle of programming : **maintainance**.

As such, Dozer is by far the best example of this. Mapping beans or even hierarchy of beans by just calling the mapper
can just magically work ! By as your application live, developers come by and go, you hand up asking just too many
times : where does this value come from ? is it ever set ? where/when is it set ? could it be set more than once ?

This mapping framework got the first word of its name from this hell : Dozer Annihilation (and as some kind of tribute to
the famous RTS game).

The spirit
----------
DA Mapping framework make the following choices :

1. the implementation of the mapping is the only thing the developer really cares about
    * the rest is just boilerplate and we want to have deal with at least as possible
2. the developer must have the control on the implementation of the mapping
    * we don't want reflection, xml configuration, interfaces to implement to "extend" the framework, etc.
    * also, plain java mapping code means you can just asked your IDE "where is this value set" and get a definitive answer in no time
3. code must be unit testable
    * we want to test the mapping not the mapping framework => we want to test the implementation
    * testable code can be mocked, composed, etc.
4. usually such testable code will be wired by a DI framework
    * obviously, we need to support DI frameworks
5. writing obvious mapping code can be tedious ? then get help to generate that tedious code
    * no need to hide it in the bytecode or do the mapping at runtime
    * just have the IDE write it in your source code, so that later on, anyone can understand what's going on and where it is happening

Current implementation
----------------------
This framework is pretty young and this first implementation is most likely still buggy, misses somes features and has
some limitations :

1. every mapper (what a mapper is is explaied belown) must implement Guava's Function interface
2. only Spring DI framework is currently supported, with limited scope for the moment
2. IDE support for DA Mapping framework is not developed yet
3. no code generation plugin has been developed yet
4. this is beta stage, so lost of bugs are most likely hiding and implementation of the framework needs refactoring

How does it work
----------------
To use DA Mapping Framework, write class Foo exposing a method creating an instance of class A from an instance of class B,
add a @Mapper annotation on that class and then just use the FooMapper interface in your code.

TOBEFINISHED