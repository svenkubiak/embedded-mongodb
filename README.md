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

2) The most likely use case is to get a MongoClient connected against the embedded Mongo DB. You can get a MongoClient by calling the following method:

	EmbeddedMongo.DB.getMongoClient();
	
If you want create the MongoClient yourself, you have two convenient methods to get the port and the host of the embedded Mongo DB.

	EmbeddedMongo.DB.getPort();
	EmbeddedMongo.DB.getHost();

Embedded MongoDB always runs on localhost and a random port between 1024 and 50000.

[1]: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo