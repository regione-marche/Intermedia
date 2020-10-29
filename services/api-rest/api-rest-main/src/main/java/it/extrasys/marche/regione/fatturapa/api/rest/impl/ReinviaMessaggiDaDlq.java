package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorRielaborazioniEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MonitorRielaborazioniManager;
import org.apache.camel.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 22/03/16.
 */
public class ReinviaMessaggiDaDlq {

    private static final Logger LOG = LoggerFactory.getLogger(ReinviaMessaggiDaDlq.class);


    private ConsumerTemplate consumer;
    private ProducerTemplate producer;
    private MonitorRielaborazioniManager monitorRielaborazioniManager;

    private static final String DLQ_QUEUE_NAME_HEADER = "DLQqueueName";
    private static final String DLQ_IN_QUEUE_SIZE_HEADER = "dlqInSize";

    //di solito e' sempre activemq, ma nei test si cambia in vm
    private String activeMqComponentName;

    public void scodaDlq(Exchange exchange) throws Exception {
        Message messageMain = exchange.getIn();
        String username = (String) exchange.getIn().getHeader("MONITORAGGIO.username");
        String nomeReport = (String) exchange.getIn().getHeader("MONITORAGGIO.nomeReport");

        String dlqQueueName = (String) messageMain.getHeader(DLQ_QUEUE_NAME_HEADER);

        /*
        per prendere il nome corretto della coda nella quale effettuare l'inserimento devo "staccare" la parte iniziale,
        che contiene il prefisso "DLQ."
         */
        String queueName = FileUtils.estraiNomeOriginaleDaDlq(dlqQueueName);

        if (queueName == null) {

            LOG.warn("RIELABORA MESSAGGI la coda " + queueName + " non e' una dlq regolare!");

            return;
        }

        LOG.info("RIELABORA MESSAGGI - INIZIO SCODA DLQ ");

        int numeroMessaggiInDlq = ((Long) exchange.getIn().getHeader(DLQ_IN_QUEUE_SIZE_HEADER)).intValue();

        int messaggiScodati = 0;

        for (int i = 0; i < numeroMessaggiInDlq; i++) {

            //Elimino dalla DLQ i messaggi dei TEST
            Boolean isTest = Boolean.FALSE;
            if (exchange.getIn().getHeader("fatturazioneTest") != null) {
                isTest = Boolean.parseBoolean((String) exchange.getIn().getHeader("fatturazioneTest"));
            }

            Exchange exchangeTmp = consumer.receive(activeMqComponentName + ":" + dlqQueueName);

            BigInteger identificativoSdi = null;
            if (StringUtils.isNotEmpty((String) exchangeTmp.getIn().getHeader("identificativoSdI"))) {
                identificativoSdi = new BigInteger((String) exchangeTmp.getIn().getHeader("identificativoSdI"));
            }

            Integer numeroRielaborazioni = 0;
            if ((Integer) exchangeTmp.getIn().getHeader("MONITOR.numero_rielaborazioni") != null) {
                numeroRielaborazioni = (Integer) exchangeTmp.getIn().getHeader("MONITOR.numero_rielaborazioni");
            }

            numeroRielaborazioni++;
            exchangeTmp.getIn().setHeader("MONITOR.numero_rielaborazioni", numeroRielaborazioni);
            exchangeTmp.getIn().setHeader("MONITORAGGIO.nomeReport", nomeReport);



            if (!isTest) {
                exchangeTmp.setPattern(ExchangePattern.InOnly);
                producer.send(activeMqComponentName + ":" + queueName, exchangeTmp);

                //Scrive audit sul DB
                MonitorRielaborazioniEntity monitorRielaborazioni = monitorRielaborazioniManager.salvaRielaborazione(numeroRielaborazioni, username, queueName, nomeReport, identificativoSdi);
            }
            messaggiScodati++;
        }

        LOG.info("RIELABORA MESSAGGI coda " + " " + ", totale messaggi scodati: " + messaggiScodati);
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

    public MonitorRielaborazioniManager getMonitorRielaborazioniManager() {
        return monitorRielaborazioniManager;
    }

    public void setMonitorRielaborazioniManager(MonitorRielaborazioniManager monitorRielaborazioniManager) {
        this.monitorRielaborazioniManager = monitorRielaborazioniManager;
    }
}
