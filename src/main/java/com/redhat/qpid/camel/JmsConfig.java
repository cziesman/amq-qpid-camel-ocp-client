package com.redhat.qpid.camel;

import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.transports.TransportOptions;
import org.apache.qpid.jms.transports.TransportSupport;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;

@Configuration
public class JmsConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    @Value("${broker.scheme}")
    private String brokerScheme;

    @Value("${broker.host}")
    private String brokerHost;

    @Value("${broker.port}")
    private String brokerPort;

    @Value("${broker.username}")
    private String brokerUsername;

    @Value("${broker.password}")
    private String brokerPassword;

    @Value("${broker.maxConnections}")
    private Integer brokerMaxConnections;

    @Value("${trustStorePath}")
    private String trustStorePath;

    @Value("${trustStorePassword}")
    private String trustStorePassword;

    @Value("${verifyHostName}")
    private String verifyHostName;

    @Bean
    public AMQPComponent amqpComponent(CachingConnectionFactory connectionFactory) {

        AMQPComponent jms = new AMQPComponent();
        jms.setConnectionFactory(connectionFactory);

        return jms;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() throws Exception {

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setSessionCacheSize(brokerMaxConnections);
        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory());
        cachingConnectionFactory.afterPropertiesSet();

        return cachingConnectionFactory;
    }

    protected JmsConnectionFactory connectionFactory() {

        JmsConnectionFactory factory = new JmsConnectionFactory();
        factory.setRemoteURI(remoteUri());
        factory.setUsername(brokerUsername);
        factory.setPassword(brokerPassword);

        return factory;
    }

    private String remoteUri() {

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(brokerScheme)
                .host(brokerHost)
                .port(brokerPort)
                .queryParam("transport.trustStoreLocation", trustStorePath)
                .queryParam("transport.trustStorePassword", trustStorePassword)
                .queryParam("transport.verifyHost", verifyHostName)
                .build();

        LOG.debug(uriComponents.toUriString());

        return String.format("failover:(%s)", uriComponents.toUriString());
    }
}
