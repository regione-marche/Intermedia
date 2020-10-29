package it.extrasys.marche.regione.fatturapa.patch;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Regma132ReinviaPecNonConsegnate {

    private static final Logger LOG = LoggerFactory.getLogger(Regma132ReinviaPecNonConsegnate.class);

    private ConsumerTemplate consumer;
    private ProducerTemplate producer;

    private String codaPecNotificaMancataConsegna;
    private String codaPecNotificaMancataConsegnaReinviati;

    private static final String QUEUE_SIZE_HEADER = "queueInSize";
    private static final String SUBJECT_HEADER = "subject";

    private DatiFatturaManager datiFatturaManager;

    public void scodaQueue(Exchange exchange) throws FatturaPAException {

        Message message = exchange.getIn();

        int numeroMessaggiInCoda = (int)exchange.getIn().getHeader(QUEUE_SIZE_HEADER);

        String listaIdentificativiSdIFatture = "";
        String listaIdentificativiSdIDecorrenze = "";

        for (int i = 0; i < numeroMessaggiInCoda; i++) {

            Exchange exchangeTmp = consumer.receive(codaPecNotificaMancataConsegna + "?concurrentConsumers=20");

            Message messageTmp = exchangeTmp.getIn();

            String subjectPec = messageTmp.getHeader(SUBJECT_HEADER, String.class);

            String[] subjectPecSplitted = subjectPec.split(":");

            if (subjectPecSplitted.length != 3) {
                LOG.info("Regma132ReinviaPecNonConsegnate - scodaQueue: messaggio dal formato errato: [" + subjectPec + "]");
            } else {

                String identificativoSdi = subjectPecSplitted[2].trim();

                String primaParteOriginalSubject = subjectPecSplitted[1];

                if (primaParteOriginalSubject.contains("Fattura")) {

                    LOG.info("Regma132ReinviaPecNonConsegnate - scodaQueue: FATTURA identificativo SdI: " + identificativoSdi);

                    listaIdentificativiSdIFatture = listaIdentificativiSdIFatture + identificativoSdi + ",";

                } else if (primaParteOriginalSubject.contains("Decorrenza")){

                    LOG.info("Regma132ReinviaPecNonConsegnate - scodaQueue: DECORRENZA identificativo SdI: " + identificativoSdi);

                    listaIdentificativiSdIDecorrenze = listaIdentificativiSdIDecorrenze + identificativoSdi + ",";

                } else {

                    LOG.info("Regma132ReinviaPecNonConsegnate - scodaQueue: messaggio malformato: [" + subjectPec + "]");
                }
            }

            //Li metto in una nuova coda (da cancellare a manella quando tutto è OK)
            producer.send(codaPecNotificaMancataConsegnaReinviati, exchangeTmp);
        }

        //tolgo l'ultima virgola in cima
        listaIdentificativiSdIFatture = removeLastChar(listaIdentificativiSdIFatture);
        listaIdentificativiSdIDecorrenze = removeLastChar(listaIdentificativiSdIDecorrenze);

        LOG.info("REGMA 132: lista identificativiSdIFatture ["+ listaIdentificativiSdIFatture + "]");

        LOG.info("REGMA 132: lista identificativiSdIDecorrenze ["+ listaIdentificativiSdIDecorrenze + "]");

        message.setHeader("listaIdentificativiSdIFatture", listaIdentificativiSdIFatture);

        message.setHeader("listaIdentificativiSdIDecorrenze", listaIdentificativiSdIDecorrenze);

        if(listaIdentificativiSdIFatture == null || "".equals(listaIdentificativiSdIFatture)){
            LOG.info("REGMA 132: Fatture: Nessun Identificativo Sdi da cancellazione gli stati dal DB");
        }else{
            LOG.info("REGMA 132: Fatture: Inizio cancellazione stati dal DB");

            cancellaStati("Fatture", listaIdentificativiSdIFatture);

            LOG.info("REGMA 132: Fatture: Fine cancellazione stati dal DB");
        }

        if(listaIdentificativiSdIDecorrenze == null || "".equals(listaIdentificativiSdIDecorrenze)){
            LOG.info("REGMA 132: Dec. Termini: Nessun Identificativo Sdi da cancellazione gli stati dal DB");
        }else{
            LOG.info("REGMA 132: Dec. Termini: Inizio cancellazione stati dal DB");

            cancellaStati("DecTermini", listaIdentificativiSdIDecorrenze);

            LOG.info("REGMA 132: Dec. Termini: Fine cancellazione stati dal DB");
        }
    }

    private void cancellaStati(String tipoOp, String listaIdSdi) throws FatturaPAException {

       datiFatturaManager.cancellaStatiPerReinvio(tipoOp, listaIdSdi);

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

    public String getCodaPecNotificaMancataConsegna() {
        return codaPecNotificaMancataConsegna;
    }

    public void setCodaPecNotificaMancataConsegna(String codaPecNotificaMancataConsegna) {
        this.codaPecNotificaMancataConsegna = codaPecNotificaMancataConsegna;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public String getCodaPecNotificaMancataConsegnaReinviati() {
        return codaPecNotificaMancataConsegnaReinviati;
    }

    public void setCodaPecNotificaMancataConsegnaReinviati(String codaPecNotificaMancataConsegnaReinviati) {
        this.codaPecNotificaMancataConsegnaReinviati = codaPecNotificaMancataConsegnaReinviati;
    }
}