package ru.levry.imq.embedded;

import com.sun.messaging.ConnectionConfiguration; // NOSONAR
import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance; // NOSONAR
import lombok.SneakyThrows;

import javax.jms.ConnectionFactory;

/**
 * @author levry
 */
class DirectEmbeddedBroker implements EmbeddedBroker {

    private final BrokerInstance directBroker;
    private final int brokerPort;

    DirectEmbeddedBroker(BrokerInstance directBroker, int brokerPort) {
        this.directBroker = directBroker;
        this.brokerPort = brokerPort;
    }

    @Override
    public void run() {
        directBroker.start();
    }

    @Override
    public void stop() {
        directBroker.stop();
        directBroker.shutdown();
    }

    @Override
    @SneakyThrows
    public ConnectionFactory connectionFactory() {
        com.sun.messaging.ConnectionFactory qcf = new com.sun.messaging.ConnectionFactory();
        qcf.setProperty(ConnectionConfiguration.imqBrokerHostName, "localhost");
        qcf.setProperty(ConnectionConfiguration.imqBrokerHostPort, Integer.toString(brokerPort));
        return qcf;
    }

}
