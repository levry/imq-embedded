# OpenMQ Embedded Broker
[![Build Status](https://travis-ci.org/levry/imq-embedded.svg?branch=master)](https://travis-ci.org/levry/imq-embedded)


### Embedded broker

Run openMQ embedded broker without complete installation of [openMQ](https://javaee.github.io/openmq).
This can be useful when creating tests.

````java
    EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().build();
    broker.run();
    
    try {
        ConnectionFactory connectionFactory = broker.connectionFactory();
        // Do it
    } finally {
        broker.stop();
    }
````

### Junit extension

A [JUnit 5 extension](https://junit.org/junit5/docs/current/user-guide/#extensions) for testing a jms interaction with openMQ broker.

````java
@ImqBrokerTest
class YourTest {

    @ImqConnection
    private ConnectionFactory connectionFactory;

    @Test
    void testJms() throws Exception {
        try (Connection conn = connectionFactory.createConnection()) {
            // Do stuff
        }
    }

}
````