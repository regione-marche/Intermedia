package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAAllegatoNotificaEsitoCommNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPACampiObbligatoriNonValorizzatiException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPANomeFileErratoException;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeNotificaEsitoCommittente;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class VerificaReqEsitoCommittenteCAProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(VerificaReqEsitoCommittenteCAProcessor.class);

    private final String REQ_CAMPI_EX_HEADER = "reqCampiEX";
    private final String REQ_NOME_FILE_EX_HEADER = "reqNomeFileEX";
    private final String REQ_FILE_EX_HEADER = "reqFileEX";

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        msg.setHeader(REQ_CAMPI_EX_HEADER, false);
        msg.setHeader(REQ_NOME_FILE_EX_HEADER, false);
        msg.setHeader(REQ_FILE_EX_HEADER, false);

        MessageContentsList message = msg.getBody(MessageContentsList.class);

        EsitoFatturaMessageRequest esitoFatturaMessageRequest = (EsitoFatturaMessageRequest) message.get(0);

        if(esitoFatturaMessageRequest.getEsitoFatturaMessage().getIdFiscaleCommittente() == null || "".equals(esitoFatturaMessageRequest.getEsitoFatturaMessage().getIdFiscaleCommittente()) ||
                esitoFatturaMessageRequest.getEsitoFatturaMessage().getCodUfficio() == null || "".equals(esitoFatturaMessageRequest.getEsitoFatturaMessage().getCodUfficio()) ||
                esitoFatturaMessageRequest.getEsitoFatturaMessage().getNomeFile() == null || "".equals(esitoFatturaMessageRequest.getEsitoFatturaMessage().getNomeFile()) ||
                esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile() == null || esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile().getContent() == null) {

            msg.setHeader(REQ_CAMPI_EX_HEADER, true);
            throw new FatturaPACampiObbligatoriNonValorizzatiException();
        }

        //Verifico nome file nella req
        if(!ValidatoreNomeNotificaEsitoCommittente.validate(esitoFatturaMessageRequest.getEsitoFatturaMessage().getNomeFile())){
            msg.setHeader(REQ_NOME_FILE_EX_HEADER, true);
            throw new FatturaPANomeFileErratoException();
        }

        //Verifico presenza allegato
        if(esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile() == null ||
                esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile().getContent() == null){
            msg.setHeader(REQ_FILE_EX_HEADER, true);
            throw new FatturaPAAllegatoNotificaEsitoCommNonTrovatoException();
        }
        ByteArrayInputStream bais = (ByteArrayInputStream) esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile().getContent();
        if(bais.available() == 0){
            msg.setHeader(REQ_FILE_EX_HEADER, true);
            throw new FatturaPAAllegatoNotificaEsitoCommNonTrovatoException();
        }
    }}