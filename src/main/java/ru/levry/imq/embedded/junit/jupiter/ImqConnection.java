package ru.levry.imq.embedded.junit.jupiter;

import java.lang.annotation.*;

/**
 * @author levry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ImqConnection {
}
