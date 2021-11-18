package com.redhat.qpid.camel;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    @Produce("{{destination.address}}")
    public ProducerTemplate producerTemplate;

    public void sendMessage(String payload) {

        try {
            producerTemplate.sendBody(payload);
        } catch (Throwable t) {
            LOG.error(t.getMessage(), t);
        }
    }

}
