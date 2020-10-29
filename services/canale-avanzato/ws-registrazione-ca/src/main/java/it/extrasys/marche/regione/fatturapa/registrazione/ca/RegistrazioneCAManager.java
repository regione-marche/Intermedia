package it.extrasys.marche.regione.fatturapa.registrazione.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.ChiaveManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificheAttivaFromSdiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.apache.cxf.transport.http.auth.HttpAuthHeader.AUTH_TYPE_BASIC;

public class RegistrazioneCAManager {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrazioneCAManager.class);

    //HEADERS
    private static final String CODICE_UFFICIO_HEADER = "codiceUfficio";
    private static final String ADDRESS_HEADER = "address";
    private static final String WSDL_URL_HEADER = "wsdlURL";
    private static final String USERNAME_HEADER = "username";
    private static final String PASSWORD_HEADER = "password";
    private static final String INVIO_UNICO_HEADER = "invioUnico";

    private static final String TIPO_FATTURAZIONE_HEADER = "tipoFatturazione";

    private static final String TIPO_NOTIFICA_HEADER = "tipoMessaggio";
    private static final String NOTIFICA_IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String RICEVUTA_COMUNICAZIONE_HEADER = "ricevutaComunicazione";

    //MANAGER
    private EnteManager enteManager;
    private NotificheAttivaFromSdiManager notificheAttivaFromSdiManager;
    private ChiaveManager chiaveManager;

    public void setInfoEnteConfigHeaders(Exchange msgExchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {

        Message msg = msgExchange.getIn();

        String codiceUfficio = msg.getHeader(CODICE_UFFICIO_HEADER, String.class);

        EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficio);

        String tipoFatturazione = msg.getHeader(TIPO_FATTURAZIONE_HEADER, String.class);

        Boolean invioUnico = enteEntity.getInvioUnico();

        if (enteEntity == null) {
            throw new FatturaPAEnteNonTrovatoException();
        } else {

            String authorization;

            switch (tipoFatturazione) {

                //Fatturazione Passiva
                case "FP":

                    msg.setHeader(ADDRESS_HEADER, invioUnico ? enteEntity.getEndpointProtocolloCa().getEndpoint() : enteEntity.getEndpointRegistrazioneCa().getEndpoint());
                    msg.setHeader(WSDL_URL_HEADER, invioUnico ? enteEntity.getEndpointProtocolloCa().getEndpoint() + "?wsdl" : enteEntity.getEndpointRegistrazioneCa().getEndpoint() + "?wsdl");
                    msg.setHeader(USERNAME_HEADER, invioUnico ? enteEntity.getEndpointProtocolloCa().getUsername() : enteEntity.getEndpointRegistrazioneCa().getUsername());
                    msg.setHeader(INVIO_UNICO_HEADER, invioUnico);

                    String chiave = chiaveManager.getChiave();
                    String psw = invioUnico ? enteEntity.getEndpointProtocolloCa().getPassword() : enteEntity.getEndpointRegistrazioneCa().getPassword();
                    try {
                        String decryptedPsw = CommonUtils.decryptPassword(psw, chiave);
                        msg.setHeader(PASSWORD_HEADER, decryptedPsw);
                        authorization = AUTH_TYPE_BASIC + " " +
                                org.apache.cxf.common.util.Base64Utility.encode((invioUnico ? enteEntity.getEndpointProtocolloCa().getUsername() : enteEntity.getEndpointRegistrazioneCa().getUsername() + ":" + decryptedPsw).getBytes());

                        msg.setHeader(AUTHORIZATION, authorization);

                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                        throw new FatturaPAException(e.getMessage());
                    }

                    break;

                //Fatturazione Attiva
                case "FA":

                    msg.setHeader(ADDRESS_HEADER, enteEntity.getEndpointNotificheAttivaCa().getEndpoint());
                    msg.setHeader(WSDL_URL_HEADER, enteEntity.getEndpointNotificheAttivaCa().getEndpoint() + "?wsdl");
                    msg.setHeader(USERNAME_HEADER, enteEntity.getEndpointNotificheAttivaCa().getUsername());

                    chiave = chiaveManager.getChiave();
                    psw = enteEntity.getEndpointNotificheAttivaCa().getPassword();
                    try {
                        String decryptedPsw = CommonUtils.decryptPassword(psw, chiave);
                        msg.setHeader(PASSWORD_HEADER, decryptedPsw);
                        authorization = AUTH_TYPE_BASIC + " " +
                                org.apache.cxf.common.util.Base64Utility.encode((enteEntity.getEndpointNotificheAttivaCa().getUsername() + ":" + decryptedPsw).getBytes());

                        msg.setHeader(AUTHORIZATION, authorization);
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                        throw new FatturaPAException(e.getMessage());
                    }

                    break;

                default:

                    msg.setHeader(ADDRESS_HEADER, enteEntity.getEndpointRegistrazioneCa().getEndpoint());
                    msg.setHeader(WSDL_URL_HEADER, enteEntity.getEndpointRegistrazioneCa().getEndpoint() + "?wsdl");
                    msg.setHeader(USERNAME_HEADER, enteEntity.getEndpointRegistrazioneCa().getUsername());
                    msg.setHeader(INVIO_UNICO_HEADER, enteEntity.getInvioUnico());

                    chiave = chiaveManager.getChiave();
                    psw = enteEntity.getEndpointRegistrazioneCa().getPassword();
                    try {
                        String decryptedPsw = CommonUtils.decryptPassword(psw, chiave);
                        msg.setHeader(PASSWORD_HEADER, decryptedPsw);

                        authorization = AUTH_TYPE_BASIC + " " +
                                org.apache.cxf.common.util.Base64Utility.encode((enteEntity.getEndpointRegistrazioneCa().getUsername() + ":" + decryptedPsw).getBytes());

                        msg.setHeader(AUTHORIZATION, authorization);
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                        throw new FatturaPAException(e.getMessage());
                    }

                    break;
            }
        }
    }

    public void verificaBodyNotificaAttiva(Exchange msgExchange) throws UnsupportedEncodingException {

        Message msg = msgExchange.getIn();

        String body = msg.getBody(String.class);

        byte[] notificaByteArray = body.getBytes();

        if (Base64Utils.isBase64(notificaByteArray)) {
            notificaByteArray = Base64.decodeBase64(notificaByteArray);
        }

        String xml = new String(notificaByteArray);

        msg.setBody(xml);
    }

    public void salvaRicevutaComunicazione(Exchange msgExchange) throws FatturaPAException {

        Message msg = msgExchange.getIn();

        String identificaticoSdI = msg.getHeader(NOTIFICA_IDENTIFICATIVO_SDI_HEADER, String.class);
        String tipoNotifica = msg.getHeader(TIPO_NOTIFICA_HEADER, String.class);
        String ricevutaComunicazione = msg.getHeader(RICEVUTA_COMUNICAZIONE_HEADER, String.class);

        BigInteger idSdI = new BigInteger(identificaticoSdI);
        notificheAttivaFromSdiManager.salvaRicevutaComunicazioneNotificaAttivaFromSdI(idSdI, tipoNotifica, ricevutaComunicazione);
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public NotificheAttivaFromSdiManager getNotificheAttivaFromSdiManager() {
        return notificheAttivaFromSdiManager;
    }

    public void setNotificheAttivaFromSdiManager(NotificheAttivaFromSdiManager notificheAttivaFromSdiManager) {
        this.notificheAttivaFromSdiManager = notificheAttivaFromSdiManager;
    }

    public ChiaveManager getChiaveManager() {
        return chiaveManager;
    }

    public void setChiaveManager(ChiaveManager chiaveManager) {
        this.chiaveManager = chiaveManager;
    }
}