[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.svenkubiak/embedded-mongodb)
[![Build Status](https://secure.travis-ci.org/svenkubiak/embedded-mongodb.png?branch=master)](http://travis-ci.org/svenkubiak/embedded-mongodb)
[![Dependency Status](https://www.versioneye.com/user/projects/54dcb9b3c1bbbda0130003fc/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54dcb9b3c1bbbda0130003fc)

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

2) The most likely use case is to start the embedded MongoDB. You can do that by calling the following method

	EmbeddedMongo.DB.start();
	
In a test case you might want to do that in the constructor, so that the embedded MongoDB stays open for all tests.

3) To get a MongoClient connected against the embedded MongoDB, call the following method:

	EmbeddedMongo.DB.getMongoClient();
	
If you want create the MongoClient yourself, you have two convenient methods to get the port and the host of the embedded Mongo DB.

	EmbeddedMongo.DB.getPort();
	EmbeddedMongo.DB.getHost();


[1]: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo