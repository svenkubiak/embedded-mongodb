package de.svenkubiak.embeddedmongodb;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;

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
            LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to start embedded MongoDB", e);
        }
    }
    /**
     * Creates a MongoClient connected against the embedded MongoDB
     * 
     * @return MongoClient instance
     */
    public MongoClient getMongoClient() {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(this.getHost(), this.getPort());
        } catch (UnknownHostException e) {
            LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to create MongoClient for embedded MongoDB", e);
        }
        
        return mongoClient;
    }

    /**
     * Returns the port of the embedded MongoDB
     * 
     * @return int the port number
     */
    public int getPort() {
        return this.port;
    }
    
    /**
     * Returns the host of the embedded MongoDB

     * @return String the host
     */
    public String getHost() {
        return LOCALHOST;
    }
}