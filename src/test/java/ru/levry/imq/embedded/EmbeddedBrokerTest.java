package ru.levry.imq.embedded;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.levry.imq.embedded.support.JmsUtils.createLocalConnectionFactory;

/**
 * @author levry
 */
class EmbeddedBrokerTest {

    @Test
    void buildAndRunBroker() {
        EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().build();
        broker.run();

        String receivedText = sendAndReceive("Build broker and send message", 7676);

        assertThat(receivedText).isEqualTo("Build broker and send message");

        broker.stop();
    }

    @Test
    void buildCustomPort() {
        EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().port(17676).build();
        broker.run();

        String receivedText = sendAndReceive("Send message to localhost:17676", 17676);

        assertThat(receivedText).isEqualTo("Send message to localhost:17676");

        broker.stop();
    }

    private String sendAndReceive(String text, int hostPort) {
        JmsHelper jms = new JmsHelper(createLocalConnectionFactory(hostPort));
        jms.sendText(text, "testQueue");
        return jms.receiveText("testQueue");
    }

}
