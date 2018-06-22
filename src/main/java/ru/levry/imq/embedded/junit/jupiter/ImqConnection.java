package ru.levry.imq.embedded.junit.jupiter;

import java.lang.annotation.*;

/**
 * @author levry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
public @interface ImqConnection {
}
