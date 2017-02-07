package de.svenkubiak.embeddedmongodb;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;

/**
 * @author svenkubiak
 *
 */
public enum EmbeddedMongo {
    DB;
    private static final int MIN_PORT = 1025;
    private static final int MAX_PORT = 50000;
    private static final String ALGORITHM = "SHA1PRNG";
    private transient MongodProcess mongodProcess;
    private boolean active;
    private boolean mongoIPv6;
    private String mongoHost = "localhost";
    private int mongoPort;

    private EmbeddedMongo() {
        try {
            this.mongoPort = SecureRandom.getInstance(ALGORITHM).nextInt(MAX_PORT) + MIN_PORT;
        } catch (final NoSuchAlgorithmException e) {
            LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to init EmbeddedMongo", e);
        }
    }

    public EmbeddedMongo port(int port) {
        this.mongoPort = port;
        return this;
    }

    public EmbeddedMongo host(String host) {
        this.mongoHost = host;
        return this;
    }

    public EmbeddedMongo ipv6(boolean ipv6) {
        this.mongoIPv6 = ipv6;
        return this;
    }

    public void start() {
        if (!this.active) {
            try {
                this.mongodProcess = MongodStarter.getDefaultInstance().prepare(new MongodConfigBuilder()
                .version(Version.Main.V3_4)
                .net(new Net(this.mongoHost, this.mongoPort, this.mongoIPv6))
                .build()).start();

                LoggerFactory.getLogger(EmbeddedMongo.class).info("Successfully started EmbeddedMongoDB @ {}:{}", this.mongoHost, this.mongoPort);
                this.active = true;
            } catch (final IOException e) {
                LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to start EmbeddedMongoDB", e);
            }
        }
    }

    public void stop() {
        if (this.active) {
            this.mongodProcess.stop();
            this.active = false;

            LoggerFactory.getLogger(EmbeddedMongo.class).info("Successfully stopped EmbeddedMongoDB @ {}:{}", this.mongoHost, this.mongoPort);
        }
    }

    public MongoClient getMongoClient() {
        return new MongoClient(this.getHost(), this.getPort());
    }

    public int getPort() {
        return this.mongoPort;
    }

    public String getHost() {
        return this.mongoHost;
    }

    public boolean isIPv6() {
        return this.mongoIPv6;
    }
}