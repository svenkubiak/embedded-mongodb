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

2) There is no specific start command. The EmbeddedMongo will start upon first instantiation.

You can get the port and host using which you e.g. can pass to your MongoClient.

	EmbeddedMongo.DB.getPort();
	EmbeddedMongo.DB.getHost();

You can shut down the mongodb instance with the following command:

	EmbeddedMongo.DB.shutdown();
	
EmbeddedMongo always runs on localhost and a random port between 1024 and 50000.

[1]: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo