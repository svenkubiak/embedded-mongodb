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
    private boolean mongoThreaded;
    private boolean mongoIPv6;
    private String mongoHost;
    private int mongoPort;
    
    private EmbeddedMongo() {
        try {
            this.mongoPort = SecureRandom.getInstance(ALGORITHM).nextInt(MAX_PORT) + MIN_PORT;
            this.mongoHost = LOCALHOST;
            this.mongoThreaded = false;
            this.mongoIPv6 = false;
        } catch (NoSuchAlgorithmException e) {
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
    
    public EmbeddedMongo threaded(boolean threaded) {
        this.mongoThreaded = threaded;
        return this;
    }
    
    public EmbeddedMongo ipv6(boolean ipv6) {
        this.mongoIPv6 = ipv6;
        return this;
    }
    
    public void start() {
        Net net = new Net(this.mongoHost, this.mongoPort, this.mongoIPv6);
        if (this.mongoThreaded) {
            EmbeddedMongoRunnable runnable = new EmbeddedMongoRunnable(net);
            Thread thread = new Thread(runnable);
            thread.start();
        } else {
            try {
                MongodStarter.getDefaultInstance().prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(net)
                .build()).start();
                
                LoggerFactory.getLogger(EmbeddedMongo.class).info("Successfully created EmbeddedMongo @ {}:{}", LOCALHOST, this.mongoPort);
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
        return this.mongoPort;
    }
    
    public String getHost() {
        return this.mongoHost;
    }
    
    public boolean isThreaded() {
        return this.mongoThreaded;
    }
    
    public boolean isIPv6() {
        return this.mongoIPv6;
    }
}