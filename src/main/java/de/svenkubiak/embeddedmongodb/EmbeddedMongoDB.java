package de.svenkubiak.embeddedmongodb;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.Defaults;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.RuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;

/**
 * 
 * @author svenkubiak
 *
 */
public class EmbeddedMongoDB {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedMongoDB.class);
    private MongodProcess mongodProcess;
    private String host = "localhost";
    private Version.Main version = Version.Main.PRODUCTION;
    private int port = 29019;
    private boolean active;
    
    /**
     * Creates a new EmbeddedMongoDB instance 
     * 
     * @return EmbeddedMongoDB instance 
     */
    public static EmbeddedMongoDB create() {
        return new EmbeddedMongoDB();
    }

    /**
     * Sets the port for the EmbeddedMongoDB instance 
     * 
     * Default is 29019
     * 
     * @param port The port to set
     * @return EmbeddedMongoDB instance 
     */
    public EmbeddedMongoDB withPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the host for the EmbeddedMongoDB instance 
     * 
     * Default is localhost
     * 
     * @param host The host to set
     * @return EmbeddedMongoDB instance 
     */
    public EmbeddedMongoDB withHost(String host) {
        Objects.requireNonNull(host, "host can not be null");
        
        this.host = host;
        return this;
    }
    
    /**
     * Sets the version for the EmbeddedMongoDB instance 
     * 
     * Default is Version.Main.PRODUCTION
     * 
     * @param version The version to set
     * @return EmbeddedMongoDB instance 
     */
    public EmbeddedMongoDB withVersion(Version.Main version) {
        Objects.requireNonNull(version, "version can not be null");
        
        this.version = version;
        return this;
    }

    /**
     * Starts the EmbeddedMongoDB instance
     * @return EmbeddedMongoDB instance 
     */
    public EmbeddedMongoDB start() {
        if (!this.active) {
            try {
                Command command = Command.MongoD;
                RuntimeConfig runtimeConfig = Defaults.runtimeConfigFor(command)
                        .processOutput(ProcessOutput.getDefaultInstanceSilent())
                        .build();
                
                this.mongodProcess = MongodStarter.getInstance(runtimeConfig).prepare(MongodConfig.builder()
                        .version(this.version)
                        .net(new Net(this.host, this.port, false))
                        .build())
                        .start();

                this.active = true;
                LOG.info("Successfully started EmbeddedMongoDB @ {}:{}", this.host, this.port);
            } catch (final IOException e) {
                LOG.error("Failed to start EmbeddedMongoDB @ {}:{}", this.host, this.port, e);
            }
        }
        
        return this;
    }
    
    /**
     * Stops the EmbeddedMongoDB instance
     */
    public void stop() {
        if (this.active) {
            this.mongodProcess.stop();
            this.active = false;

            LOG.info("Successfully stopped EmbeddedMongoDB @ {}:{}", this.host, this.port);
        }
    }

    public String getHost() {
        return host;
    }

    public Version.Main getVersion() {
        return version;
    }

    public int getPort() {
        return port;
    }

    public boolean isActive() {
        return active;
    }
}