 - TODO or not TODO: InstantiationType.SPRING_COMPONENT ?
        Ça hardcode une dépendance avec Spring.
        Ce serait sans doute mieux dans un module à part (qui s'occuperait aussi de générer SpringMapperContext.java).
        corrolaire : l'utilisation d'une enum pour indiquer la manière d'instancier le mapper n'est pas extensible
                     (ie. ajout d'un support Guice, JEE, ...) utiliser plutôt une interface ?
        meilleure idée
           => supprimer le paramètre de @Mapper
           => déduire le cas enum si c'est une enum qui est annotée
               le nom de la valeur de l'enum n'a pas d'importance, on valide juste à la compilation qu'il n'y en a qu'une
           => déduire le cas constructeur que c'est une enum qui est annotée
           => une API permet d'ajouter une annotation en plus de @Mapper qui fera que l'on créera une classe avec un @Component
              et une autre implémentation de factory
 - TODO : ajouter un paramètre à @Mapper pour préciser la visibilité de la classe Mapper générée (public par défaut, sinon protected)
           -> on n'ajoute pas de paramètre, la visibilité de l'interface générée sera la même que celle de la classe annotée avec @Mapper
 - framework d'injection super léger : essayer Dagger
 - pour étendre DAMapping de façon pluggable, utiliser la méthode de Dozer: utiliser le SPI discovery
    - cf. http://thecodersbreakfast.net/index.php?post/2008/12/26/Java-%3A-pr%C3%A9sentation-du-Service-Provider-API
    - cf. javadoc JavacAnnotationHandler in lombok
    - cf. HandlerLibrary in lombok pour la découverte (méthode static)


ROADMAP
[X] intégration avec Spring, générer des classes annotées @Component pour les classes @Mapper elles-mêmes annotées avec @Mapper
[X] écriture d'un cas d'intégration avec un Spring contexte
[X] auto discovery de l'annotation processor
[X] séparation en modules spécifiques des annotations et du processor
[X] toute exception doit indiquer la classe @Mapper pour laquelle ça a pété
[X] supprimer usage de FluentIterable.toImmutableList() et toImmutableSet() ou de toList() et toSet()
[ ] supprimer le support des méthodes protected et package protected comme méthodes de mapper
   (simplification du framework et de toute façon, on génère une interface donc méthode implicitement public)
   (le support de ces méthodes non public est une possibilité liée au fait que l'on génère nos classes dans le même
    package mais ce n'est pas une fonctionnalité)
[ ] rewrite Javax parsing using Visitors instead of instanceof and chained getters
[ ] ProcessingContext should wrap ProcessingEnvironment + expose methods of JavaxParsing
[ ] use qualified name when writting annotations added by DAMapping to avoid having to modify the imports ?
[ ] when compiling a @Mapper extending Function and Guava's is not in path, MapperImpl is generated with a "import Function;"
    statement and file does not compile
[X] les annotations sur méthodes Fuction.apply, @MapperMethod et @MapperFactoryMethod sont perdues dans les codes généré
    (exemple: manque le @Nullable hérité de Function.apply dans toutes les classes MapperImpl sur le paramètre et le type de retour)
[ ] optimiser les classes annonymes (Function, Predicate, ...)
[ ] mettre en place un système d'exception internes pour ne pas faire des getMessager().[...] un peu partout, afficher
  ces exceptions avec un getMessager() mais ne pas remonter à javac
[ ] disposer d'une liste de String pour y coller les simples warnings pour ne pas faire de getMessager() un peu partout,
  afficher ces warnings avant de traiter l'exception interne
[X] tester MapperFactory avec des Objets non java.lang, des génériques, ...
[X] test U pour les méthodes appendType, appendParams, ... de DAStatementWriter ?
[ ] ajouter un contrôle : les MapperFactoryMethods doivent retourner le type de l'objet annotée avec @Mapper
[ ] ajouter une méthode appendReturn à DAStatementWriter ?
[ ] ajouter un test U pour DAType.superBound
[ ] ajout support de génériques avec bounds multiples : <T extends B1 & B2 & B3>
[X] remplacer la propriété DAMethod.mapperFactoryMethod par l'ajout de la liste des annotations sur la méhode de sorte
  que la logique de calcul de cette propriété ne soit pas dupliquée dans le parsing Psi ou Javax
[X] remplacer la propriété instantiationType dans DASourceClass en ajoutant les annotations au modèles
[ ] test U pour DAClassWriter.newClass
[X] reduire duplication dans le calcul des imports (ImportVisitable implemenations)
[ ] déplacer DAFileWriter#PackagePredicate dans une classe DAName predicate et ajouter des tests U
  (en particulier pour le package par défaut, ie. pas de package)
[ ] factoriser la génération des méthodes @MapperMethod (pour l'instant juste apply de Guava.Function) entre
  MapperImplFileGenerator et MapperFactoryImplFileGenerator ?
[X] ajouter les @Override manquant dans les classes générées Mapper*Impl
[ ] traiter les TODO et FIXME qui trainent dans les sources
[X] supprimer les propriétés publiques dans les beans utilisés dans la genération de fichiers sources
[ ] ajouter test U pour les méthodes de DAMethod
[ ] ajouter test U pour DAMethodPredicates
[X] passe de refacto : sortir code de lecture de API javac des annotation processors, predicates singleton si applicable, ajouter des tests U
[ ] étudier l'intégration avec l'IDE
[ ] visibilité du mapper généré
[ ] améliorer le support de l'injection de dépendance
    [ ] utiliser injection par constructeur au lieu de l'injection par propriété dans les classes générées
    [ ] mettre en place un mécanisme de plugin ? utiliser un plugin pour avoir un module core qui dépend de spring et offre plus d'options
    [ ] donner le choix de l'annotation Spring à mettre sur les classes générées (@Component ou toute annotation qui l'étends)
    [ ] créer une class @Configuration pour tous les mapper d'un package pour éviter de faire un package-scan ?
    [ ] faire un plugin compatible avec la JSR de Dependency Injection de Java (javax.annotation.Resource & co ?)
    [ ] ajouter une annotation pour définir ou surcharger le comportement par défaut qui ajoute des @Component
[X] ajouter une annotation @MapperMethod pour ne pas être dépendant de guava
[X] removing model dependency on Javax.lang
    [X] DASourceClass#classElement
        [X] add enum flag to DASourceClass
        [X] add enum value(s) to DASourceClass
    [X] Modifier enum (DASourceClass, DAParameter, DAMethod)
    [X] remove ElementKind and TypeKind dependency
[ ] fix indent of pom.xml files


==============================


### How to use DAMapping with Spring

To use DAMapping with Spring, simply add Spring's `@Component` annotation on the class annoted with `@Mapper`.
DAMapping generated classes will be automatically annoted with `@Component` and will declare the instance of
your class `@Mapper` to be injected via property-injection using the `@Resource` annotation.

Since, generated classes belong the same package as the class annotated with `@Mapper, adding a package-scan on that
package will make all generated classes available in the Spring context.

TODO : add code sample to make description easier to follow


Spring support :

Spring is currently supported via the `@Component` annotation parsing on classes annoted with `@Mapper`.

Example :

```java
package fr.phan.dammaping.demo;

[... imports ...]

@Component
@Mapper
public class Foo implements Function<Bar, Bundy> {
    @Override
    public Bundy apply(Bar bar) {
        // some code return a Bundy instance
    }
}
```

DAMapping generated `FooMapper` interface and `FooMapperImpl` class will be created in the same package as the `Foo`
class as usual but the `FooMapperImpl` class will be annoted with `@Component` and will retrieve the `Foo` instance
by injection.

Therefor, the most convenient way to use DAMapping with Spring is to add a component-scan on the package where the
`@Mapper` class(es) and the generated classes and interfaces are.

Sample XML configuration based Spring Application context declaration :

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.2.xsd">

  <!-- scanning package fr.phan.dammaping.demo for @Component beans -->
  <context:component-scan base-package="fr.phan.dammaping.demo"/>

</beans>
```

=================================



