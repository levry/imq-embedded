package ru.levry.imq.embedded.junit.jupiter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import ru.levry.imq.embedded.JmsHelper;

import javax.jms.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author levry
 */
class EmbeddedBrokerExtensionTest {

    @RegisterExtension
    static EmbeddedBrokerExtension broker = EmbeddedBrokerExtension.builder().build();

    @Test
    void registerExtension() {
        assertThat(broker).isNotNull();
    }

    @Test
    void connectionFactory() {
        ConnectionFactory connectionFactory = broker.connectionFactory();

        assertThat(connectionFactory).isNotNull();
    }

    @Test
    void sendMessage() throws Exception {
        JmsHelper jms = new JmsHelper(broker.connectionFactory());

        jms.sendText("Hello, World!!", "testQueue");

        TextMessage message = jms.browseFirst("testQueue");

        assertThat(message.getText()).isEqualTo("Hello, World!!");
    }

}