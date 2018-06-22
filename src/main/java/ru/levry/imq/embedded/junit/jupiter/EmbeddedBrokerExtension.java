package ru.levry.imq.embedded.junit.jupiter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import ru.levry.imq.embedded.EmbeddedBroker;

import javax.jms.ConnectionFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;

/**
 * @author levry
 */
@Slf4j
public class EmbeddedBrokerExtension implements BeforeAllCallback,
        TestInstancePostProcessor, ParameterResolver {

    private static final Namespace NAMESPACE = Namespace.create(EmbeddedBrokerExtension.class);
    private static final String KEY = "embeddedBroker";

    @Override
    public void beforeAll(ExtensionContext context) {
        EmbeddedBroker embeddedBroker = getOrCreateBroker(context);

        if(!embeddedBroker.isRunning()) {
            log.debug("Run broker: {}", embeddedBroker);
            embeddedBroker.run();
        }
    }

    private static EmbeddedBroker getOrCreateBroker(ExtensionContext context) {
        Store store = getStore(context);
        BrokerResource resource = store.getOrComputeIfAbsent(KEY, key -> new BrokerResource(createBroker()), BrokerResource.class);
        return resource.getBroker();
    }

    private static EmbeddedBroker createBroker() {
        return EmbeddedBroker.builder().homeTemp().build();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        Predicate<Field> isConnection = field -> field.getType().isAssignableFrom(ConnectionFactory.class);

        List<Field> fields = findAnnotatedFields(testInstance.getClass(), ImqConnection.class, isConnection);
        withConnectionFactory(context).ifPresent(connectionFactory ->
            fields.forEach(field ->
                setupField(field, testInstance, connectionFactory)
            )
        );
    }

    @SneakyThrows
    private void setupField(Field field, Object testInstance, ConnectionFactory connectionFactory) {
        field.setAccessible(true);
        field.set(testInstance, connectionFactory);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Parameter parameter = parameterContext.getParameter();
        return parameter.isAnnotationPresent(ImqConnection.class)
                && parameter.getType().isAssignableFrom(ConnectionFactory.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return withConnectionFactory(extensionContext)
                .orElseThrow(() -> new IllegalStateException("Not resolve connectionFactory"));
    }

    private Optional<ConnectionFactory> withConnectionFactory(ExtensionContext context) {
        return withBroker(context).map(EmbeddedBroker::connectionFactory);
    }

    private static Optional<EmbeddedBroker> withBroker(ExtensionContext context) {
        Store store = getStore(context);
        return Optional.ofNullable(store.get(KEY, BrokerResource.class))
                .map(BrokerResource::getBroker);
    }

    private static Store getStore(ExtensionContext context) {
        return context.getStore(NAMESPACE);
    }

    private static class BrokerResource implements Store.CloseableResource {

        private final EmbeddedBroker broker;

        private BrokerResource(EmbeddedBroker broker) {
            this.broker = broker;
        }

        private EmbeddedBroker getBroker() {
            return broker;
        }

        @Override
        public void close() {
            log.debug("Stop broker: {}", broker);
            broker.stop();
        }
    }

}
