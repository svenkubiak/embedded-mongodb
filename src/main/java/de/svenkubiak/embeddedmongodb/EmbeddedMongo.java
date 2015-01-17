package de.svenkubiak.embeddedmongodb;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

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
    
    public MongoClient getMongoClient() {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(this.getHost(), this.getPort());
        } catch (UnknownHostException e) {
            LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to create MongoClient for embedded MongoDB", e);
        }
        
        return mongoClient;
    }
    
    public MongoClient getMongoClient(String username, String password, String dbName) {
        Preconditions.checkNotNull(username, "Username is required for embedded MongoDB with authentication");
        Preconditions.checkNotNull(password, "Password is required for embedded MongoDB with authentication");
        Preconditions.checkNotNull(dbName, "Database name is required for embedded MongoDB with authentication");
        
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(
                        new ServerAddress(this.getHost(), this.getPort()),
                        Arrays.asList(MongoCredential.createMongoCRCredential(username, dbName, password.toCharArray())));
        } catch (UnknownHostException e) {
            LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to create MongoClient with authentication for embedded MongoDB", e);
        }
        
        return mongoClient;
     }
    
    public int getPort() {
        return this.port;
    }
    
    public String getHost() {
        return LOCALHOST;
    }
}