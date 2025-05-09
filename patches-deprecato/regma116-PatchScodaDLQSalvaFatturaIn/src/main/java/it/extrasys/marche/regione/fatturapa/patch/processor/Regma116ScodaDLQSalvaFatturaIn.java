package it.extrasys.marche.regione.fatturapa.patch.processor;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 10/07/15.
 */
public class Regma116ScodaDLQSalvaFatturaIn {

    private static final Logger LOG = LoggerFactory.getLogger(Regma116ScodaDLQSalvaFatturaIn.class);

    private ConsumerTemplate consumer;
    private ProducerTemplate producer;

    private String codaDlqSalvaFattureIn;

    //private String codaTmpDlqSalvaFattureIn;
    //Non viene più ulizzata la coda TMP in quanto viene rimesso tutto in salva.fatture.in.queue...
    //Se qualcosa và male, il messaggio rientra nella DLQ
    private String codaSalvaFattureIn;

    private static final String DLQ_SALVA_FATTURA_IN_QUEUE_SIZE_HEADER= "dlqSalvaFattureInQueueSize";

    private DatiFatturaManager datiFatturaManager;

    public void scodaDlqFatture(Exchange exchange) throws Exception {

        LOG.info("REGMA 116 - INIZIO SCODA DLQ FATTURE");

        int numeroFattureInDlq = (int)exchange.getIn().getHeader(DLQ_SALVA_FATTURA_IN_QUEUE_SIZE_HEADER);

        int messaggiScodati = 0;
        int messaggiReincodati = 0;

        for (int i = 0; i < numeroFattureInDlq; i++){

            Exchange exchangeTmp = consumer.receive(codaDlqSalvaFattureIn);

            Message message = exchangeTmp.getIn();
            //JmsMessage inMessage = (JmsMessage) exchangeTmp.getIn();

            String identificativoSdi = (String) message.getHeader("identificativoSdI");

            if (identificativoSdi == null || "".equals(identificativoSdi)) {

                //ERRORE nel prelievo dell'header

                //producer.send(codaTmpDlqSalvaFattureIn, exchangeTmp);
                producer.send(codaSalvaFattureIn, exchangeTmp);
            } else {
                LOG.info("REGMA 116 - Prossimo identificativo SdI: " + identificativoSdi);
            }

            List<DatiFatturaEntity> datiFatturaList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdi));

            if (datiFatturaList.size() == 0) {
                //nessuna fattura trovata per questo identificaivo SdI, il messaggio deve essere rimesso in coda perche' non e' correttamente trattato

                LOG.info("REGMA 116 - Nessuna fattura trovata per identificativo SdI " + identificativoSdi + ", messaggio verra' reincodato");

                //producer.send(codaTmpDlqSalvaFattureIn, exchangeTmp);
                producer.send(codaSalvaFattureIn, exchangeTmp);
                messaggiReincodati++;
            } else {
                //la fattura relativa a questo identificativo SdI e' gia' stata trattata, si tratta dunque di una ripetizione e si puo' eliminare dalla coda
                LOG.info("REGMA 116 - Trovata fattura per identificativo SdI " + identificativoSdi + ", messaggio verra' eliminato");
                messaggiScodati++;
            }

        }

        LOG.info("REGMA 116 - FINE, totale messaggi scodati: " + messaggiScodati + ", totale messaggi reincodati: " + messaggiReincodati);

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

    public String getCodaDlqSalvaFattureIn() {
        return codaDlqSalvaFattureIn;
    }

    public void setCodaDlqSalvaFattureIn(String codaDlqSalvaFattureIn) {
        this.codaDlqSalvaFattureIn = codaDlqSalvaFattureIn;
    }

    /*
    public String getCodaTmpDlqSalvaFattureIn() {
        return codaTmpDlqSalvaFattureIn;
    }

    public void setCodaTmpDlqSalvaFattureIn(String codaTmpDlqSalvaFattureIn) {
        this.codaTmpDlqSalvaFattureIn = codaTmpDlqSalvaFattureIn;
    }
    */

    public String getCodaSalvaFattureIn() {
        return codaSalvaFattureIn;
    }

    public void setCodaSalvaFattureIn(String codaSalvaFattureIn) {
        this.codaSalvaFattureIn = codaSalvaFattureIn;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}