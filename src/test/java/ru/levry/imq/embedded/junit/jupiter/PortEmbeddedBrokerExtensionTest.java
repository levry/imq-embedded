package ru.levry.imq.embedded.junit.jupiter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import static ru.levry.imq.embedded.support.JmsSupport.createConnectionFactory;

/**
 * @author levry
 */
@ImqBrokerTest(port = 7667)
class PortEmbeddedBrokerExtensionTest {

    @Test
    void connectToBroker() {
        ConnectionFactory connectionFactory = createConnectionFactory(7667);

        Assertions.assertThatCode(() -> {
            try (Connection connection = connectionFactory.createConnection()) {
                connection.start();
            }
        }).doesNotThrowAnyException();
    }

}