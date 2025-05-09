package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.attiva.processors;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStatiAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaNotificheManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 08/07/16.
 */
public class AggiornaStatoNotificaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggiornaStatoNotificaProcessor.class);

    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";
    private static final String ID_NOTIFICA_HEADER = "idNotifica";
    private static final String IDENTIFICATIVO_SDI = "identificativoSdI";
    private static final String NOME_ENTE_OSPEDALIERO = "nomeEnteHeader";

    private FatturaAttivaNotificheManager fatturaAttivaNotificheManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        String identificativoSdI = (String)msg.getHeader(IDENTIFICATIVO_SDI);
        String nomeEnteOspedaliero = (String)msg.getHeader(NOME_ENTE_OSPEDALIERO);

        LOG.info("AggiornaStatoNotificaProcessor - aggiornaStatoNotifica " + nomeEnteOspedaliero + " STARTED, identificativo SdI: " + identificativoSdI);

        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO_HEADER);

        String statoInviata = CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.INVIATA.getValue();

        String headerIdNotifica = (String) msg.getHeader(ID_NOTIFICA_HEADER);
        BigInteger idNotifica = new BigInteger(headerIdNotifica);

        if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoRiceviConsegna(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoMancataConsegna(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoNotificaScarto(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoNotificaEsito(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoNotificaDecorrenzaTermini(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoAttestazioneTrasmissioneFattura(statoInviata, idNotifica);
        }

    }

    public FatturaAttivaNotificheManager getFatturaAttivaNotificheManager() {
        return fatturaAttivaNotificheManager;
    }

    public void setFatturaAttivaNotificheManager(FatturaAttivaNotificheManager fatturaAttivaNotificheManager) {
        this.fatturaAttivaNotificheManager = fatturaAttivaNotificheManager;
    }
}
