package de.svenkubiak.embeddedmongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;

/**
 * 
 * @author skubiak
 *
 */
public class EmbeddedMongoDB {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedMongoDB.class);
    private static final MongodStarter STARTER = MongodStarter.getDefaultInstance();
    private static final String HOST = "localhost";
    private static MongodExecutable mongodExecutable;
    private static EmbeddedMongoDB embeddedMongoDB;
    private static int port = (int) (Math.random() * (65000 - 28000) + 28000);
    
    private EmbeddedMongoDB() {
        try {
            mongodExecutable = STARTER.prepare(new MongodConfigBuilder()
            .version(Version.Main.V2_6)
            .net(new Net(HOST, port, false))
            .build());

            mongodExecutable.start();
            
            LOG.info("Started embedded mongodb on " + HOST + " @ " + port);
        } catch (Exception e) {
            LOG.error("Failed to start embedded mongodb on " + HOST + " @ " + port, e);
        }
    }

    public static EmbeddedMongoDB getInstance() {
        if (embeddedMongoDB == null) {
            embeddedMongoDB = new EmbeddedMongoDB();
        }
        return embeddedMongoDB;
    }
    
    public static void shutdown() {
        mongodExecutable.stop();
    }
    
    public static int getPort() {
        return port;
    }
    
    public static String getHost() {
        return HOST;
    }
}