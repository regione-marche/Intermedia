package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReinviaPecCaNonConsegnate {

    private static final Logger LOG = LoggerFactory.getLogger(ReinviaPecCaNonConsegnate.class);

    private ConsumerTemplate consumer;
    private ProducerTemplate producer;

    private String codaPecCaNotificaMancataConsegna;
    private String codaPecCaNotificaMancataConsegnaReinviati;

    private static final String QUEUE_SIZE_HEADER = "queueInSize";
    private static final String SUBJECT_HEADER = "subject";

    private DatiFatturaManager datiFatturaManager;

    public void scodaQueue(Exchange exchange) throws FatturaPAException {

        Message message = exchange.getIn();

        int numeroMessaggiInCoda = (int)exchange.getIn().getHeader(QUEUE_SIZE_HEADER);

        String listaIdentificativiSdIFatture = "";
        String listaIdentificativiSdIDecorrenze = "";

        for (int i = 0; i < numeroMessaggiInCoda; i++) {

            Exchange exchangeTmp = consumer.receive(codaPecCaNotificaMancataConsegna + "?concurrentConsumers=20");

            Message messageTmp = exchangeTmp.getIn();

            LOG.info("HEADERS "+messageTmp.getHeaders());

            String subjectPec = messageTmp.getHeader(SUBJECT_HEADER, String.class);

            String[] subjectPecSplitted = subjectPec.split(":");

            if (subjectPecSplitted.length != 3) {
                LOG.info("ReinviaPecCaNonConsegnate - scodaQueue: messaggio dal formato errato: [" + subjectPec + "]");
            } else {

                String identificativoSdi = subjectPecSplitted[2].trim();

                String primaParteOriginalSubject = subjectPecSplitted[1];

                if (primaParteOriginalSubject.contains("Fattura")) {

                    LOG.info("ReinviaPecCaNonConsegnate - scodaQueue: FATTURA identificativo SdI: " + identificativoSdi);

                    listaIdentificativiSdIFatture = listaIdentificativiSdIFatture + identificativoSdi + ",";

                } else if (primaParteOriginalSubject.contains("Decorrenza")){

                    LOG.info("ReinviaPecCaNonConsegnate - scodaQueue: DECORRENZA identificativo SdI: " + identificativoSdi);

                    listaIdentificativiSdIDecorrenze = listaIdentificativiSdIDecorrenze + identificativoSdi + ",";

                } else {

                    LOG.info("ReinviaPecCaNonConsegnate - scodaQueue: messaggio malformato: [" + subjectPec + "]");
                }
            }

        }

        //tolgo l'ultima virgola in cima
        listaIdentificativiSdIFatture = removeLastChar(listaIdentificativiSdIFatture);
        listaIdentificativiSdIDecorrenze = removeLastChar(listaIdentificativiSdIDecorrenze);

        LOG.info("lista identificativiSdIFatture ["+ listaIdentificativiSdIFatture + "]");

        LOG.info("lista identificativiSdIDecorrenze ["+ listaIdentificativiSdIDecorrenze + "]");

        message.setHeader("listaIdentificativiSdIFatture", listaIdentificativiSdIFatture);

        message.setHeader("listaIdentificativiSdIDecorrenze", listaIdentificativiSdIDecorrenze);

    }

    private String removeLastChar(String str) {

        if(str == null || "".equals(str)){
            return "";
        }

        return str.substring(0,str.length()-1);
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

    public String getCodaPecCaNotificaMancataConsegna() {
        return codaPecCaNotificaMancataConsegna;
    }

    public void setCodaPecCaNotificaMancataConsegna(String codaPecCaNotificaMancataConsegna) {
        this.codaPecCaNotificaMancataConsegna = codaPecCaNotificaMancataConsegna;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public String getCodaPecCaNotificaMancataConsegnaReinviati() {
        return codaPecCaNotificaMancataConsegnaReinviati;
    }

    public void setCodaPecCaNotificaMancataConsegnaReinviati(String codaPecCaNotificaMancataConsegnaReinviati) {
        this.codaPecCaNotificaMancataConsegnaReinviati = codaPecCaNotificaMancataConsegnaReinviati;
    }
}