DA Mapping Framework
====================

Mapping frameworks usually compete on the ease of writing the mapping code, usually by somehow generating automagically the mapping for the developper.
It's then a matter of taste which framework's approach you prefer.

But very often, maintening that mapping code becomes difficult as the code base grows, the time goes and developers come and go.

That's because that mapping code is not regular code but it shoud be !

So DAMapping is here to provide the developper with the missing glue to make writting mapping code easy while still following the common code paradigms:
* testability:
    - use interface and implementations
    - use aggregation over inheritance
    - [SOC](http://en.wikipedia.org/wiki/Separation_of_concerns)
    - [KISS](http://en.wikipedia.org/wiki/Keep_it_simple_stupid)
* integration with Dependency Injection frameworks
* use plain Java code
    - to leverage the power of your IDE, static analysis tools, to be easy to debug and so on... out of the box
    - keep excellent performance (no reflection, no byte-code manipulation)

# How to use DAMapping

Write a class implementing the mapping from one type to another with the DAMapping `@Mapper` annotation:

```java
@Mapper
public class BarToBundy implements Function<Bar, Bundy> {
    @Override
    public Bundy apply(Bar bar) {
        // some code return a Bundy instance
    }
}
```

DAMapping generates for you a `BarToBundyMapper` interface and a `BarToBundyMapperImpl` class implementing `BarToBundyMapper`.

> Please note that `BarToBundy` implements Guava's `Function` interface.
> This is currently a requirement. But even though it is an excellent practice (more details on that below), this will become optionnal in the near futur.

Now use the `BarToBundyMapper` type wherever you need to convert a `Bar` object to new `Bundy` object, with the benefits of excellent testability:

```java
public class MyService {
    private final BarProvider barProvider;
    private final BarToBundyMapper barToBundyMapper;

    public MyService(BarProvider barProvider, BarToBundyMapper barToBundyMapper) {
        this.barProvider = barProvider;
        this.barToBundyMapper = barToBundyMapper;
    }

    public Bundy search(String barId) {
        Bar bar = barProvider.findById(barId);
        return barToBundyMapper.apply(bar);
    }
}
```

# Installation

### For Maven-based build tools

DAMapping will be soon available in Maven Central.

To use DAMapping, the DAMapping annotation-processor has to be a direct compile dependency of your project.

```xml
<dependency>
    <groupId>fr.phan.damapping</groupId>
    <artifactId>annotation-processor</artifactId>
    <version>0.2.0</version>
    <scope>compile</scope>
</dependency>
```

Details on how to setup Maven in various cases and details on how it works are available in the sample projects:
* [Maven single module project](https://github.com/lesaint/damapping-samples/tree/master/maven-project)
* [Maven multi modules project](https://github.com/lesaint/damapping-samples/tree/master/maven-multimodule-project)

### From source

This is the best option if you are using another build tools or none (who does ?!).

For other build tools, feel free to contribute installation instructions :)

* clone DAMapping Github repository
    - current version in developpement is in branch master
    - to buid a released version, checkout the tag of that version
  
     ```sh
    git clone https://github.com/lesaint/damapping.git
     ```

* build project with maven
    - you need Maven 3.0 or later installed. Download Maven from [here](http://maven.apache.org/download.cgi)
    
    ```sh
    cd damapping
    mvn clean package
    ```

* DAMapping annotation processor jar file will be available in `damapping/core-parent/annotation-processor/target/` directory

# Requirements

DAMapping supports the Java compiler for :
* Java 6 (TODO verify)
* Java 7
* Java 8 (planed)

As long as you declare the artifact of DAMapping annotation-processor as a depdendency, the annotation processor should
be automatically used by the Java compiler.

# Features

## Plain mappers

> TODO link to sample-projects

## Mapper factories
 
> TODO link to sample-projects

## Dependency Injection frameworks support

### Spring

Current version of DAMapping as limited but working support for dependency injection with Spring.

### Other frameworks

Support for other framework is planned but will required (in order) :
* some internal work to make support for Dependency Injection framework more pluggable
* requests from user (or implementation proposal) to prioritize support for one frameowrk or another

Current idea is to provide support for JSR 330 (aka CDI) dependency injection in order to leverage support for compliant
frameworks.

# IDE support

## Eclipse
There shouldn't be any configuration required to use DAMapping with Eclipse.

## IntelliJ IDEA

Using DAMapping with Intellij IDEA requires the use of a plugin.

Current version of the plugin focuses of making IDEA aware of the generated classes and interface so that using Mapper and MapperFactory in your code doesn't result in compilation warnings.

### Installation instructions

DAMapping plugin will be soon available in IntelliJ IDEA's plugin repository.

TODO add link to plugin installation help page of Jetbrains

### Supported versions

DAMapping plugin has been tested with :
* IntelliJ IDEA 12.06

### Planned features

> TODO

# Three steps to better mapping code

## Step 1 : Dozer and the like

DAMapping framework has been created from several years of experience with Dozer usage in a large application.

This application beeing correctly designed with separated software layers, bean mapping occured quite often and Dozer was used to speed up mapping code writing.

But this code was pretty hard to maintain due to the opacity of Dozer mapping operations.

I found myself wasting long hours debuging, looking for the place some specific property was set (or not), struggling because that property was set in multiple places, hard to find obviously, investigating existing code written some time ago, ...

## Step 2 : Guava's Function

At some point, we replaced this Dozer code with code based on Guava's `Function` interface.
It was very convenient when dealing with collections and also mapping code was now written in plain Java. Find out if a property was set (or not) and where would only two keustrokes in IntelliJ IDEA.

Also, the `apply` method kind of created a convention on how to write mapper which tended to make the code much more readable.

But developers quickly abused of the Enum instance pattern to instanciate mapper objects:

```java
public enum FooBarMapper implements Function<Foo, Bar> {
    INSTANCE;

    public Bar apply(Foo foo) {
        // implement me
    }

}
```

This pattern is very convenient because it solves the instanciation problem (only one per JVM) but it creates static code.
Static code crashes the testability of your code, especially when mapping tree of beans where mappers use mappers that use mappers, ...

## Step 3 : interface extends Guava's Function + implementation

To solve the testability problem, the solution is to separate the interface from the implementation : create a `FooBarMapper` interface that extends Guava's `Function` and implement it in `FooBarMapperImpl`.

Used in conjonction with a Dependency Injection framework, we would get maximum testability of each Mapper and very easily wire them with each other and the rest of the application.

But that's a lots of extra code. Creating the interface, creating the implementation, it's time consuming and boring. Not so good either.

## The final step : DAMapping

Introducing DAMapping !

The goal of DAMapping is to make step 3 seamless.

DAMapping takes care of generation the boiler plate code for you and you keep your application clean.

## Where does the name come from ?

Initially, this framework was named after this Dozer hell experience : **D**ozer **A**nnihilation (and as some kind of tribute to the famous RTS game).
But, as good a prototype codename as it was, I renamed it to **DA**Mapping.
