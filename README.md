[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb)
[![Build Status](https://secure.travis-ci.org/svenkubiak/embedded-mongodb.png?branch=master)](http://travis-ci.org/svenkubiak/embedded-mongodb)

If this software is useful to you, you can support further development by using Flattr. Thank you!

[![Flattr this repository](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=svenkubiak&url=https://github.com/svenkubiak/embedded-mongodb&title=embedded-mongodb&language=en&tags=github&category=software)


embedded-mongodb
================

Embedded MongoDB for unit testing purposes based on [flapdoodle-oss/de.flapdoodle.embed.mongo][1]

**IMPORTANT!**
Version 2.x.x uses MongoDB Java-Driver 2.x
Version 3.x.x/4.x.x uses MongoDB Java-Driver 3.x

Usage
------------------

1) Add the embedded-mongodb dependency to your pom.xml:

    <dependency>
        <groupId>de.svenkubiak</groupId>
        <artifactId>embedded-mongodb</artifactId>
        <version>x.x.x</version>
        <scope>test</scope>
    </dependency>

2) The most likely use case is to start the embedded MongoDB. You can do that by calling the following method

	EmbeddedMongo.DB.start();
	
	
Example
------------------

If you have unit tests which rely on a MongoDB, it is recommended to use @BeforeClass and @AfterClass to startup and shutdown the Embedded mongoDB.

	@BeforeClass
	public static void startup() {
		EmbeddedMongo.DB.port(28018).start();
	}

	@AfterClass
	public static void shutdown() {
		EmbeddedMongo.DB.stop();
	}
	
With the following configuration in your application.conf

	%test.ninja.mongodb.host=127.0.0.1
	%test.ninja.mongodb.port=28018
	%test.ninja.mongodb.dbname=mydb
	%test.ninja.morphia.package=models


[1]: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo
