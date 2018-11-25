[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb)
[![Build Status](https://secure.travis-ci.org/svenkubiak/embedded-mongodb.png?branch=master)](http://travis-ci.org/svenkubiak/embedded-mongodb)


embedded-mongodb
================

Embedded MongoDB for unit testing and developement purposes. Based on [flapdoodle-oss/de.flapdoodle.embed.mongo][1]

Requires Java 11 and is based on Mongo Java 3.x

Usage
------------------

1) Add the embedded-mongodb dependency to your pom.xml:

    <dependency>
        <groupId>de.svenkubiak</groupId>
        <artifactId>embedded-mongodb</artifactId>
        <version>x.x.x</version>
    </dependency>

2) The most likely use case is to start the embedded MongoDB. You can do that by calling the following method

	EmbeddedMongoDB.create().start();
	
This will start an in-memory MongoDB at localhost on port 29019 with the latest "production" version.

Options
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