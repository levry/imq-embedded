package ru.levry.imq.embedded;

import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance; // NOSONAR

/**
 * @author levry
 */
class DirectEmbeddedBroker implements EmbeddedBroker {

    private final BrokerInstance directBroker;

    DirectEmbeddedBroker(BrokerInstance directBroker) {
        this.directBroker = directBroker;
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

}
