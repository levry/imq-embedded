package ru.levry.imq.embedded.junit.jupiter;

import java.lang.annotation.*;

/**
 * Injects {@link javax.jms.ConnectionFactory} into test
 *
 * <p>Inject field:<pre>
 *     &#064;ImqConnection
 *     ConnectionFactory connectionFactory
 *
 *     &#064;Test
 *     void testJms() throws Exception {
 *         try (Connection conn = connectionFactory.createConnection()) {
 *             // Do it
 *         }
 *     }
 * </pre>
 *
 * <p>Inject parameter:<pre class="code">
 *     &#064;Test
 *     void testJms(&#064;ImqConnection ConnectionFactory connectionFactory) throws Exception {
 *         try (Connection conn = connectionFactory.createConnection()) {
 *             // Do it
 *         }
 *     }
 * </pre>
 *
 * @author levry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
public @interface ImqConnection {
}
