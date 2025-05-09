package it.extrasys.marche.regione.fatturapa.patch;

import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by agosteeno on 22/03/16.
 */
public class Regma135ReinviaMessaggiDaDlq {

    private static final Logger LOG = LoggerFactory.getLogger(Regma135ReinviaMessaggiDaDlq.class);

    private ConsumerTemplate consumer;
    private ProducerTemplate producer;

    private static final String DLQ_QUEUE_NAME_HEADER = "DLQqueueName";
    private static final String DLQ_IN_QUEUE_SIZE_HEADER = "dlqInSize";

    //di solito e' sempre activemq, ma nei test si cambia in vm
    private String activeMqComponentName;

    public void scodaDlq(Exchange exchange) throws Exception {

        Message messageMain = exchange.getIn();

        String dlqQueueName = (String) messageMain.getHeader(DLQ_QUEUE_NAME_HEADER);

        /*
        per prendere il nome corretto della coda nella quale effettuare l'inserimento devo "staccare" la parte iniziale,
        che contiene il prefisso "DLQ."
         */
        String queueName = FileUtils.estraiNomeOriginaleDaDlq(dlqQueueName);

        if(queueName == null){

            LOG.warn("REGMA 135 la coda " + queueName + " non e' una dlq regolare!");

            return;
        }

        LOG.info("REGMA 135 - INIZIO SCODA DLQ ");

        int numeroMessaggiInDlq = (int)exchange.getIn().getHeader(DLQ_IN_QUEUE_SIZE_HEADER);

        int messaggiScodati = 0;

        for (int i = 0; i < numeroMessaggiInDlq; i++){

            Exchange exchangeTmp = consumer.receive(activeMqComponentName + ":" + dlqQueueName);

            producer.send(activeMqComponentName + ":" + queueName, exchangeTmp);
            messaggiScodati++;
        }

        LOG.info("REGMA 135 coda " +  " " + ", totale messaggi scodati: " + messaggiScodati );

    }

    public ConsumerTemplate getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerTemplate consumer) {
        this.consumer = consumer;
    }

    public ProducerTemplate getProducer() {
        return producer;
    }

    public void setProducer(ProducerTemplate producer) {
        this.producer = producer;
    }

    public String getActiveMqComponentName() {
        return activeMqComponentName;
    }

    public void setActiveMqComponentName(String activeMqComponentName) {
        this.activeMqComponentName = activeMqComponentName;
    }
}
