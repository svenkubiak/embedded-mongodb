[![Maven Central](https://img.shields.io/maven-central/v/de.svenkubiak/embedded-mongodb)](https://mvnrepository.com/artifact/de.svenkubiak/embedded-mongodb)
[![Coverage](https://sonar.svenkubiak.de/badges/embedded-mongodb)](https://sonar.svenkubiak.de/badges/embedded-mongodb)
![SemVer](https://img.shields.io/badge/SemVer-2.0.0-green)
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20A%20Coffee-%F0%9F%8D%BA-yellow)](https://buymeacoffee.com/svenkubiak)

embedded-mongodb
================

A small Java wrapper around [flapdoodle-oss/de.flapdoodle.embed.mongo][1] that makes it easy to run an embedded MongoDB instance for unit tests and local development. The library downloads the matching `mongod` binary for your platform, starts it as a child process, and shuts it down again when you call `stop()` or when the JVM exits.

This project is intended for **testing or development** only. The embedded server runs without authentication and should not be exposed to untrusted networks.

Requirements
------------

| Library version | Java version |
|-----------------|--------------|
| 7.x             | Java 17      |
| 8.x             | Java 21      |
| 9.x             | Java 25      |

Defaults
--------

When created without further configuration, an instance uses:

| Setting | Default value |
|---------|---------------|
| Host    | `localhost`   |
| Port    | `29019`       |
| Version | MongoDB 7.0 (`Version.Main.V7_0`) |
| IPv6    | disabled      |

The default port is deliberately not `27017` to reduce the chance of clashing with a locally installed MongoDB.

Usage
-----

### 1. Add the dependency

```xml
<dependency>
    <groupId>de.svenkubiak</groupId>
    <artifactId>embedded-mongodb</artifactId>
    <version>x.x.x</version>
</dependency>
```

### 2. Start and stop the server

The simplest way to start an embedded MongoDB with the defaults:

```java
EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().start();
```

Or in one step:

```java
EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.createAndStart();
```

Always stop the instance when you are done, for example in an `@AfterEach` method in your tests:

```java
@AfterEach
void tearDown() {
    embeddedMongoDB.stop();
}
```

If the JVM shuts down before `stop()` is called, a shutdown hook registered at instance creation will attempt to stop the process automatically.

Check whether the server is running before connecting:

```java
if (embeddedMongoDB.isActive()) {
    // connect with your MongoDB driver
}
```

### 3. Connect with the MongoDB Java driver

```java
EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().start();

String connectionString = "mongodb://" + embeddedMongoDB.getHost() + ":" + embeddedMongoDB.getPort();

try (MongoClient mongoClient = MongoClients.create(connectionString)) {
    MongoDatabase database = mongoClient.getDatabase("test");
    // run your tests against the database
}

embeddedMongoDB.stop();
```

Configuration
-------------

All configuration methods return the same instance so you can chain them:

```java
EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create()
    .withHost("localhost")
    .withPort(27017)
    .withVersion(Version.Main.V8_1)
    .enableIPv6()
    .start();
```

| Method | Description |
|--------|-------------|
| `withHost(String)` | Bind address (default: `localhost`) |
| `withPort(int)` | TCP port, must be between 1024 and 65535 (default: `29019`) |
| `withVersion(Version.Main)` | MongoDB major version to download and run (default: `V7_0`) |
| `enableIPv6()` | Enable IPv6 for the network configuration (default: off) |

Factory methods
---------------

| Method | Description |
|--------|-------------|
| `create()` | Create an instance with default settings |
| `createAndStart()` | Create an instance and start it immediately |
| `create(String host, int port)` | Create an instance with a custom host and port |

Lifecycle
---------

```
create() → configure (optional) → start() → isActive() == true
                                      ↓
                                    stop() → isActive() == false
```

Calling `start()` on an already running instance has no effect. Calling `stop()` on a stopped instance is safe. You can call `start()` again after `stop()` to restart the server with the same configuration.

[1]: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo
