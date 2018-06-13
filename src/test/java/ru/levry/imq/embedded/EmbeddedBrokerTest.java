package ru.levry.imq.embedded;

import com.sun.messaging.ConnectionConfiguration;
import org.junit.jupiter.api.Test;

import javax.jms.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author levry
 */
class EmbeddedBrokerTest {

    @Test
    void buildAndRunBroker() throws Exception {
        EmbeddedBroker broker = EmbeddedBroker.builder()
                .homeTemp()
                .build();

        broker.run();

        Message message = sendAndReceiveMessage("Build broker and send message", "localhost", "7676");

        assertThat(((TextMessage)message).getText()).isEqualTo("Build broker and send message");

        broker.stop();
    }

    @Test
    void buildCustomPort() throws Exception {
        EmbeddedBroker broker = EmbeddedBroker.builder()
                .homeTemp()
                .port(17676)
                .build();

        broker.run();

        Message message = sendAndReceiveMessage("Send message to localhost:17676", "localhost", "17676");

        assertThat(((TextMessage)message).getText()).isEqualTo("Send message to localhost:17676");

        broker.stop();
    }

    private Message sendAndReceiveMessage(String text, String hostName, String hostPort) throws Exception {
        com.sun.messaging.ConnectionFactory qcf = new com.sun.messaging.ConnectionFactory();
        qcf.setProperty(ConnectionConfiguration.imqBrokerHostName, hostName);
        qcf.setProperty(ConnectionConfiguration.imqBrokerHostPort, hostPort);
//        qcf.setProperty(ConnectionConfiguration.imqAddressList, "mq://localhost:7676");
//        qcf.setProperty(ConnectionConfiguration.imqAddressList, "mq://localhost/direct");

        try (Connection connection = qcf.createConnection()) {
            connection.start();
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                Queue queue = session.createQueue("exampleQueue");
                sendText(text, session, queue);
                return receiveMessage(session, queue);
            }
        }
    }

    private Message receiveMessage(Session session, Queue queue) throws JMSException {
        try (MessageConsumer consumer = session.createConsumer(queue)) {
            return consumer.receive(1000);
        }
    }

    private void sendText(String text, Session session, Queue queue) throws JMSException {
        try (MessageProducer producer = session.createProducer(queue)) {
            TextMessage textMessage = session.createTextMessage(text);
            producer.send(textMessage);
        }
    }
}