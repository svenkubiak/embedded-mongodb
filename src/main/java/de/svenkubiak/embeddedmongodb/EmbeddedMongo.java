package de.svenkubiak.embeddedmongodb;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import de.flapdoodle.embed.mongo.MongodExecutable;
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
    private String host = "localhost";
    private int port;
    private MongodExecutable mongodExecutable;
    
    private EmbeddedMongo() {
        try {
            port = SecureRandom.getInstance("SHA1PRNG").nextInt(50000) + 1024;

            mongodExecutable = MongodStarter.getDefaultInstance().prepare(new MongodConfigBuilder()
            .version(Version.Main.V2_6)
            .net(new Net(this.host, port, false))
            .build());

            mongodExecutable.start();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public void shutdown() {
        this.mongodExecutable.stop();
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getHost() {
        return this.host;
    }
}