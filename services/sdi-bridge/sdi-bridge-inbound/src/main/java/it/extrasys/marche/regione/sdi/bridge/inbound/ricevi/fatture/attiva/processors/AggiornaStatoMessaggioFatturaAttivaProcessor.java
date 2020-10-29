package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.attiva.processors;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaNotificheManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 22/03/15.
 */
public class AggiornaStatoMessaggioFatturaAttivaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggiornaStatoMessaggioFatturaAttivaProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdi";
    private static final String NOME_FILE_HEADER = "nomeFile";

    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";
    private static final String ID_NOTIFICA_HEADER = "idNotifica";

    private FatturaAttivaManagerImpl fatturaAttivaManager;
    private FatturaAttivaNotificheManager fatturaAttivaNotificheManager;

    @Override
    public void process(Exchange exchange) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        Message msg = exchange.getIn();

        BigInteger identificativoSdi = new BigInteger((String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER));

        FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManager.getFatturaAttivaFromIdentificativSdi(identificativoSdi);

        String codificaStatoEntity = CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.RICEVUTA.getValue();

        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO_HEADER);

        BigInteger idNotifica = null;

        if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.getValue().equals(tipoMessaggio)){

            idNotifica = fatturaAttivaNotificheManager.salvaStatoAttestazioneTrasmissioneFattura(fatturaAttivaEntity, codificaStatoEntity);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.getValue().equals(tipoMessaggio)){

            idNotifica = fatturaAttivaNotificheManager.salvaStatoNotificaDecorrenzaTermini(fatturaAttivaEntity, codificaStatoEntity);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.getValue().equals(tipoMessaggio)){

            idNotifica = fatturaAttivaNotificheManager.salvaStatoNotificaEsito(fatturaAttivaEntity, codificaStatoEntity);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.getValue().equals(tipoMessaggio)){

            idNotifica = fatturaAttivaNotificheManager.salvaStatoNotificaScarto(fatturaAttivaEntity, codificaStatoEntity);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.getValue().equals(tipoMessaggio)){

            idNotifica = fatturaAttivaNotificheManager.salvaStatoNotificaMancataConsegna(fatturaAttivaEntity, codificaStatoEntity);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue().equals(tipoMessaggio)){

            idNotifica = fatturaAttivaNotificheManager.salvaStatoRicevutaConsegna(fatturaAttivaEntity, codificaStatoEntity);
        }

        msg.setHeader(ID_NOTIFICA_HEADER, idNotifica.toString());
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }

    public FatturaAttivaNotificheManager getFatturaAttivaNotificheManager() {
        return fatturaAttivaNotificheManager;
    }

    public void setFatturaAttivaNotificheManager(FatturaAttivaNotificheManager fatturaAttivaNotificheManager) {
        this.fatturaAttivaNotificheManager = fatturaAttivaNotificheManager;
    }
}
