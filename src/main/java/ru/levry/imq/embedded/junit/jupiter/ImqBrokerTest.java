package ru.levry.imq.embedded.junit.jupiter;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

import static ru.levry.imq.embedded.EmbeddedBrokerBuilder.DEFAULT_BROKER_PORT;

/**
 * @author levry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ExtendWith(EmbeddedBrokerExtension.class)
public @interface ImqBrokerTest {

    int port() default DEFAULT_BROKER_PORT;
}
