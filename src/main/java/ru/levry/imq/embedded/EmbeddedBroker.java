package ru.levry.imq.embedded;

import javax.jms.ConnectionFactory;

/**
 * {@code EmbeddedBroker} serves as a handle to an embedded broker instance.
 *
 * <p>Usage:<pre>
 *     EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().build();
 *     broker.run();
 *     try {
 *         // Do work
 *
 *     } finally {
 *         broker.stop();
 *     }
 * </pre>
 *
 * @author levry
 */
public interface EmbeddedBroker {

    /**
     * Start the broker instance
     */
    void run();

    /**
     * Stop and shutdown the broker instance
     */
    void stop();

    /**
     * Check if broker is run
     */
    boolean isRunning();

    /**
     * Creates a ConnectionFactory that connects to the embedded broker
     */
    ConnectionFactory connectionFactory();

    static EmbeddedBrokerBuilder builder() {
        return new EmbeddedBrokerBuilder();
    }

}
