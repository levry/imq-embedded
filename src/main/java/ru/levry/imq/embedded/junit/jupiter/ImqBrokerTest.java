package ru.levry.imq.embedded.junit.jupiter;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * @author levry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ExtendWith(EmbeddedBrokerExtension.class)
public @interface ImqBrokerTest {

}
