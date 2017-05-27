# dropwizard-config-importable

[![Build Status](https://travis-ci.org/foodpanda/dropwizard-config-importable.svg?branch=master)](https://travis-ci.org/foodpanda/dropwizard-config-importable)

Use configuration imports in [Dropwizard](https://github.com/dropwizard/dropwizard) instead of copy-pasting files for each environment.

In foodpanda, we've been missing a popular Symfony2 feature which [allows to import another file](http://symfony.com/doc/current/service_container/import.html) directly from your configuration file.

This can be used to:
 - split a large configuration file into multiple smaller snippets
 - override environment-dependent values
 
## Installation

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>com.foodpanda</groupId>
        <artifactId>dropwizard-config-importable</artifactId>
        <version>${current.version}</version>
    </dependency>
</dependencies>
```

### Gradle

```groovy
compile 'com.foodpanda:dropwizard-config-importable:1.0.1'
```

### Setup
Find your `HelloApplication.java` class and in the `initialize` method add this line:

```java
bootstrap.setConfigurationFactoryFactory(new ImportableConfigurationFactoryFactory<>());
```

## Example

`base.yml`:

``` yml
database:
    driverClass: org.postgresql.Driver
    logValidationErrors: true
```

`dev.yml`:

``` yml
imports:
   - base.yml

database:
   user: postgres
```

`prod.yml`:

``` yml
imports:
   - base.yml

database:
   user: ${PROD_DB_USER}
   logValidationErrors: false
```
## TODO

 - [ ] Circular reference detection
 - [ ] Support for other than local file resource types
