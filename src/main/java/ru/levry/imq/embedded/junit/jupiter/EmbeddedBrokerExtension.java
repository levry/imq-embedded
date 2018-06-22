package ru.levry.imq.embedded.junit.jupiter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import ru.levry.imq.embedded.EmbeddedBroker;

import javax.jms.ConnectionFactory;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;

/**
 * @author levry
 */
@Slf4j
public class EmbeddedBrokerExtension implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor {

    private static final Namespace NAMESPACE = Namespace.create(EmbeddedBrokerExtension.class);
    private static final String KEY = "embeddedBroker";

    @Override
    public void beforeAll(ExtensionContext context) {
        EmbeddedBroker embeddedBroker = getOrCreateBroker(context);

        log.debug("Run broker: {}", embeddedBroker);
        embeddedBroker.run();
    }

    private static EmbeddedBroker getOrCreateBroker(ExtensionContext context) {
        Store store = getStore(context);
        return store.getOrComputeIfAbsent(KEY, key -> createBroker(), EmbeddedBroker.class);
    }

    private static EmbeddedBroker createBroker() {
        return EmbeddedBroker.builder().homeTemp().build();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        withBroker(context).ifPresent(embeddedBroker -> {
            log.debug("Stop broker: {}", embeddedBroker);
            embeddedBroker.stop();
        });
    }

    private static Optional<EmbeddedBroker> withBroker(ExtensionContext context) {
        Store store = getStore(context);
        return Optional.ofNullable(store.get(KEY, EmbeddedBroker.class));
    }

    private static Store getStore(ExtensionContext context) {
        return context.getRoot().getStore(NAMESPACE);
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

    private Optional<ConnectionFactory> withConnectionFactory(ExtensionContext context) {
        return withBroker(context).map(EmbeddedBroker::connectionFactory);
    }

    @SneakyThrows
    private void setupField(Field field, Object testInstance, ConnectionFactory connectionFactory) {
        field.setAccessible(true);
        field.set(testInstance, connectionFactory);
    }

}
