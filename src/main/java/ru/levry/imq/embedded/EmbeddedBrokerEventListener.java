package ru.levry.imq.embedded;

import com.sun.messaging.jmq.jmsservice.BrokerEvent; // NOSONAR
import com.sun.messaging.jmq.jmsservice.BrokerEventListener; // NOSONAR
import lombok.extern.slf4j.Slf4j;

/**
 * @author levry
 */
@Slf4j
class EmbeddedBrokerEventListener implements BrokerEventListener {

    @Override
    public void brokerEvent(BrokerEvent brokerEvent) {
        log.debug("Broker event: {}", brokerEvent);
    }

    @Override
    public boolean exitRequested(BrokerEvent event, Throwable throwable) {
        log.debug("Broker is requesting a shutdown because of: {} with {}", event, throwable);
        return true;
    }

}
