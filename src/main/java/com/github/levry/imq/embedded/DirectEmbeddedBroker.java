package com.github.levry.imq.embedded;

import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance; // NOSONAR

import javax.jms.ConnectionFactory;

import static com.github.levry.imq.embedded.support.JmsSupport.createConnectionFactory;

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
    public boolean isRunning() {
        return directBroker.isBrokerRunning();
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return createConnectionFactory(brokerPort);
    }

}
