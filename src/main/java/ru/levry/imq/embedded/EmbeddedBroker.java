package ru.levry.imq.embedded;

/**
 * @author levry
 */
public interface EmbeddedBroker {

    void run();

    void stop();

    static EmbeddedBrokerBuilder builder() {
        return new EmbeddedBrokerBuilder();
    }

}
