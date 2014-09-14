[![Build Status](https://travis-ci.org/lesaint/damapping.svg?branch=master)](https://travis-ci.org/lesaint/damapping)

DAMapping Framework
====================


DAMapping is a mapping framework that takes a deeply different approach to bean mapping from existing frameworks.

Here is the DAMapping mapping manifesto:

1. mapping code should be part of the application as any other piece of code
2. when mapping code is trivial, it should be generated as well as possible

DAMapping is implemented in three complementary components:

1. a mapping coding paradigm that provides a very light definition of how mapping code should be written and that makes sure it is easy to integrate with the rest of the application
2. an annotation processor that saves the developer from writting some repetitve and low-value integration code and boost up integration with [DI](http://en.wikipedia.org/wiki/Dependency_injection) frameworks
3. IDE plugin(s) to help generate the initial mapping code and saves the developer from trivial mapping coding

To find out more about how and why we got to the DAMapping manifesto and get more details on how to use DAMapping, see the [why and how presentation](http://www.javatronic.fr/damapping/presentations/damapping_why_and_how.html).

## How to use

Write a class dedicated to implementing the mapping from one type to another. Add the DAMapping `@Mapper` annotation to it:

```java
@Mapper
public class BarToBundy {
    @Override
    public Bundy transform(Bar bar) {
        // some code returning a Bundy instance
    }
}
```

DAMapping generates for you a `BarToBundyMapper` interface and a `BarToBundyMapperImpl` class implementing `BarToBundyMapper`.

Now use the `BarToBundyMapper` type wherever you need to convert a `Bar` object to new `Bundy` object, especially in
another class annotated with ```@Mapper```, with the benefits of excellent testability.

Note that Guavas's ```Function``` interface is natively supported.

```java
@Mapper
public class FooToAcme implements Function<Foo, Acme> {
    private final BarToBundyMapper barToBundyMapper;

    public FooToAcme(BarToBundyMapper barToBundyMapper) {
        this.barToBundyMapper = barToBundyMapper;
    }

    @Nullable
    public Acme apply(@Nullable Foo input) {
         // implement mapping code using barToBundyMapper somehow here
    }
}
```

### Resources

To find out more on how to use DAMappping and why, see the [why and how presentation](http://www.javatronic.fr/damapping/presentations/damapping_why_and_how.html).

You can also check the sample Maven project [here](https://github.com/lesaint/damapping-samples/tree/master/maven-project).

## Installation

### For Maven-based build tools

To use DAMapping, the DAMapping annotation-processor has to be a default/compile dependency of your project.

```xml
<dependency>
    <groupId>fr.phan.damapping</groupId>
    <artifactId>damapping-annotation-processor</artifactId>
    <version>0.3.1</version>
    <!-- scope does not need to be explicitly specified, default scope works just fine -->
    <scope>compile</scope>
</dependency>
```

An extensive list of dependency declarations is available on this [page](http://search.maven.org/#artifactdetails|fr.javatronic.damapping|annotation-processor|0.3.0|jar).

### Other tools or hand coding

To use DAMapping Annotation Processor, the only requirements are:

* to use a Java compiler that supports Annotation Processors declared with the service provider-configuration file ```META-INF/services/javax.annotation.processing.Processor```
* to have the DAMapping Annotation Processor jar on the classpath 

## Requirements

DAMapping supports the Java compiler for :
* Java 6 (TODO verify)
* Java 7
* Java 8 (planed)

## Contact and support

Feel free to create an [issue](https://github.com/lesaint/damapping/issues) even to ask questions and/or contact me on [Twitter](https://twitter.com/LesaintSeb).

## Documentation

Documentation is currently a work in progress and is beeing created on the GitHub project's [Wiki](https://github.com/lesaint/damapping/wiki).
