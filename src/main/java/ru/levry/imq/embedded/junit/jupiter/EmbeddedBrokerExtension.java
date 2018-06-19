package ru.levry.imq.embedded.junit.jupiter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.levry.imq.embedded.EmbeddedBroker;
import ru.levry.imq.embedded.EmbeddedBrokerBuilder;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import static ru.levry.imq.embedded.support.JmsUtils.createLocalConnectionFactory;

/**
 * @author levry
 */
@Slf4j
public class EmbeddedBrokerExtension implements BeforeAllCallback, AfterAllCallback {

    private final EmbeddedBroker embeddedBroker;
    private final ConnectionFactorySupplier connectionFactorySupplier;

    private EmbeddedBrokerExtension(EmbeddedBroker embeddedBroker, ConnectionFactorySupplier connectionFactorySupplier) {
        this.embeddedBroker = embeddedBroker;
        this.connectionFactorySupplier = connectionFactorySupplier;
    }

    public static EmbeddedBrokerExtensionBuilder builder() {
        return new EmbeddedBrokerExtensionBuilder();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        log.debug("Run broker: {}", embeddedBroker);
        embeddedBroker.run();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        log.debug("Stop broker: {}", embeddedBroker);
        embeddedBroker.stop();
    }

    @SneakyThrows
    public ConnectionFactory connectionFactory() {
        return connectionFactorySupplier.get();
    }

    public static class EmbeddedBrokerExtensionBuilder {

        private final EmbeddedBrokerBuilder brokerBuilder = EmbeddedBroker.builder().homeTemp();

        public EmbeddedBrokerExtensionBuilder port(int port) {
            brokerBuilder.port(port);
            return this;
        }

        public EmbeddedBrokerExtension build() {
            final int brokerPort = brokerBuilder.getPort();
            ConnectionFactorySupplier connectionFactorySupplier = () -> createLocalConnectionFactory(brokerPort);
            return new EmbeddedBrokerExtension(brokerBuilder.build(), connectionFactorySupplier);
        }

    }

    @FunctionalInterface
    interface ConnectionFactorySupplier {
        ConnectionFactory get() throws JMSException;
    }

}
