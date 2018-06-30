package com.github.levry.imq.embedded.junit.jupiter;

import com.github.levry.imq.embedded.EmbeddedBrokerBuilder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

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
    int port() default EmbeddedBrokerBuilder.DEFAULT_BROKER_PORT;
}
