embedded-mongodb
================

Embedded MongoDB for unit testing purposes based on [flapdoodle-oss/de.flapdoodle.embed.mongo][1]

Usage
------------------

1) Add the ninja-validation-module dependency to your pom.xml:

    <dependency>
        <groupId>de.svenkubiak</groupId>
        <artifactId>embedded-mongodb</artifactId>
        <version>x.x.x</version>
    </dependency>

2) Start up the embedded mongodb with the following command:

	EmbeddedMongoDB.getInstance();
	
You may want to do this in an @Before annotated class of you unit tests
	
You can get the port and host using 

	EmbeddedMongoDB.port;
	EmbeddedMongoDB.host;
	
By default EmbeddedMongoDB runs on port 127.0.0.1 and a random port between 28000 and 65000.

[1]: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo