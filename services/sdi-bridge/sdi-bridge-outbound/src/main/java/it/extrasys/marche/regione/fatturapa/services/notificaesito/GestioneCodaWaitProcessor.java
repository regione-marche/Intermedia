package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 13/05/15.
 */

public class GestioneCodaWaitProcessor implements Processor {

    private ConsumerTemplate consumer;

    private ProducerTemplate producer;

    private String codaWait;

    private String codaSend;

    private static final Logger LOG = LoggerFactory.getLogger(GestioneCodaWaitProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        int counter = 0;
        LOG.info("GestioneCodaWaitProcessor: START");
        while (true) {
            // receive the message from the queue, wait at most 3 sec

            Exchange message = consumer.receive(codaWait, 3000);
            consumer.doneUoW(exchange);

            if (message == null) {
                // no more messages in queue
                break;
            }

            producer.send(codaSend, message);
            counter++;
        }
        LOG.info("GestioneCodaWaitProcessor: STOP");

        LOG.info("GestioneCodaWaitProcessor: SCODATE E INVIATE " + counter + " NOTIFICHE DI RIFIUTO ALLA CODA DI INVIO ALLO SDI");
    }

    public void setConsumer(ConsumerTemplate consumer) {
        this.consumer = consumer;
    }

    public void setProducer(ProducerTemplate producer) {
        this.producer = producer;
    }

    public void setCodaWait(String codaWait) {
        this.codaWait = codaWait;
    }

    public void setCodaSend(String codaSend) {
        this.codaSend = codaSend;
    }
}
