# OpenMQ Embedded Broker
[![Build Status](https://travis-ci.org/levry/imq-embedded.svg?branch=master)](https://travis-ci.org/levry/imq-embedded)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=github.levry.imq.embedded&metric=alert_status)](https://sonarcloud.io/dashboard?id=github.levry.imq.embedded)
[![Download](https://api.bintray.com/packages/levry/maven/imq-embedded/images/download.svg)](https://bintray.com/levry/maven/imq-embedded/_latestVersion)
[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](//github.com/levry/imq-embedded/blob/master/LICENSE)


Run openMQ embedded broker without complete installation of [openMQ](https://javaee.github.io/openmq).
This can be useful when creating tests.

### How to connect the project

Gradle

```groovy
repositories {
    jcenter()
}
```

```groovy
compile 'com.github.levry:imq-embedded:+'
```


### How to use

```java
    EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().build();
    broker.run();
    
    try {
        ConnectionFactory connectionFactory = broker.connectionFactory();
        // Do it
    } finally {
        broker.stop();
    }
```