[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb)
[![Build Status](https://secure.travis-ci.org/svenkubiak/embedded-mongodb.png?branch=master)](http://travis-ci.org/svenkubiak/embedded-mongodb)

If this software is useful to you, you can support further development by using Flattr. Thank you!

[![Flattr this repository](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=svenkubiak&url=https://github.com/svenkubiak/embedded-mongodb&title=embedded-mongodb&language=en&tags=github&category=software)


embedded-mongodb
================

Embedded MongoDB for unit testing purposes based on [flapdoodle-oss/de.flapdoodle.embed.mongo][1]

Usage
------------------

1) Add the embedded-mongodb dependency to your pom.xml:

    <dependency>
        <groupId>de.svenkubiak</groupId>
        <artifactId>embedded-mongodb</artifactId>
        <version>x.x.x</version>
        <scope>test</scope>
    </dependency>

2) Start up the embedded mongodb with the following command:

	EmbeddedMongoDB.getInstance();
	
You may want to do this in an @Before annotated class of your unit tests!
	
You can get the port and host using 

	EmbeddedMongoDB.getPort();
	EmbeddedMongoDB.getHost();

You can shut down the mongodb instance with the following command:

	EmbeddedMongoDB.shutdown();
	
EmbeddedMongoDB always runs on localhost and a random port between 28000 and 65000.

[1]: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo