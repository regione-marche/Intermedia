package it.extrasys.marche.regione.fatturapa.api.rest.processor;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorRielaborazioniEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MonitorRielaborazioniManager;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 13/05/15.
 */

public class GestioneCodaWaitProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(GestioneCodaWaitProcessor.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private ConsumerTemplate consumer;
    private ProducerTemplate producer;
    private String codaWait;
    private MonitorRielaborazioniManager monitorRielaborazioniManager;
    private Integer minutiPassati;

    @Override
    public void process(Exchange exchange) throws Exception {
        int numeroMessaggiInDlq = ((Long) exchange.getIn().getHeader("queueInSize")).intValue();
        int messaggiScodati = 0;

        List<List<MonitorRielaborazioniEntity>> monitorRielaborazioniList = new ArrayList<>();

        for (int i = 0; i < numeroMessaggiInDlq; i++) {
            // receive the message from the queue, wait at most 3 sec

            Exchange message = consumer.receive("activemq:" + codaWait, 3000);
            consumer.doneUoW(exchange);

            if (message == null) {
                // no more messages in queue
                break;
            }
            DateTime dataRielaborazione = new DateTime(sdf.parse((String) message.getIn().getHeader("MONITORAGGIO.dataRielaborazione")));
            DateTime now = new DateTime();
            Minutes minutes = Minutes.minutesBetween(dataRielaborazione, now);
            if (minutes.isGreaterThan(Minutes.minutes(minutiPassati))) {
                //Sono passati N minuti -> OK
                List<MonitorRielaborazioniEntity> monitorRielaborazioni = monitorRielaborazioniManager.getMonitorRielaborazioniByNomeReport((String) message.getIn().getBody());
                monitorRielaborazioniList.add(monitorRielaborazioni);
            } else {
                //rimette in coda
                producer.send("activemq:" + codaWait, message);
               // LOG.info("MONITOR REPORT - GestioneCodaWaitProcessor: messaggio rimesso in coda");
            }

            messaggiScodati++;
        }
        exchange.getIn().setBody(monitorRielaborazioniList);
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

    public MonitorRielaborazioniManager getMonitorRielaborazioniManager() {
        return monitorRielaborazioniManager;
    }

    public void setMonitorRielaborazioniManager(MonitorRielaborazioniManager monitorRielaborazioniManager) {
        this.monitorRielaborazioniManager = monitorRielaborazioniManager;
    }

    public Integer getMinutiPassati() {
        return minutiPassati;
    }

    public void setMinutiPassati(Integer minutiPassati) {
        this.minutiPassati = minutiPassati;
    }
}
