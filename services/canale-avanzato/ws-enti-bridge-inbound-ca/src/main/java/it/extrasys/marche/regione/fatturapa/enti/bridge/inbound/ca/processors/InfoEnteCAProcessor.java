package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPACredenzialiNonValideException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.TipoCanaleEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.ChiaveManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.apache.cxf.transport.http.auth.HttpAuthHeader.AUTH_TYPE_BASIC;

public class InfoEnteCAProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(InfoEnteCAProcessor.class);

    private final String CODICE_UFFICIO_HEADER = "codiceUfficio";
    private final String CODA_PROTOCOLLO_CA_IN_HEADER = "codaProtocolloCAIn";
    private final String CODA_REGISTRAZIONE_CA_IN_HEADER = "codaGestionaleCaIn";

    private final String ENTE_EX_HEADER = "enteEX";
    private final String ENTE_CREDENZIALE_EX_HEADER = "enteCredenzialiEX";

    private EnteManager enteManager;
    private ChiaveManager chiaveManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        msg.setHeader(ENTE_EX_HEADER, false);
        msg.setHeader(ENTE_CREDENZIALE_EX_HEADER, false);

        MessageContentsList message = msg.getBody(MessageContentsList.class);

        EsitoFatturaMessageRequest esitoFatturaMessageRequest = (EsitoFatturaMessageRequest) message.get(0);

        String codiceUfficio = esitoFatturaMessageRequest.getEsitoFatturaMessage().getCodUfficio();

        if(codiceUfficio == null || "".equals(codiceUfficio)){
            msg.setHeader(ENTE_EX_HEADER, true);
            throw new FatturaPAEnteNonTrovatoException();

        }else {

            EnteEntity enteEntity = null;

            try{
                enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficio);
            }catch (Exception e){
                //Ente non trovato
            }

            if(enteEntity == null) {
                msg.setHeader(ENTE_EX_HEADER, true);
                throw new FatturaPAEnteNonTrovatoException();
            }

            if(!enteEntity.getTipoCanale().getCodStato().equals(TipoCanaleEntity.TIPO_CANALE.CA)) {
                msg.setHeader(ENTE_EX_HEADER, true);
                throw new FatturaPAEnteNonTrovatoException();
            }

            String authorizationHeader = msg.getHeader(AUTHORIZATION, String.class);

            if(authorizationHeader == null || "".equals(authorizationHeader)) {
                msg.setHeader(ENTE_CREDENZIALE_EX_HEADER, true);
                throw new FatturaPACredenzialiNonValideException();
            }

            if(!checkEnte(enteEntity, authorizationHeader)) {
                msg.setHeader(ENTE_CREDENZIALE_EX_HEADER, true);
                throw new FatturaPACredenzialiNonValideException();
            }

            String codaProtocolloCAIn = enteEntity.getCodaProtocolloCa();
            String codaGestionaleCaIn = enteEntity.getCodaGestionaleCa();

            msg.setHeader(CODICE_UFFICIO_HEADER, codiceUfficio);
            msg.setHeader(CODA_PROTOCOLLO_CA_IN_HEADER, codaProtocolloCAIn);
            msg.setHeader(CODA_REGISTRAZIONE_CA_IN_HEADER, codaGestionaleCaIn);
        }
    }

    private boolean checkEnte(EnteEntity enteEntity, String authorizationHeader){

        boolean checkEnte = false;

        try{

            String psw = enteEntity.getEndpointEsitoCommittenteCa().getPassword();
            String chiave = chiaveManager.getChiave();
            String decryptedPsw = CommonUtils.decryptPassword(psw, chiave);

            String authorization = AUTH_TYPE_BASIC + " " +
                    org.apache.cxf.common.util.Base64Utility.encode((enteEntity.getEndpointEsitoCommittenteCa().getUsername() + ":" + decryptedPsw).getBytes());

            if(authorization.equals(authorizationHeader)) {
                checkEnte = true;
            }

        }catch (Exception e){
            checkEnte = false;
        }

        return checkEnte;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public ChiaveManager getChiaveManager() {
        return chiaveManager;
    }

    public void setChiaveManager(ChiaveManager chiaveManager) {
        this.chiaveManager = chiaveManager;
    }
}