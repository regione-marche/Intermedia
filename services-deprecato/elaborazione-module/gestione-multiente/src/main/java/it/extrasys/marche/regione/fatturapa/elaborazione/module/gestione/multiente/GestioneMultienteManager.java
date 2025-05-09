package it.extrasys.marche.regione.fatturapa.elaborazione.module.gestione.multiente;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.TipoCanaleEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 26/02/15.
 */
public class GestioneMultienteManager {

    private static final Logger LOG = LoggerFactory.getLogger(GestioneMultienteManager.class);

    private static final String COMMITTENTE_CODICE_IVA = "committenteCodiceIva";
    private static final String CODA_INVIO_ENTE = "codaInvioEnte";
    private static final String CODA_INVIO_ENTE_ATTIVA = "codaInvioEnteAttiva";
    private static final String ID_FISCALE_COMMITTENTE = "idFiscaleCommittente";
    private static final String CODICE_UFFICIO = "codiceUfficio";

    private static final String IDENTIFICATIVO_SDI = "identificativoSdi";
    private static final String ID_ENTE_DESTINATARIO = "idEnteDestinatario";

    private EnteManager enteManager;

    private FatturaAttivaManagerImpl fatturaAttivaManager;

    private static final String COD_TIPO_CANALE_HEADER = "codTipoCanale";
    private static final String TIPO_CANALE_PROTOCOLLO_HEADER = "tipoCanaleProtocollo";
    private static final String TIPO_CANALE_REGISTRAZIONE_HEADER = "tipoCanaleRegistrazione";
    private static final String ENDPOINT_PROTOCOLLO_HEADER = "endpointProtocollo";
    private static final String ENDPOINT_REGISTRAZIONE_HEADER = "endpointGestionale";
    private static final String CODA_PROTOCOLLO_INVIO_ENTE_HEADER = "codaProtocolloInvioEnte";
    private static final String CODA_REGISTRAZIONE_INVIO_ENTE_HEADER = "codaGestionaleInvioEnte";

    private static final String INFO_TIPO_INVIO_CA_HEADER = "infoTipoInvioFatturaCA";

    public void selezionaEnte(Exchange msgExchange) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {

        Message msg = msgExchange.getIn();

        String committenteCodiceIva =  (String) msg.getHeader(COMMITTENTE_CODICE_IVA);
        String codiceUfficio = (String) msg.getHeader(CODICE_UFFICIO);
        String identificativoSdi =  msg.getHeader(IDENTIFICATIVO_SDI,String.class);

        LOG.info("MULTIENTE: GestioneMultienteManager - SELEZIONA ENTE: committenteCodiceIva = " + committenteCodiceIva + ";"+" - IDENTIFICATIVO SDI "+identificativoSdi);

        String idFiscaleCommittente = "";

        idFiscaleCommittente = committenteCodiceIva;

        LOG.info("MULTIENTE: GestioneMultienteManager - SELEZIONA ENTE: idFiscaleCommittente: " + idFiscaleCommittente+ ";"+" - IDENTIFICATIVO SDI "+identificativoSdi);

        msg.setHeader(ID_FISCALE_COMMITTENTE, idFiscaleCommittente);


        LOG.info("MULTIENTE: GestioneMultienteManager - SELEZIONA ENTE: RICERCA ENTE PER codiceUfficio: " + codiceUfficio + ";"+" - IDENTIFICATIVO SDI "+identificativoSdi);

        EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficio);

        String codTipoCanale = "";

        if(enteEntity.getTipoCanale() != null)
            codTipoCanale = enteEntity.getTipoCanale().getCodTipoCanale();

        msg.setHeader(COD_TIPO_CANALE_HEADER, codTipoCanale);

        if(codTipoCanale.equals(TipoCanaleEntity.TIPO_CANALE.CA.getValue())){

            LOG.info("MULTIENTE: GestioneMultienteManager - FLUSSO CA ...");

            String tipoCanaleProtocollo = null;
            String endpointProtocollo = null;
            String codaProtocolloInvioEnte = null;

            String tipoCanaleGestionale = null;
            String endpointGestionale = null;
            String codaRegistrazioneInvioEnte = null;

           // if(enteEntity.getEndpointFromFtpCa() == null) {

                //Info per Protocollazione
                tipoCanaleProtocollo = enteEntity.getEndpointProtocolloCa().getCanaleCa().getCodCanale();
                endpointProtocollo = enteEntity.getEndpointProtocolloCa().getEndpoint();
                codaProtocolloInvioEnte = enteEntity.getCodaProtocolloCa();

                msg.setHeader(TIPO_CANALE_PROTOCOLLO_HEADER, tipoCanaleProtocollo);
                msg.setHeader(ENDPOINT_PROTOCOLLO_HEADER, endpointProtocollo);
                msg.setHeader(CODA_PROTOCOLLO_INVIO_ENTE_HEADER, codaProtocolloInvioEnte);
           // }

            //Info per Gestionale

            if(enteEntity.getEndpointRegistrazioneCa() != null) {

                tipoCanaleGestionale = enteEntity.getEndpointRegistrazioneCa().getCanaleCa().getCodCanale();
                endpointGestionale = enteEntity.getEndpointRegistrazioneCa().getEndpoint();
                codaRegistrazioneInvioEnte = enteEntity.getCodaGestionaleCa();

                msg.setHeader(TIPO_CANALE_REGISTRAZIONE_HEADER, tipoCanaleGestionale);
                msg.setHeader(ENDPOINT_REGISTRAZIONE_HEADER, endpointGestionale);
                msg.setHeader(CODA_REGISTRAZIONE_INVIO_ENTE_HEADER, codaRegistrazioneInvioEnte);
            }

            if(tipoCanaleGestionale == null){
                //************   WS || PEC || FTP   ************
                if(tipoCanaleProtocollo.equals("003") || tipoCanaleProtocollo.equals("004") || tipoCanaleProtocollo.equals("005")){
                    //Sono nel caso di invio singolo
                    msg.setHeader(INFO_TIPO_INVIO_CA_HEADER, TipoInvioFatturaCA.INVIO_SINGOLO.getValue());
                }else{
                    throw new FatturaPAException("CA: ENDPOINT GESTIONALE NULLO!!!");
                }
            }else{
                msg.setHeader(INFO_TIPO_INVIO_CA_HEADER, TipoInvioFatturaCA.PROTOCOLLAZIONE.getValue());
            }
        }else{

            LOG.info("MULTIENTE: GestioneMultienteManager - FLUSSO CUSTOM ...");

            String codaEnte = enteEntity.getCodaInvioEnte();
            msg.setHeader(CODA_INVIO_ENTE, codaEnte);
        }
    }

    public void selezioneEnteAttiva(Exchange exchange) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        Message msg = exchange.getIn();

        String identificativoSdi = (String) msg.getHeader(IDENTIFICATIVO_SDI);

        FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManager.getFatturaAttivaFromIdentificativSdi(new BigInteger(identificativoSdi));

        EnteEntity enteEntity = fatturaAttivaEntity.getEnte();

        String codaEnteAttiva = enteEntity.getCodaInvioEnteAttiva();

        String idEnte = enteEntity.getIdEnte().toString();
        msg.setHeader(ID_ENTE_DESTINATARIO, idEnte);
        msg.setHeader(CODA_INVIO_ENTE_ATTIVA, codaEnteAttiva);
        msg.setHeader(CODICE_UFFICIO, enteEntity.getCodiceUfficio());
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }
}