package ru.levry.imq.embedded.junit.jupiter;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

import static ru.levry.imq.embedded.EmbeddedBrokerBuilder.DEFAULT_BROKER_PORT;

/**
 * Enables extension {@link EmbeddedBrokerExtension}.
 * <p>
 * Allows to customize a embedded broker:
 * <ul>
 * <li>port number (default: 7676)</li>
 * </ul>
 * </pre>
 *
 * @author levry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ExtendWith(EmbeddedBrokerExtension.class)
public @interface ImqBrokerTest {

    /**
     * Port number of embedded broker
     */
    int port() default DEFAULT_BROKER_PORT;
}
