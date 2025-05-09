package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.attiva.processors;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by agosteeno on 11/08/15.
 */
public class CheckFlussoSemplificatoProcessor implements Processor{
    private static final Logger LOG = LoggerFactory.getLogger(CheckFlussoSemplificatoProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdi";

    private static final String CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER = "codiceEnteDestinatarioFlussoSemplificato";
    private static final String IS_ENTE_ADERENTE_HEADER = "isEnteAderente";
    private static final String ENTE_NON_ADERENTE = "nonAderente";
    private static final String ENTE_ADERENTE = "aderente";

    private EnteManager enteManager;

    @Override
    public void process(Exchange exchange) throws Exception{

        Message message = exchange.getIn();

        String codiceUfficioDestinatario = (String) message.getHeader(CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER);

        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        EnteEntity ente = null;
        try {
            ente = enteManager.getEnteByCodiceUfficio(codiceUfficioDestinatario);

            LOG.info("CheckFlussoSemplificatoProcessor - ENTE " + codiceUfficioDestinatario + "  aderente al servizio IntermediaMarche; identificativoSdI " + identificativoSdI);

            message.setHeader(IS_ENTE_ADERENTE_HEADER, ENTE_ADERENTE);
        } catch (FatturaPAException e) {
            throw e;
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (FatturaPAEnteNonTrovatoException e) {
            //Ente non trovato, dunque il destinatario non e' aderente al servizio intermediaMarche

            LOG.info("CheckFlussoSemplificatoProcessor - ENTE " + codiceUfficioDestinatario + " NON aderente al servizio IntermediaMarche; identificativoSdI " + identificativoSdI);

            message.setHeader(IS_ENTE_ADERENTE_HEADER, ENTE_NON_ADERENTE);
        }
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }
}
