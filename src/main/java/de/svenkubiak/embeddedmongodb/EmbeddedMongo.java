package de.svenkubiak.embeddedmongodb;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;

/**
 * @author skubiak
 *
 */
public enum EmbeddedMongo {
    DB;
    private static final String LOCALHOST = "localhost";
    private int port;
    
    private EmbeddedMongo() {
        try {
            this.port = SecureRandom.getInstance("SHA1PRNG").nextInt(50000) + 1024;

            MongodStarter.getDefaultInstance().prepare(new MongodConfigBuilder()
            .version(Version.Main.V2_6)
            .net(new Net(LOCALHOST, this.port, false))
            .build()).start();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Failed to start EmbeddedMongoDB", e);
        }
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getHost() {
        return LOCALHOST;
    }
}