package ru.levry.imq.embedded;

import com.google.common.io.Files;
import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance; // NOSONAR
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime; // NOSONAR
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.levry.imq.embedded.support.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author levry
 */
// TODO clean home (for tempo)
// TODO test him
// TODO support for junit
// TODO public github
// TODO public to bintray
@Slf4j
public class EmbeddedBrokerBuilder {

    private static final String BROKER_HOME_RESOURCE =
            EmbeddedBrokerBuilder.class.getClassLoader().getResource("openmq").getPath();
    private static final String BROKER_INSTANCE_NAME = "imqbroker";

    private String brokerHomeDir;
    private int brokerPort = 7676;

    private boolean withDeploy;

    public EmbeddedBrokerBuilder homeDir(String path) {
        brokerHomeDir = path;
        return this;
    }

    public EmbeddedBrokerBuilder brokerPort(int brokerPort) {
        this.brokerPort = brokerPort;
        return this;
    }

    /**
     * Create temp dir and deploy embedded broker
     */
    public EmbeddedBrokerBuilder homeTemp() {
        // TODO lazy create temp dir
        File tempDir = Files.createTempDir();
        return homeDir(tempDir.getPath()).deployToHome();
    }

    @SneakyThrows
    public EmbeddedBrokerBuilder deployToHome() {
        withDeploy = true;
        return this;
    }

    @SneakyThrows
    public EmbeddedBroker build() {
        deployBrokerResourceToHome();

        ClientRuntime clientRuntime = ClientRuntime.getRuntime();

        BrokerInstance brokerInstance = clientRuntime.createBrokerInstance();

        String[] args = buildBrokerArgs();
        Properties brokerProps = buildBrokerProps(brokerInstance, args);
        brokerInstance.init(brokerProps, new EmbeddedBrokerEventListener());

        return createBroker(brokerInstance);
    }

    private void deployBrokerResourceToHome() throws IOException {
        if(!withDeploy) {
            return;
        }

        log.debug("Deploy to path: {}", brokerHomeDir);
        File homeDir = new File(brokerHomeDir);
        File imqDir = new File(BROKER_HOME_RESOURCE);
        FileUtils.copyRecursively(imqDir.toPath(), homeDir.toPath());
    }

    private String[] buildBrokerArgs() {
        return new String[]{
                "-port", Integer.toString(brokerPort),
                "-name", BROKER_INSTANCE_NAME,
                "-varhome", brokerHomeDir.concat("/var"),
                "-libhome", brokerHomeDir.concat("/lib"),
                "-imqhome", brokerHomeDir,
                "-save",
                "-silent"
        };
    }

    private Properties buildBrokerProps(BrokerInstance brokerInstance, String[] args) {
        Properties props = brokerInstance.parseArgs(args);
        props.setProperty("imq.jmx.enabled", "false");
        props.setProperty("imq.persist.file.newTxnLog.enabled", "false");
        props.setProperty("imq.cluster.enabled", "false");
        props.setProperty("imq.instanceshome", brokerHomeDir);
        props.setProperty("imq.instancename", BROKER_INSTANCE_NAME);
        return props;
    }

    private EmbeddedBroker createBroker(BrokerInstance brokerInstance) {
        return new DirectEmbeddedBroker(brokerInstance);
    }

}
