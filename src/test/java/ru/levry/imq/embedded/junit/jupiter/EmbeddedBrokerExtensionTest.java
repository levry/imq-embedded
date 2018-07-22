package ru.levry.imq.embedded.junit.jupiter;

import org.junit.jupiter.api.Test;
import ru.levry.imq.embedded.JmsHelper;

import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author levry
 */
@ImqBrokerTest
class EmbeddedBrokerExtensionTest {

    @ImqConnection
    private ConnectionFactory connectionFactory;

    @Test
    void connectionFactory() {
        assertThat(connectionFactory).isNotNull();
    }

    @Test
    void sendMessage() throws Exception {
        TextMessage message = sendAndBrowse("Hello, Broker!!", connectionFactory);

        assertThat(message.getText()).isEqualTo("Hello, Broker!!");
    }

    private TextMessage sendAndBrowse(String text, ConnectionFactory connectionFactory) {
        JmsHelper jms = new JmsHelper(connectionFactory);
        jms.sendText(text, "testQueue");
        return jms.browseFirst("testQueue");
    }

}