package de.svenkubiak.embeddedmongodb;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger(EmbeddedMongo.class);
    private final MongodStarter mongodStarter = MongodStarter.getDefaultInstance();
    private final String host = "localhost";
    private int port;
    private MongodExecutable mongodExecutable;
    
    private EmbeddedMongo() {
        try {
            port = SecureRandom.getInstance("SHA1PRNG").nextInt(50000) + 1024;
            
            mongodExecutable = mongodStarter.prepare(new MongodConfigBuilder()
            .version(Version.Main.V2_6)
            .net(new Net(this.host, port, false))
            .build());

            mongodExecutable.start();
            
            logger.info("Started embedded mongodb on " + this.host + " @ " + port);
        } catch (Exception e) {
            logger.error("Failed to start embedded mongodb on " + this.host + " @ " + port, e);
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