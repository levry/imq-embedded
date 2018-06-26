package com.github.levry.imq.embedded.support;

import com.sun.messaging.ConnectionConfiguration; // NOSONAR
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.jms.ConnectionFactory;

/**
 * Internal utility
 *
 * @author levry
 */
@UtilityClass
public class JmsSupport {

    @SneakyThrows
    public static ConnectionFactory createConnectionFactory(int hostPort) {
        com.sun.messaging.ConnectionFactory qcf = new com.sun.messaging.ConnectionFactory();
        qcf.setProperty(ConnectionConfiguration.imqBrokerHostName, "localhost");
        qcf.setProperty(ConnectionConfiguration.imqBrokerHostPort, Integer.toString(hostPort));
        return qcf;
    }

}
