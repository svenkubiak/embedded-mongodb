package de.svenkubiak.embeddedmongodb;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;

public class EmbeddedMongoRunnable implements Runnable {
    private MongodProcess mongodProcess;
    private boolean running = true;
    private Net net;
    
    public EmbeddedMongoRunnable (Net net) {
        this.net = net;
    }
    
    @Override
    public void run() {
        try {
            this.mongodProcess = MongodStarter.getDefaultInstance().prepare(new MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(net)
            .build()).start();
            
            LoggerFactory.getLogger(EmbeddedMongo.class).info("Successfully created EmbeddedMongo @ {}:{}", net.getBindIp(), net.getPort());
        } catch (IOException e) {
            LoggerFactory.getLogger(EmbeddedMongo.class).error("Failed to start EmbeddedMongo", e);
        } 
        
        while(running) {
            //intentionally left blank
        }
    }
    
    public void shutdown() {
        this.mongodProcess.stop();
        this.running = false;
    }
}