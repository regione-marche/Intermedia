package it.extrasys.marche.regione.fatturapa.patch.processor;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.patch.Utils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class CodiceHashFatturaAttivaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CodiceHashFatturaAttivaProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdi";
    private static final String CODE_HASH_HEADER = "codHash";
    private static final String FORMATO_TRASMISSIONE_HEADER = "formatoTrasmissioneFatturaAttiva";
    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";
    private static final String NOME_FILE_HEADER = "nomeFile";


    private FatturaAttivaManagerImpl fatturaAttivaManager;

    @Override
    public void process(Exchange exchange) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        Message message = exchange.getIn();

        FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManager.getFatturaAttivaFromIdentificativSdi(new BigInteger(message.getHeader(IDENTIFICATIVO_SDI_HEADER, String.class)));

        String hashFileFattura = Utils.hashString(fatturaAttivaEntity.getFileFatturaOriginale(), "SHA-256");

        message.setHeader(CODE_HASH_HEADER, hashFileFattura);
        message.setHeader(FORMATO_TRASMISSIONE_HEADER, fatturaAttivaEntity.getFormatoTrasmissione());
        message.setHeader(TIPO_MESSAGGIO_HEADER, TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue());
        message.setHeader(NOME_FILE_HEADER, Utils.getNomeFileRC(fatturaAttivaEntity.getNomeFile()));
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }
}
