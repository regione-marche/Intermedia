package it.extrasys.marche.regione.fatturapa.api.rest.processor;

import it.extrasys.marche.regione.fatturapa.api.rest.model.ReinviaFatturaRequest;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GestioneCodaValidazioneKO implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(GestioneCodaValidazioneKO.class);

    private ConsumerTemplate consumer;
    private ProducerTemplate producer;
    private String codaValidazioneKo;

    /*
    Scoda tutti i messaggi dalla coda (nel body c'è l'identificativoSdi) e prepara una lista di ReinviaFatturaRequest per chiamare la rotta 'renivia-fatture'
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        int numeroMessaggi = ((Long) exchange.getIn().getHeader("codaSize")).intValue();
        int messaggiScodati = 0;
        List<String> identificativoSdIList = new ArrayList<>();

        for (int i = 0; i < numeroMessaggi; i++) {
            // receive the message from the queue, wait at most 3 sec
            Exchange message = consumer.receive("activemq:" + codaValidazioneKo, 3000);
            consumer.doneUoW(exchange);

            if (message == null) {
                // no more messages in queue
                break;
            }

            //TODO:verifica il tipo se è stringa
            String identificativoSdI = (String) message.getIn().getHeader("identificativoSdI"); // (String) message.getIn().getBody();
            identificativoSdIList.add(identificativoSdI);

            LOG.info("MONITORAGGIO - INVIO ESITO COMMITTENTE BATCH - Scodato identificativoSdI " + identificativoSdI);
            messaggiScodati++;
        }

        ReinviaFatturaRequest request = new ReinviaFatturaRequest();
        request.setOnlyRegistrazione(false);
        request.setIdentificativoSdi(identificativoSdIList);

        //Crea l'oggetto
        MessageContentsList mcl = new MessageContentsList();
        //Non c'è autenticazione
        mcl.set(0, (String) "token");
        mcl.set(1, (ReinviaFatturaRequest) request);
        exchange.getIn().setBody(mcl);
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

    public String getCodaValidazioneKo() {
        return codaValidazioneKo;
    }

    public void setCodaValidazioneKo(String codaValidazioneKo) {
        this.codaValidazioneKo = codaValidazioneKo;
    }
}
