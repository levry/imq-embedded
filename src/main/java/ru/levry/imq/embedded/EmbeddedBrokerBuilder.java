package ru.levry.imq.embedded;

import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance; // NOSONAR
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime;  // NOSONAR
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.levry.imq.embedded.support.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author levry
 */
@Slf4j
public class EmbeddedBrokerBuilder {

    private static final String BROKER_HOME_RESOURCE =
            EmbeddedBrokerBuilder.class.getClassLoader().getResource("openmq").getPath();
    private static final String BROKER_INSTANCE_NAME = "imqbroker";

    private Supplier<String> brokerHome;
    private int brokerPort = 7676;
    private boolean withDeploy;

    public EmbeddedBrokerBuilder homeDir(String path) {
        return homeDir(() -> path);
    }

    /**
     * Create temp dir and deploy embedded broker resources
     */
    public EmbeddedBrokerBuilder homeTemp() {
        return homeDir(() -> {
            try {
                Path homeDir = Files.createTempDirectory("imq-emb-");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        log.debug("Remove temporary directory: {}", homeDir);
                        FileUtils.deleteRecursively(homeDir);
                    } catch (IOException e) {
                        log.warn("Failed to delete temporary directory on exit: " + e);
                    }
                }));
                return homeDir.toString();
            } catch (IOException e) {
                throw new IllegalStateException("Cannot create temp dir for broker home", e);
            }
        }).deployToHome();
    }

    private EmbeddedBrokerBuilder homeDir(Supplier<String> home) {
        brokerHome = home;
        return this;
    }

    public EmbeddedBrokerBuilder port(int brokerPort) {
        this.brokerPort = brokerPort;
        return this;
    }

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

        String[] args = buildBrokerArgs(homeDir);
        Properties brokerProps = buildBrokerProps(brokerInstance, args, homeDir);
        brokerInstance.init(brokerProps, new EmbeddedBrokerEventListener());

        return createBroker(brokerInstance);
    }

    private void deployBrokerResources(String target) {
        if(!withDeploy) {
            return;
        }

        try {
            log.debug("Deploy a broker resources to path: {}", target);
            File deployDir = new File(target);
            File resourceDir = new File(BROKER_HOME_RESOURCE);
            FileUtils.copyRecursively(resourceDir.toPath(), deployDir.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot deploy a broker resources", e);
        }
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

    private Properties buildBrokerProps(BrokerInstance brokerInstance, String[] args, String homeDir) {
        Properties props = brokerInstance.parseArgs(args);
        props.setProperty("imq.jmx.enabled", "false");
        props.setProperty("imq.persist.file.newTxnLog.enabled", "false");
        props.setProperty("imq.cluster.enabled", "false");
        props.setProperty("imq.instanceshome", homeDir);
        props.setProperty("imq.instancename", BROKER_INSTANCE_NAME);
        return props;
    }

    private EmbeddedBroker createBroker(BrokerInstance brokerInstance) {
        return new DirectEmbeddedBroker(brokerInstance);
    }

}
