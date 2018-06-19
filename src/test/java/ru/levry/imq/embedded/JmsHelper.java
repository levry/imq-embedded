package ru.levry.imq.embedded;

import lombok.SneakyThrows;

import javax.jms.*;
import javax.jms.IllegalStateException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author levry
 */
public class JmsHelper {

    private final ConnectionFactory connectionFactory;
    private long defaultTimeout = SECONDS.toMillis(5L);

    public JmsHelper(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void sendText(String text, String queueName) {
        withSession(session -> {
            Queue queue = session.createQueue(queueName);
            try (MessageProducer producer = session.createProducer(queue)) {
                TextMessage textMessage = session.createTextMessage(text);
                producer.send(textMessage);
            }
            return null;
        });
    }

    public String receiveText(String queueName) {
        return withSession(session -> receiveText(session, session.createQueue(queueName), defaultTimeout));
    }

    private String receiveText(Session session, Queue queue, long timeout) throws JMSException {
        Message message = receiveMessage(queue, session, timeout);
        if (!(message instanceof TextMessage)) {
            throw new IllegalStateException("Illegal message type received");
        }
        return ((TextMessage) message).getText();
    }

    private Message receiveMessage(Queue queue, Session session, long timeout) throws JMSException {
        try (MessageConsumer consumer = session.createConsumer(queue)) {
            return consumer.receive(timeout);
        }
    }

    public <T extends Message> T browseFirst(String queueName) {
        return withSession(session -> {
            Queue queue = session.createQueue(queueName);
            try (QueueBrowser browser = session.createBrowser(queue)) {
                return (T) browser.getEnumeration().nextElement();
            }
        });
    }

    @FunctionalInterface
    public interface InSessionCallable<T> {
        T execute(Session session) throws JMSException;
    }

    @SneakyThrows
    private <T> T withSession(InSessionCallable<T> callable) {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                return callable.execute(session);
            }
        }
    }

}