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
class ParameterEmbeddedBrokerExtensionTest {

    @Test
    void resolveParameter(@ImqConnection ConnectionFactory connectionFactory) {
        assertThat(connectionFactory).isNotNull();
    }

    @Test
    void sendMessage(@ImqConnection ConnectionFactory connectionFactory) throws Exception {
        JmsHelper jms = new JmsHelper(connectionFactory);

        jms.sendText("Hello, Mr.Parameter!", "testQueue");

        TextMessage message = jms.browseFirst("testQueue");

        assertThat(message.getText()).isEqualTo("Hello, Mr.Parameter!");
    }

}