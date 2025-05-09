package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificheManagerXAImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 19/02/15.
 */
public class SalvaNotifichePersistenceProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(SalvaNotifichePersistenceProcessor.class);

    private static final String CHECK_FLUSSO_SEMPLIFICATO_HEADER = "checkFlussoSemplificato";
    private static final String NOME_FILE_FATTURA_HEADER = "nomeFileFattura";


    FatturazionePassivaNotificheManagerXAImpl fatturazionePassivaNotificheManagerXA;

    public void process(Exchange exchange) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException {

        String idComunicazione = exchange.getIn().getHeader("idComunicazione", String.class);

        MessageContentsList message = exchange.getIn().getBody(MessageContentsList.class);

        EsitoFatturaMessageRequest esitoFatturaMessageRequest = (EsitoFatturaMessageRequest) message.get(0);

        /*
            REGMA 112: questo array di stringa e' stato fatto per ottenere un passaggio di parametro per riferimento e ottenere dunque dei dati senza dover fare
            nuovamente una query: mi serve infatti sapere se si tratta di una fattura che rientra nel ciclo del flusso semplificato e in questo caso avere anche
            il nome del file fattura originale. Questo lo potrei fare una query ma la funzione salvaNotifiche lo fa' gia' ;)
         */
        String[] flussoSemplificatoArray = new String[2];
        flussoSemplificatoArray[0] = "F";
        flussoSemplificatoArray[1] = "";

        List<NotificaFromEntiEntity> notificaFromEntiEntityList = fatturazionePassivaNotificheManagerXA.salvaNotifiche(esitoFatturaMessageRequest, idComunicazione, flussoSemplificatoArray);

        if("T".equals(flussoSemplificatoArray[0])){
            exchange.getIn().setHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER, CHECK_FLUSSO_SEMPLIFICATO_HEADER);

            LOG.info("SalvaNotifichePersistenceProcessor: caso flusso semplificato, nome file fattura: " + flussoSemplificatoArray[1]   );
            exchange.getIn().setHeader(NOME_FILE_FATTURA_HEADER, flussoSemplificatoArray[1]);
        }

        exchange.getIn().setBody(esitoFatturaMessageRequest.getEsitoFatturaMessage());

    }

    public FatturazionePassivaNotificheManagerXAImpl getFatturazionePassivaNotificheManagerXA() {
        return fatturazionePassivaNotificheManagerXA;
    }

    public void setFatturazionePassivaNotificheManagerXA(FatturazionePassivaNotificheManagerXAImpl fatturazionePassivaNotificheManagerXA) {
        this.fatturazionePassivaNotificheManagerXA = fatturazionePassivaNotificheManagerXA;
    }
}