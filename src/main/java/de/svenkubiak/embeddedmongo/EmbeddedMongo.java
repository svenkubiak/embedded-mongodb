package de.svenkubiak.embeddedmongo;

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
public class EmbeddedMongo {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedMongo.class);
    private static final MongodStarter STARTER = MongodStarter.getDefaultInstance();
    private static MongodExecutable mongodExecutable;
    private static EmbeddedMongo instance;
    public static int port = 0;
    public static String host = "127.0.0.1";
    
    private EmbeddedMongo() {
    	port = (int) (Math.random() * (65000 - 28000) + 28000);
    	try {
            mongodExecutable = STARTER.prepare(new MongodConfigBuilder()
            .version(Version.Main.V2_6)
            .net(new Net(port, false))
            .build());

            mongodExecutable.start();
        } catch (Exception e) {
            LOG.error("Failed to start in embedded mongodb", e);
        }
    }

    public static EmbeddedMongo getInstance() {
        if (instance == null) {
            instance = new EmbeddedMongo();
        }
        return instance;
    }
    
    public static void shutdown() {
    	mongodExecutable.stop();
    }
}