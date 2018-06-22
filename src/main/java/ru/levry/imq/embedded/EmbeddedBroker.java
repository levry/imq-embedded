package ru.levry.imq.embedded;

import javax.jms.ConnectionFactory;

/**
 * @author levry
 */
public interface EmbeddedBroker {

    void run();

    void stop();

    boolean isRunning();

    ConnectionFactory connectionFactory();

    static EmbeddedBrokerBuilder builder() {
        return new EmbeddedBrokerBuilder();
    }

}
