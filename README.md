[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb/badge.svg)](https://mvnrepository.com/artifact/de.svenkubiak/embedded-mongodb)


embedded-mongodb
================

Embedded MongoDB for unit testing and developement purposes. Based on [flapdoodle-oss/de.flapdoodle.embed.mongo][1]

7.x requires Java 17.
8.x requires Java 21.

Usage
------------------

1) Add the embedded-mongodb dependency to your pom.xml:
```
	<dependency>
		<groupId>de.svenkubiak</groupId>
		<artifactId>embedded-mongodb</artifactId>
		<version>x.x.x</version>
	</dependency>
```
2) Start the embedded MongoDB by calling the following method
```
	EmbeddedMongoDB.create().start();
```	
This will start an in-memory MongoDB with default options at localhost on port 29019 with the latest "production" version of Mongo DB.

Additional options
------------------

    EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create()
        .withHost("localhost")
        .withPort(27017)
        .withVersion(Main.V3_6)
        .start();
        
    if (embeddedMongoDB.isActive()) {
    	//do something
    }


[1]: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo
