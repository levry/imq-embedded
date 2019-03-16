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
    private boolean withDeploy;

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
        return homeDir(() -> Resources.createTempDir("imq-emb-")).deployToHome();
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
        withDeploy = true;
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
        if(!withDeploy) {
            return;
        }

        try {
            log.debug("Deploy a broker resources to path: {}", targetPath);
            File targetDir = new File(targetPath);
            Resources.copyResourcesRecursively(BROKER_HOME_RESOURCE, targetDir);
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
