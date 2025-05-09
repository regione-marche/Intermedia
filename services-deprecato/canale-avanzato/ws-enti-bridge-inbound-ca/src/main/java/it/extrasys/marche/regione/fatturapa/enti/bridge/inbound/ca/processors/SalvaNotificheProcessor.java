package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificheCAManagerXAImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class SalvaNotificheProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(SalvaNotificheProcessor.class);

    private static final String CHECK_FLUSSO_SEMPLIFICATO_HEADER = "checkFlussoSemplificato";
    private static final String NOME_FILE_FATTURA_HEADER = "nomeFileFattura";

    private FatturazionePassivaNotificheCAManagerXAImpl fatturazionePassivaNotificheCAManagerXA;

    public void process(Exchange exchange) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException, IOException, JAXBException {

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

        NotificaEsitoCommittenteType notificaEsitoCommittente = JaxBUtils.getNotificaEsitoCommittenteType(esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile());

        NotificaFromEntiEntity notificaFromEntiEntity = fatturazionePassivaNotificheCAManagerXA.salvaNotifica(esitoFatturaMessageRequest, notificaEsitoCommittente, flussoSemplificatoArray);

        if ("T".equals(flussoSemplificatoArray[0])) {
            exchange.getIn().setHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER, CHECK_FLUSSO_SEMPLIFICATO_HEADER);

            LOG.info("SalvaNotificheProcessor: caso flusso semplificato, nome file fattura: " + flussoSemplificatoArray[1]);
            exchange.getIn().setHeader(NOME_FILE_FATTURA_HEADER, flussoSemplificatoArray[1]);
        }

        //exchange.getIn().setBody(esitoFatturaMessageRequest.getEsitoFatturaMessage());
        exchange.getIn().setHeader("dataRicezioneEnte", notificaFromEntiEntity.getDataRicezioneFromEnte());
        exchange.getIn().setHeader("nomeCedentePrestatore", notificaFromEntiEntity.getNomeCedentePrestatore());
        exchange.getIn().setBody(esitoFatturaMessageRequest);

    }

    public FatturazionePassivaNotificheCAManagerXAImpl getFatturazionePassivaNotificheCAManagerXA() {
        return fatturazionePassivaNotificheCAManagerXA;
    }

    public void setFatturazionePassivaNotificheCAManagerXA(FatturazionePassivaNotificheCAManagerXAImpl fatturazionePassivaNotificheCAManagerXA) {
        this.fatturazionePassivaNotificheCAManagerXA = fatturazionePassivaNotificheCAManagerXA;
    }
}