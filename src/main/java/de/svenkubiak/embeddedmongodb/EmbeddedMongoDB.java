package de.svenkubiak.embeddedmongodb;

import de.flapdoodle.checks.Preconditions;
import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.embed.process.io.ProcessOutput;
import de.flapdoodle.reverse.Transition;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Objects;

/**
 * Convenient class for handling an EmbeddedMongoDB instance
 * 
 * @author svenkubiak
 *
 */
public class EmbeddedMongoDB {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedMongoDB.class);
    private TransitionWalker.ReachedState<RunningMongodProcess> runningMongodProcess;
    private Version.Main version = Version.Main.V8_1;
    private String host = "localhost";
    private int port = 29019;
    private boolean active;
    private boolean ipv6;
    
    /**
     * Creates a new EmbeddedMongoDB instance with default values
     * Host: localhost
     * Port: 29019
     * 
     * @return EmbeddedMongoDB instance 
     */
    public static EmbeddedMongoDB create() {
        return new EmbeddedMongoDB();
    }
    
    /**
     * Creates and starts a new EmbeddedMongoDB instance with default values
     * Host: localhost
     * Port: 29019
     * 
     * @return EmbeddedMongoDB instance 
     */
    public static EmbeddedMongoDB createAndStart() {
        return new EmbeddedMongoDB().start();
    }
    
    /**
     * Creates a new EmbeddedMongoDB instance with a given host and port
     * 
     * @param host The host to set
     * @param port The port to set
     * @return EmbeddedMongoDB instance
     */
    public static EmbeddedMongoDB create(String host, int port) {
        return new EmbeddedMongoDB().withHost(host).withPort(port);
    }

    /**
     * Sets the port for the EmbeddedMongoDB instance 
     * <p>
     * Default is 29019
     * 
     * @param port The port to set
     * @return EmbeddedMongoDB instance 
     */
    public EmbeddedMongoDB withPort(int port) {
        Preconditions.checkArgument(port >= 1024 && port <= 65535, "Port needs to be between 1024 and 65535");

        this.port = port;
        return this;
    }

    /**
     * Sets the host for the EmbeddedMongoDB instance 
     * <p>
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
     * Enable IPv6 for host configuration
     * <p>
     * Default is false
     * 
     * @return EmbeddedMongoDB instance 
     */
    public EmbeddedMongoDB enableIPv6() {
        this.ipv6 = true;
        return this;
    }
    
    /**
     * Sets the version for the EmbeddedMongoDB instance 
     * <p>
     * Default is Version.Main.V6_0
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
     * 
     * @return EmbeddedMongoDB instance 
     */
    public EmbeddedMongoDB start() {
        if (!active && !inUse(port)) {
            Mongod mongod = new Mongod() {
                @Override
                public Transition<Net> net() {
                    return Start.to(Net.class).initializedWith(Net.of(host, port, ipv6));
                }

                @Override
                public Transition<ProcessOutput> processOutput() {
                    return Start.to(ProcessOutput.class)
                            .initializedWith(ProcessOutput.silent());
                }

                @Override
                public Transition<MongodArguments> mongodArguments() {
                    return Start.to(MongodArguments.class)
                            .initializedWith(MongodArguments.defaults().withIsQuiet(true));
                }
            };

            try {
                runningMongodProcess = mongod.start(version);
                active = true;
                Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

                LOG.info("Successfully started EmbeddedMongoDB @ {}:{}", host, port);
            } catch (Exception e) {
                LOG.error("Failed to start EmbeddedMongoDB @ {}:{}", host, port, e);
            }
        } else {
            LOG.error("Could not start EmbeddedMongoDB. Either already active or port '{}' in use", port);
        }
        
        return this;
    }

    /**
     * Stops the EmbeddedMongoDB instance
     */
    public void stop() {
        if (active) {
            runningMongodProcess.close();
            active = false;
            LOG.info("Closed EmbeddedMongoDB @ {}:{}", host, port);
        }
    }
    
    /**
     * Returns the configured host name
     *
     * @return The configured host name
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the mongod version in use
     *
     * @return The mongod version in use
     */
    public Version.Main getVersion() {
        return version;
    }

    /**
     * Returns the configured port
     *
     * @return The configured port
     */
    public int getPort() {
        return port;
    }

    /**
     * True if up and running, false otherwise
     *
     * @return True if up and running, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * True if IPv6 is enabled, false otherwise
     *
     * @return True if IPv6 is enabled, false otherwise
     */
    public boolean isIPv6() {
        return ipv6;
    }
    
    /**
     * Checks if the given host and port is already in use
     * 
     * @param port The port to check
     * @return True is port is in use, false otherwise
     */
    private boolean inUse(int port) {
        var result = false;

        try (var serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host))) { //NOSONAR
            result = serverSocket == null;
        } catch (IOException e) {
            result = true;
            LOG.warn("Port ist not available for starting EmbeddedMongoDB @ {}:{}", host, port, e);
        }

        return result;
    }
}