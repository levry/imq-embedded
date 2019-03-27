package com.github.levry.imq.embedded;

import com.github.levry.imq.embedded.support.Resources;
import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance; // NOSONAR
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime; // NOSONAR
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.function.Supplier;


/**
 * Builder for openMQ Broker
 *
 * @author levry
 */
@Slf4j
public class EmbeddedBrokerBuilder {

    private static final URL BROKER_HOME_RESOURCE =
            EmbeddedBrokerBuilder.class.getClassLoader().getResource("openmq");

    private static final String BROKER_INSTANCE_NAME = "imqbroker";
    public static final int DEFAULT_BROKER_PORT = 7676;
    private static final String FALSE = "false";

    private Supplier<String> brokerHome;
    private int brokerPort = DEFAULT_BROKER_PORT;
    private URL homeSource;

    /**
     * The location of the base IMQ directory
     * @param path full path to base directory
     * @return a reference to this builder
     */
    public EmbeddedBrokerBuilder homeDir(String path) {
        return homeDir(() -> path);
    }

    /**
     * Create temp dir and deploy embedded broker resources
     * @return a reference to this builder
     */
    public EmbeddedBrokerBuilder homeTemp() {
        return createTempHome().deployToHome();
    }

    /**
     * Create temp dir for a base broker directory
     * @return a reference to this builder
     */
    public EmbeddedBrokerBuilder createTempHome() {
        return homeDir(() -> Resources.createTempDir("imq-emb-"));
    }

    private EmbeddedBrokerBuilder homeDir(Supplier<String> home) {
        brokerHome = home;
        return this;
    }

    /**
     * Port number of openMQ broker
     * @param brokerPort broker port
     * @return a reference to this builder
     */
    public EmbeddedBrokerBuilder port(int brokerPort) {
        this.brokerPort = brokerPort;
        return this;
    }

    /**
     * Copy a openMQ properties files to base directory
     * @return a reference to this builder
     */
    public EmbeddedBrokerBuilder deployToHome() {
        return deployFrom(BROKER_HOME_RESOURCE);
    }

    /**
     * Copy a openMQ custom home directory to base directory
     * @param homeSource url to custom home directory
     * @return a reference to this builder
     */
    public EmbeddedBrokerBuilder deployFrom(URL homeSource) {
        this.homeSource = homeSource;
        return this;
    }

    @SneakyThrows
    public EmbeddedBrokerBuilder deployFrom(File homeSource) {
        this.homeSource = homeSource.toURI().toURL();
        return this;
    }

    @SneakyThrows
    public EmbeddedBroker build() {
        String homeDir = brokerHome.get();

        deployBrokerResources(homeDir);

        ClientRuntime clientRuntime = ClientRuntime.getRuntime();

        BrokerInstance brokerInstance = clientRuntime.createBrokerInstance();

        Properties brokerProps = buildBrokerProps(brokerInstance, homeDir);
        brokerInstance.init(brokerProps, new EmbeddedBrokerEventListener());

        return createBroker(brokerInstance);
    }

    private void deployBrokerResources(String targetPath) {
        if (null == homeSource) {
            return;
        }

        try {
            log.debug("Deploy a broker resources to path: {}", targetPath);
            File targetDir = new File(targetPath);
            Resources.copyResourcesRecursively(homeSource, targetDir);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot deploy a broker resources", e);
        }
    }

    private Properties buildBrokerProps(BrokerInstance brokerInstance, String homeDir) {
        String[] args = buildBrokerArgs(homeDir);
        Properties props = brokerInstance.parseArgs(args);
        props.setProperty("imq.jmx.enabled", FALSE);
        props.setProperty("imq.persist.file.newTxnLog.enabled", FALSE);
        props.setProperty("imq.cluster.enabled", FALSE);
        props.setProperty("imq.instanceshome", homeDir);
        props.setProperty("imq.instancename", BROKER_INSTANCE_NAME);
        return props;
    }

    private String[] buildBrokerArgs(String homeDir) {
        return new String[]{
                "-port", Integer.toString(brokerPort),
                "-name", BROKER_INSTANCE_NAME,
                "-varhome", homeDir.concat("/var"),
                "-libhome", homeDir.concat("/lib"),
                "-imqhome", homeDir,
                "-save",
                "-silent"
        };
    }

    private EmbeddedBroker createBroker(BrokerInstance brokerInstance) {
        return new DirectEmbeddedBroker(brokerInstance, brokerPort);
    }

}
