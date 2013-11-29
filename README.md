DA Mapping Framework
====================

A little history and context
----------------------------
DA Mapping Framework is yet another bean mapping framework but it is entitled to handle some issues encountered with
quite a bunch of mapping frameworks I had the opportunity to either use or study.

Most mapping framework focus on the ease of writing the mapping code, most of the time by somehow generating automagically
the mapping for you. In this process, they often miss a key element of the life cycle of programming : **maintenance**.

As such, Dozer is by far the "best" example of this. Mapping beans or even hierarchy of beans by just calling the mapper
can just magically work! As your application live, developers come by and go and you end up asking just too many
times: where does this value come from? Is it ever set? Where/when is it set? Could it be set more than once?

This mapping framework got the first word of its name from this hell : **D**ozer **A**nnihilation (and as some kind of tribute to
the famous RTS game).

The spirit
----------
DA Mapping framework makes the following choices :

1. the implementation of the mapping is the only thing the developer really cares about
    * the rest is just boilerplate and we want to deal with it as little as possible
2. the developer must have the control on the implementation of the mapping
    * we don't want reflection, XML configuration, interfaces to implement to "extend" the framework, etc.
    * also, plain java mapping code means you can just ask your IDE "where is this value set" and get a definitive answer in no time
    * plan java code also means anyone can maintain it without the need to know some mapping magic
3. mapping must be strongly typed
    * think of all the work the guys have put into the compiler, that's just a shame not to use it!
3. code must be unit testable
    * we want to test the mapping not the mapping framework => we want to test the implementation
    * testable code can be mocked, composed, ... you name it!
4. usually such testable code will be wired by a DI framework
    * obviously, we need to support the most popular DI frameworks
5. writing obvious mapping code can be tedious? Then get help to generate that tedious code
    * no need to hide it in the bytecode or do the mapping at runtime
    * just have the IDE write it in your source code, so that later on, anyone can understand what's going on and where it is happening
6. "mapping occurs only from one object to another, not from any number of objects to one"
    * obviously, many use cases make it clear that such a statement isn't totally true
    * but we can still handle them with a single "source" object, other objects are just considered as part of a context

Current implementation
----------------------
This framework is pretty young and this first implementation is most likely still buggy, misses some features and has
some limitations:

1. every mapper (what a mapper is is explained below) must implement Guava's Function interface
    * Guava's `Function` interface has been chosen for a very specific reason : so that mappers can be very easily used
      when handling collections (and Java 8 adoption is well... you know...)
2. only Spring DI framework is currently supported, with limited scope at the moment
2. IDE support for DA Mapping framework is not developed yet
3. no code generation plugin has been developed yet
4. this is beta stage, so lost of bugs are most likely hiding and implementation of the framework needs refactoring

How does it work
----------------
To use DA Mapping Framework, write Foo class exposing a method creating an instance of class A from an instance of class B,
add a `@Mapper` annotation on that class and then just use the FooMapper interface in your code.

Creating a Mapper

```java
@Mapper
public class Foo implements Function<Bar, Bundy> {
    @Override
    public Bundy apply(Bar bar) {
        // some code return a Bundy instance
    }
}
```

Handcrafted (no DI) use of a Mapper

```java
public class SomeService {
    private final FooMapper fooMapper;

    public SomeService() {
        this(new FooMapperImpl());
    }

    public SomeService(FooMapper fooMapper) {
        this.fooMapper = fooMapper;
    }

    public void someMethod(Bar bar) {
        Bundy bundy = fooMapper.apply(bar);
        // ...
    }
}
```

What just happened ?
In the previous example, notice the following :

1. class `Foo` has a `@Mapper` annotation and implements Guava's `Function` interface
2. class `SomeService` has a property `fooMapper` of type `FooMapper` (which we didn't code!!)
3. class `SomeService` instantiates a `FooMapperImpl` object in its default constructor

DA Mapping framework uses annotation processing to generate the boilerplaty interface `FooMapper` and its implementation class `FooMapperImpl`.

By doing this, DA Mapping framework makes your code :

* instantly unit testable
    * write the unit test for `Foo` and you get 100% coverage
    * you can easily mock `FooMapper` while testing `SomeService`
* instantly usable with any DI framework
    * just add the injection annotation of your choice, instantiate the `FooMapperImpl` in your DI context
    * DA Mapping framework can also handle this part for even more convenience
* perfectly maintainable
    * plan java code I said!
* and most likely more performant
    * this mapping code is generated at compile-time! No runtime performance impact!

All you have to do for all of this is to write the implementation of the mapping and only that. Just the stuff you actually are about!
