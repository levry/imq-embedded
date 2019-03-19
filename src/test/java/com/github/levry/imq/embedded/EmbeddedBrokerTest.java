package com.github.levry.imq.embedded;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author levry
 */
class EmbeddedBrokerTest {

    @Test
    void buildAndRunBroker() {
        EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().build();
        broker.run();

        String receivedText = sendAndReceive("Build broker and send message", broker);

        assertThat(receivedText).isEqualTo("Build broker and send message");

        broker.stop();
    }

    @Test
    void runAndStopBroker() {
        EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().build();

        assertThat(broker.isRunning()).isEqualTo(false);

        broker.run();

        assertThat(broker.isRunning()).isEqualTo(true);

        broker.stop();

        assertThat(broker.isRunning()).isEqualTo(false);
    }

    @Test
    void buildCustomPort() {
        EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().port(7777).build();
        broker.run();

        String receivedText = sendAndReceive("Send message to localhost:7777", broker);

        assertThat(receivedText).isEqualTo("Send message to localhost:7777");

        broker.stop();
    }

    private String sendAndReceive(String text, EmbeddedBroker broker) {
        JmsHelper jms = new JmsHelper(broker);
        jms.sendText(text, "testQueue");
        return jms.receiveText("testQueue");
    }

}
