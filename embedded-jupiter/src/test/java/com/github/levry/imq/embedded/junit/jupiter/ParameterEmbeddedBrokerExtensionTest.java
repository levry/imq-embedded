package com.github.levry.imq.embedded.junit.jupiter;

import com.github.levry.imq.embedded.support.JmsHelper;
import org.junit.jupiter.api.Test;

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
        TextMessage message = sendAndBrowse("Hello, Mr.Parameter!", connectionFactory);

        assertThat(message.getText()).isEqualTo("Hello, Mr.Parameter!");
    }

    private TextMessage sendAndBrowse(String text, ConnectionFactory connectionFactory) {
        JmsHelper jms = new JmsHelper(connectionFactory);
        jms.sendText(text, "testQueue");
        return jms.browseFirst("testQueue");
    }

}