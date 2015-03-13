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
 * @author svenkubiak
 *
 */
public enum EmbeddedMongo {
    DB;
    private static final int MIN_PORT = 1025;
    private static final int MAX_PORT = 50000;
    private static final String ALGORITHM = "SHA1PRNG";
    private static final String LOCALHOST = "localhost";
    private boolean threaded;
    private boolean ipv6;
    private String host;
    private int port;
    
    private EmbeddedMongo() {
        try {
            this.port = SecureRandom.getInstance(ALGORITHM).nextInt(MAX_PORT) + MIN_PORT;
            this.host = LOCALHOST;
            this.threaded = false;
            this.ipv6 = false;
        } catch (NoSuchAlgorithmException e) {
            LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to init EmbeddedMongo", e);
        }
    }
    
    public EmbeddedMongo port(int port) {
        this.port = port;
        return this;
    }
    
    public EmbeddedMongo host(String host) {
        this.host = host;
        return this;
    }
    
    public EmbeddedMongo threaded(boolean threaded) {
        this.threaded = threaded;
        return this;
    }
    
    public EmbeddedMongo ipv6(boolean ipv6) {
        this.ipv6 = ipv6;
        return this;
    }
    
    public void start() {
        if (this.threaded) {
            EmbeddedMongoRunnable runnable = new EmbeddedMongoRunnable(new Net(this.host, this.port, this.ipv6));
            Thread thread = new Thread(runnable);
            thread.start();
        } else {
            try {
                MongodStarter.getDefaultInstance().prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(this.host, this.port, this.ipv6))
                .build()).start();
                
                LoggerFactory.getLogger(EmbeddedMongo.class).info("Successfully created EmbeddedMongo @ {}:{}", LOCALHOST, this.port);
            } catch (IOException e) {
                LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to start EmbeddedMongo", e);
            }  
        }
    }
    
    public MongoClient getMongoClient() {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(this.getHost(), this.getPort());
        } catch (UnknownHostException e) {
            LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to create MongoClient for EmbeddedMongo", e);
        }
        
        return mongoClient;
    }

    public int getPort() {
        return this.port;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public boolean isThreaded() {
        return this.threaded;
    }
    
    public boolean isIPv6() {
        return this.ipv6;
    }
}