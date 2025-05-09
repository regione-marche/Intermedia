package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.api.rest.model.*;
import it.extrasys.marche.regione.fatturapa.api.rest.utils.Constants;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.TestCicloAttivoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.TestCicloPassivoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.*;
import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.BadRequestException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntitiesResourceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(EntitiesResourceImpl.class);

    private EnteManager enteManager;

    private UtentiManager utentiManager;

    private TestManager testManager;

    private DatiFatturaManager datiFatturaManager;

    private String durataToken;

    private FatturaAttivaManagerImpl fatturaAttivaManager;

    private CodificaStati2Manager codificaStati2Manager;

    private FatturaAttivaNotificheManager fatturaAttivaNotificheManager;

    private CampoOpzionaleFatturaManager campoOpzionaleFatturaManager;

    private NotificaFromEntiManager notificaFromEntiManager;

    private NotificheAttivaFromSdiManager notificheAttivaFromSdiManager;

    /**
     * Servizio utilizzato per ricercare un ente
     */
    public EntitiesResponseList servizioEntitiesGET(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPATokenNonValidoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        //Dalla GUI arriverà uno solo dei primi 3
        String denominazioneEnte = (String) parameters.get(0);
        String idFiscaleCommittente = (String) parameters.get(1);
        String codiceUfficio = (String) parameters.get(2);
        String token = (String) parameters.get(3);

        List<EnteEntity> enteEntityList = null;

        LOG.info("*************** servizioEntitiesGet() denominazionEnte: " + denominazioneEnte + " idFiscaleCommittente: " + idFiscaleCommittente + " codiceUfficio: " + codiceUfficio + " ***************");

        EntitiesResponse entitiesResponse;
        EntitiesResponseList entitiesResponseList = new EntitiesResponseList();

        try {

            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);

            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(usernameUtente);

            if (denominazioneEnte != null) {
                enteEntityList = enteManager.getEnteByDenominazioneEnte(denominazioneEnte, usernameUtente);
                UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
                utenteEnteEntity.setUtente(utenteEntity);
                utenteEnteEntity.setEnte(enteEntityList.get(0));
                List<EnteEntity> tmp = new ArrayList<>();
                for(EnteEntity enteEntity : enteEntityList){
                    utenteEnteEntity.setEnte(enteEntity);
                    if(utentiManager.checkAuth(utenteEnteEntity)){
                        tmp.add(enteEntity);
                    }
                }
                if(tmp == null || tmp.size() == 0){
                    throw new FatturaPAUtenteNonAutorizzatoException();
                }
                enteEntityList = tmp;
                for (EnteEntity enteEntity : enteEntityList) {
                    entitiesResponse = FunzioniConversione.enteEntityToEntityResp(enteEntity);
                    entitiesResponseList.addElement2List(entitiesResponse);
                }

                return entitiesResponseList;

            } else if (idFiscaleCommittente != null) {
                enteEntityList = enteManager.getEnteByIdFiscaleCommittente(idFiscaleCommittente, usernameUtente);
                UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
                utenteEnteEntity.setUtente(utenteEntity);
                utenteEnteEntity.setEnte(enteEntityList.get(0));
                if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                    throw new FatturaPAUtenteNonAutorizzatoException();
                }
                for (EnteEntity enteEntity : enteEntityList) {
                    entitiesResponse = FunzioniConversione.enteEntityToEntityResp(enteEntity);
                    entitiesResponseList.addElement2List(entitiesResponse);
                }

                return entitiesResponseList;
            } else if (codiceUfficio != null) {
                EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficio);
                UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
                utenteEnteEntity.setUtente(utenteEntity);
                utenteEnteEntity.setEnte(enteEntity);
                if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                    throw new FatturaPAUtenteNonAutorizzatoException();
                }
                entitiesResponse = FunzioniConversione.enteEntityToEntityResp(enteEntity);
                entitiesResponseList.addElement2List(entitiesResponse);
                return entitiesResponseList;
            } else {
                enteEntityList = enteManager.getEnteByUser(usernameUtente);
                for (EnteEntity enteEntity : enteEntityList) {
                    entitiesResponse = FunzioniConversione.enteEntityToEntityResp(enteEntity);
                    entitiesResponseList.addElement2List(entitiesResponse);
                }

                return entitiesResponseList;
            }

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPaPersistenceException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }

    }

    /**
     * Servizio utilizzato per aggiornare la configurazione di un ente
     */
    public EntitiesIdResponse servizioEntitiesPUT(Exchange exchange) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPACanaleNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPATokenNonValidoException {

        String codiceUff = "";

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        EntitiesRequestPut body = (EntitiesRequestPut) parameters.get(0);
        String token = (String) parameters.get(1);

        LOG.info("*************** servizioEntitiesPUT ***************");

        try {

            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);
            codiceUff = body.getCodiceUfficio();
            BigInteger idEnte = enteManager.updateEnteById(body);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(idEnte);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            return idResponse;

        } catch (FatturaPAException e) {
            if (e.getMessage().equals("codice ufficio " + codiceUff + "già associato ad un altro ente")) {
                LOG.error("*************** BAD REQUEST EXCEPTION, CODICE UFFICIO GIÀ PRESENTE ***************");
                LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
                throw new BadRequestException(e.getMessage());
            } else {
                LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
                throw e;
            }
        } catch (FatturaPAEnteNonTrovatoException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPACanaleNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }

    }

    /**
     * Servizio utilizzato per creare un nuovo ente
     */
    public EntitiesIdResponse servizioEntitiesPOST(Exchange exchange) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPACanaleNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        EntitiesRequest body = (EntitiesRequest) parameters.get(0);
        String token = (String) parameters.get(1);

        LOG.info("*************** servizioEntitiesPOST ***************");

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);

            if (body.getCodiceUfficio() == null || body.getCampiOpzionali() == null || body.getCicloAttivo() == null ||
                    body.getCicloPassivo() == null || body.getAmbienteCicloAttivo() == null ||
                    body.getAmbienteCicloPassivo() == null || body.getIdFiscaleCommittente() == null || body.getTipoCanale() == null) {
                throw new BadRequestException();
            }

            BigInteger idEnte = enteManager.insertEnteEntity(body);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(idEnte);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            exchange.getIn().setHeader("tipoCanale", body.getTipoCanale());

            return idResponse;
        } catch (FatturaPaPersistenceException e) {
            if (e.getMessage().equals("Ente con codice ufficio " + body.getCodiceUfficio() + " già esistente")) {
                LOG.error("*************** BAD REQUEST EXCEPTION, CODICE UFFICIO GIÀ PRESENTE ***************");
                LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
                throw new BadRequestException();
            } else {
                LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
                throw e;
            }
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di ricezione delle fatture
     */
    public ACinvoicesResponse servizioEntitiesActiveCycleRicezioneFattureGET(Exchange exchange) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPATokenNonValidoException, FatturaPAConfigurazioneNonTrovataException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCycleRicezioneFattureGET **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        EnteEntity enteEntity = null;

        try {

            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);

            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(usernameUtente);

            enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            if (!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("Errore tipo canale");
            }
            ACinvoicesResponse acInvRes = FunzioniConversione.enteEntityToAcinvoicesResp(enteEntity);
            if (acInvRes.getPassword() != null) {
                String encPsw = acInvRes.getPassword();
                acInvRes.setPassword(utentiManager.decryptPassword(encPsw));
            }
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            return acInvRes;

        } catch (FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAConfigurazioneNonTrovataException
                | FatturaPAException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione del sistema di ricezione delle fatture
     */
    public EntitiesIdResponse servizioEntitiesActiveCycleRicezioneFatturePUT(Exchange exchange) throws FatturaPAUtenteNonTrovatoException, FatturaPACanaleNonTrovatoException, FatturaPAException, FatturaPAUtenteNonAutorizzatoException, FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCycleRicezioneFatturePUT **********");

        Integer idEnte = (Integer) parameters.get(0);
        ACinvoicesRequest body = (ACinvoicesRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);
            enteManager.servizioEntitiesActiveCycleRicezioneFatturePUT(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAUtenteNonAutorizzatoException
                | FatturaPATokenNonValidoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per configurare l’integrazione del sistema di ricezione delle fatture
     */
    public EntitiesIdResponse servizioEntitiesActiveCycleRicezioneFatturePOST(Exchange exchange) throws FatturaPAUtenteNonTrovatoException, FatturaPACanaleNonTrovatoException, FatturaPAException, FatturaPAUtenteNonAutorizzatoException, FatturaPAEnteNonTrovatoException, FatturaPATokenNonValidoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCycleRicezioneFatturePOST **********");

        Integer idEnte = (Integer) parameters.get(0);
        ACinvoicesRequest body = (ACinvoicesRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);
            enteManager.servizioEntitiesActiveCycleRicezioneFatturePOST(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAUtenteNonAutorizzatoException
                | FatturaPAException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di inoltro delle notifiche
     */
    public ACnotificationsResponse servizioEntitiesActiveCycleInoltroNotificheGET(Exchange exchange) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAConfigurazioneNonTrovataException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCycleInoltroNotificheGET **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        EnteEntity enteEntity = null;

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(usernameUtente);

            enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            if (!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("Errore tipo canale");
            }

            ACnotificationsResponse aCnotificationsResponse = FunzioniConversione.enteEntityToACnotificationsResponse(enteEntity);
            if (aCnotificationsResponse.getPassword() != null) {
                String encPsw = aCnotificationsResponse.getPassword();
                aCnotificationsResponse.setPassword(utentiManager.decryptPassword(encPsw));
            }
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            return aCnotificationsResponse;

        } catch ( FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAConfigurazioneNonTrovataException
                | FatturaPAException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | BadPaddingException
                | IllegalBlockSizeException
                | InvalidKeyException
                | NoSuchAlgorithmException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione del sistema di inoltro delle notifiche
     */
    public EntitiesIdResponse servizioEntitiesActiveCycleInoltroNotifichePUT(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPACanaleNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCycleInoltroNotifichePUT **********");

        Integer idEnte = (Integer) parameters.get(0);
        ACnotificationsRequest body = (ACnotificationsRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);
            enteManager.servizioEntitiesActiveCycleInoltroNotifichePUT(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAUtenteNonAutorizzatoException
                | FatturaPATokenNonValidoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per configurare l’integrazione del sistema di inoltro delle notifiche
     */
    public EntitiesIdResponse servizioEntitiesActiveCycleInoltroNotifichePOST(Exchange exchange) throws FatturaPAUtenteNonAutorizzatoException, FatturaPAException, FatturaPACanaleNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCycleInoltroNotifichePOST **********");

        Integer idEnte = (Integer) parameters.get(0);
        ACnotificationsRequest body = (ACnotificationsRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);
            enteManager.servizioEntitiesActiveCycleInoltroNotifichePOST(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAUtenteNonAutorizzatoException
                | FatturaPAException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per gestire il passaggio dall’ambiente di staging all’ambiente di produzione del Ciclo Attivo
     */
    public EntitiesIdResponse servizioEntitiesActiveCyclePassaggioProduzionePUT(Exchange exchange) throws FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCyclePassaggioProduzionePUT **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            enteManager.servizioEntitiesActiveCyclePassaggioProduzionePUT(idEnte, username);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }

    /**
     * Servizio utilizzato per recuperare lo storico dei test effettuati nel ciclo attivo
     */
    public TestACHistoryDto servizioEntitiesActiveCycleStoricoTestGET(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaTestCicloNonTrovatoException, FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCycleStoricoTestGET **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);
        TestACHistoryDto testACHistoryDto = new TestACHistoryDto();
        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            List<TestCicloAttivoEntity> testCicloAttivoEntityList = testManager.getTestCicloAttivoByEnte(enteEntity);
            testACHistoryDto.setCodiceUfficio(enteEntity.getCodiceUfficio());
            List<ACTestDto> acTestDtoList = new ArrayList<>();
            for(TestCicloAttivoEntity testCicloAttivoEntity : testCicloAttivoEntityList){
                ACTestDto acTestDto = new ACTestDto();
                acTestDto.setDataTest(testCicloAttivoEntity.getDataTest());
                acTestDto.setIdentificativoSdi(testCicloAttivoEntity.getIdentificativoSdi().toString());
                acTestDto.setNomeFile(testCicloAttivoEntity.getNomeFile());
                acTestDto.setRicevutaComunicazione(testCicloAttivoEntity.getRicevutaComunicazione());
                List<FatturaAttivaEntity> fatturaAttivaEntityList;
                try {
                    fatturaAttivaEntityList = fatturaAttivaManager.getFatturaAttivaListFromIdentificativoSdiTest(testCicloAttivoEntity.getIdentificativoSdi());
                }
                catch(FatturaPAFatturaNonTrovataException e){
                    fatturaAttivaEntityList = null;
                }
                if(fatturaAttivaEntityList != null && fatturaAttivaEntityList.size() > 0) {
                    FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaEntityList.get(0);
                    List<StatoFatturaAttivaEntity> statoFatturaAttivaEntityList = fatturaAttivaManager.getStatiFatturaAttivaList(fatturaAttivaEntity.getIdFatturaAttiva());
                    if(statoFatturaAttivaEntityList != null && statoFatturaAttivaEntityList.size() > 0) {
                        acTestDto.setFatturaAttiva(true);
                    }
                    else {
                        acTestDto.setFatturaAttiva(null);
                    }
                    FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegnaEntity = fatturaAttivaNotificheManager.getFatturaAttivaRicevutaConsegnaFatturaAttiva(fatturaAttivaEntity);
                    List<StatoAttivaRicevutaConsegnaEntity> statoAttivaRicevutaConsegnaEntityList = fatturaAttivaNotificheManager.getStatoRicevutaConsegnaFromRicevutaConsegna(fatturaAttivaRicevutaConsegnaEntity);
                    if (statoAttivaRicevutaConsegnaEntityList != null && statoAttivaRicevutaConsegnaEntityList.size() != 0){
                        StatoAttivaRicevutaConsegnaEntity statoAttivaRicevutaConsegnaEntity = statoAttivaRicevutaConsegnaEntityList.get(0);
                        String cod2 = statoAttivaRicevutaConsegnaEntity.getStato().getCodStato().getValue();
                        if (cod2.equalsIgnoreCase("002")) {
                            acTestDto.setRicevutaConsegna(true);
                        } else {
                            acTestDto.setRicevutaConsegna(false);
                        }
                        NotificheAttivaFromSdiEntity notificheAttivaFromSdi = notificheAttivaFromSdiManager.getNotificaAttivaFromIdSdi(new BigInteger(acTestDto.getIdentificativoSdi()));
                        acTestDto.setNomeFileRC(notificheAttivaFromSdi.getNomeFile());
                    }
                    else {
                        acTestDto.setRicevutaConsegna(null);
                    }
                }
                else {
                    acTestDto.setRicevutaConsegna(false);
                    acTestDto.setFatturaAttiva(false);
                }
                acTestDtoList.add(acTestDto);

            }
            testACHistoryDto.setListaTest(acTestDtoList);
            return testACHistoryDto;
        } catch (FatturaPAException | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPaTestCicloNonTrovatoException
                | FatturaPAFatturaNonTrovataException
                | FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

    /**
     * Servizio utilizzato per effettuare test del ciclo attivo
     */
    public String servizioEntitiesActiveCycleTestRunPOST(Exchange exchange) {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesActiveCycleTestRunPOST **********");

        Integer idEnte = (Integer) parameters.get(0);
        TestRunRequest body = (TestRunRequest) parameters.get(1);

        return null;
    }

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di ricezione esito committente
     */
    public PCesitoCommittenteResponse servizioEntitiesPassiveCycleEsitoCommittenteGET(Exchange exchange) throws FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAException, FatturaPaPersistenceException, FatturaPAConfigurazioneNonTrovataException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleEsitoCommittenteGET **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(usernameUtente);
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            PCesitoCommittenteResponse pc = FunzioniConversione.enteEntityToPCesitoCommittenteResponse(enteEntity);
            String encPsw = pc.getPassword();
            if (encPsw != null) {
                pc.setPassword(utentiManager.decryptPassword(encPsw));
            }
            return pc;
        } catch (FatturaPAException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAConfigurazioneNonTrovataException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | BadPaddingException
                | IllegalBlockSizeException
                | InvalidKeyException
                | NoSuchAlgorithmException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione del sistema di ricezione esito committente
     */
    public EntitiesIdResponse servizioEntitiesPassiveCycleEsitoCommittentePUT(Exchange exchange) throws FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAException, FatturaPACanaleNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleEsitoCommittentePUT **********");

        Integer idEnte = (Integer) parameters.get(0);
        PCesitoCommittenteRequest body = (PCesitoCommittenteRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsername(usernameUtente);
            enteManager.servizioEntitiesPassiveCycleEsitoCommittentePUT(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;

        } catch (FatturaPAException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchAlgorithmException
                | InvalidKeyException
                | NoSuchPaddingException
                | BadPaddingException
                | IllegalBlockSizeException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per configurare l’integrazione del sistema di ricezione esito committente
     */
    public EntitiesIdResponse servizioEntitiesPassiveCycleEsitoCommittentePOST(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAEnteNonTrovatoException, FatturaPACanaleNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleEsitoCommittentePOST **********");

        Integer idEnte = (Integer) parameters.get(0);
        PCesitoCommittenteRequest body = (PCesitoCommittenteRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsername(usernameUtente);
            enteManager.servizioEntitiesPassiveCycleEsitoCommittentePOST(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchAlgorithmException
                | InvalidKeyException
                | NoSuchPaddingException
                | BadPaddingException
                | IllegalBlockSizeException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }
    }

    /**
     * Servizio utilizzato per gestire il passaggio dall’ambiente di staging all’ambiente di produzione
     */
    public EntitiesIdResponse servizioEntitiesPassiveCyclePassaggioProduzionePUT(Exchange exchange) throws FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCyclePassaggioProduzionePUT **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            enteManager.servizioEntitiesPassiveCyclePassaggioProduzionePUT(idEnte, username);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di protocollo
     */
    public PCprotocolResponse servizioEntitiesPassiveCycleProtocolloGET(Exchange exchange) throws FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPaPersistenceException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAConfigurazioneNonTrovataException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleProtocolloGET **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(usernameUtente);
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            PCprotocolResponse pc = FunzioniConversione.enteEntityToPCprotocolResponse(enteEntity);
            String encPsw = pc.getPassword();
            if (encPsw != null) {
                pc.setPassword(utentiManager.decryptPassword(encPsw));
            }
            return pc;
        } catch ( FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAConfigurazioneNonTrovataException
                | FatturaPAException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | BadPaddingException
                | IllegalBlockSizeException
                | InvalidKeyException
                | NoSuchAlgorithmException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione con il sistema di protocollo
     */
    public EntitiesIdResponse servizioEntitiesPassiveCycleProtocolloPUT(Exchange exchange) throws FatturaPACanaleNonTrovatoException, FatturaPAException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleProtocolloPUT **********");

        Integer idEnte = (Integer) parameters.get(0);
        PCprotocolRequest body = (PCprotocolRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);
            enteManager.servizioEntitiesPassiveCycleProtocolloPUT(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per configurare l’integrazione con il sistema di protocollo
     */
    public EntitiesIdResponse servizioEntitiesPassiveCycleProtocolloPOST(Exchange exchange) throws FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAException, FatturaPACanaleNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleProtocolloPOST **********");

        Integer idEnte = (Integer) parameters.get(0);
        PCprotocolRequest body = (PCprotocolRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);

            enteManager.servizioEntitiesPassiveCycleProtocolloPOST(idEnte, body);

            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;

        } catch (FatturaPAException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }
    }

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di registrazione
     */
    public PCregistrationResponse servizioEntitiesPassiveCycleRegistrazioneGET(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException, BadPaddingException, IllegalBlockSizeException, FatturaPAConfigurazioneNonTrovataException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleRegistrazioneGET **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        String usernameUtente = null;
        try {
            usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(usernameUtente);
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            PCregistrationResponse pc = FunzioniConversione.enteEntityToPCregistrationResponse(enteEntity);
            String pswEnc = pc.getPassword();
            if (pswEnc != null) {
                pc.setPassword(utentiManager.decryptPassword(pswEnc));
            }
            return pc;
        } catch (FatturaPAException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException
                | FatturaPAConfigurazioneNonTrovataException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | BadPaddingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione del sistema di registrazione
     */
    public EntitiesIdResponse servizioEntitiesPassiveCycleRegistrazionePUT(Exchange exchange) throws FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAException, FatturaPACanaleNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleRegistrazionePUT **********");

        Integer idEnte = (Integer) parameters.get(0);
        PCregistrationRequest body = (PCregistrationRequest) parameters.get(1);
        String token = (String) parameters.get(2);

        String usernameUtente;

        try {
            usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);
            enteManager.servizioEntitiesPassiveCycleRegistrazionePUT(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }


    }

    /**
     * Servizio utilizzato per configurare l’integrazione del sistema di registrazione
     */
    public EntitiesIdResponse servizioEntitiesPassiveCycleRegistrazionePOST(Exchange exchange) throws FatturaPACanaleNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleRegistrazionePOST **********");

        Integer idEnte = (Integer) parameters.get(0);
        PCregistrationRequest body = (PCregistrationRequest) parameters.get(1);

        String token = (String) parameters.get(2);

        String usernameUtente;

        try {
            usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            body.setUsernameUtente(usernameUtente);
            enteManager.servizioEntitiesPassiveCycleRegistrazionePOST(idEnte, body);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            EntitiesIdResponse idResponse = new EntitiesIdResponse();
            idResponse.setIdEnte(BigInteger.valueOf(idEnte));
            return idResponse;
        } catch (FatturaPAException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }
    }

    /**
     * Servizio utilizzato per recuperare lo storico dei test effettuati nel ciclo passivo
     */
    public TestPCHistoryDto servizioEntitiesPassiveCycleStoricoTestGET(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaTestCicloNonTrovatoException, FatturaPaPersistenceException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioEntitiesPassiveCycleStoricoTestGET **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        TestPCHistoryDto testPCHistoryDto = new TestPCHistoryDto();

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            boolean invioUnico = enteEntity.getInvioUnico();
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            List<TestCicloPassivoEntity> testCicloPassivoEntityList = testManager.getTestCicloPassivoByEnte(enteEntity);
            testPCHistoryDto.setCodiceUfficio(enteEntity.getCodiceUfficio());
            List<PCTestDto> pcTestDtoList = new ArrayList<>();
            for(TestCicloPassivoEntity testCicloPassivoEntity : testCicloPassivoEntityList){
                PCTestDto pcTestDto = new PCTestDto();
                pcTestDto.setIdentificativoSdi(testCicloPassivoEntity.getIdentificativoSdi().toString());
                pcTestDto.setUsername(testCicloPassivoEntity.getUtente().getUsername());
                pcTestDto.setDataTest(testCicloPassivoEntity.getDataTest());
                pcTestDto.setNomeFile(testCicloPassivoEntity.getNomeFile());
                List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDITest(testCicloPassivoEntity.getIdentificativoSdi());
                if(datiFatturaEntityList != null && datiFatturaEntityList.size() > 0) {
                    DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);
                    StatoFatturaEntity statoFatturaEntity = datiFatturaManager.getUltimoStatoFattura(datiFatturaEntity);
                    if (statoFatturaEntity == null) {
                        if (invioUnico) {
                            pcTestDto.setInvioUnico(false);
                        } else {
                            pcTestDto.setProtocollo(false);
                            pcTestDto.setRegistrazione(false);
                        }
                        LOG.error("NESSUNO STATO PER IDENTIFICATIVO SDI " + pcTestDto.getIdentificativoSdi());
                    } else {
                        Date dataStato = statoFatturaEntity.getData();
                        CodificaStati2Entity codificaStati2Entity = codificaStati2Manager.getByIdCodStato(statoFatturaEntity.getStato().getCodStato().getValue());
                        String stato = codificaStati2Entity.getDescStato();
                        if (isUltimoStato(stato)) {
                            setPCTestDtoFromLastState(pcTestDto, stato, invioUnico);
                        } else {
                            if (invioUnico) {
                                pcTestDto.setInvioUnico(false);
                                pcTestDto.setProtocollo(null);
                                pcTestDto.setRegistrazione(null);
                            } else {
                                pcTestDto.setInvioUnico(null);
                                pcTestDto.setProtocollo(false);
                                pcTestDto.setRegistrazione(false);
                            }
                        }
                        if(pcTestDto.getDataEsitoCommittente() != null) {
                            pcTestDto.setDataEsitoCommittente(dataStato);
                            String nomeFileEc = notificaFromEntiManager.getNomeFileECFromIdentificativoSdi(testCicloPassivoEntity.getIdentificativoSdi());
                            if(nomeFileEc != null){
                                pcTestDto.setNomeFileEsitoCommittente(nomeFileEc);
                            }
                        }
                    }

                }
                else {
                    if (invioUnico) {
                        pcTestDto.setInvioUnico(false);
                        pcTestDto.setProtocollo(null);
                        pcTestDto.setRegistrazione(null);
                    } else {
                        pcTestDto.setInvioUnico(null);
                        pcTestDto.setProtocollo(false);
                        pcTestDto.setRegistrazione(false);
                    }
                }
                pcTestDtoList.add(pcTestDto);
            }
            testPCHistoryDto.setListaTest(pcTestDtoList);
            return testPCHistoryDto;
        } catch (FatturaPAException | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPaTestCicloNonTrovatoException
                | FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

    /**
     * Servizio utilizzato per effettuare test del ciclo passivo
     */
    public void servizioEntitiesPassiveCycleTestRunPOST(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPATokenNonValidoException, FatturaPaPersistenceException, FatturaPaTestCicloNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.debug("********** servizioEntitiesPassiveCycleTestRunPOST **********");

        String token = (String) parameters.get(1);

        String username;
        try {
            Integer idEnte = (Integer) parameters.get(0);
            username = Autenticazione.checkToken(token, utentiManager, durataToken);
            EnteEntity ente = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(ente);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //Recupera il MAX identificativo_sdi già utilizzato per i test
            BigInteger maxIdentificativoSdi = datiFatturaManager.getMaxIdentificativoSdiTest();

            BigInteger identificativoSdi = maxIdentificativoSdi.add(BigInteger.ONE);

            String numeroSequenza = generaNumeroSequenza(identificativoSdi.longValue());
           // String nomeFileMetadati = Constants.NOME_FILE_METADATI.replace(Constants.NUMERO_SEQUENZA, numeroSequenza);
           // String nomeFileFattura = Constants.NOME_FILE_FATTURA.replace(Constants.NUMERO_SEQUENZA, numeroSequenza);

            String nomeFileMetadati = ente.getIdFiscaleCommittente().concat("_").concat(numeroSequenza).concat("_MT_001.xml");
            String nomeFileFattura = ente.getIdFiscaleCommittente().concat("_").concat(numeroSequenza).concat(".xml");
            String idCodice = ente.getIdFiscaleCommittente().substring(2); //Toglie 'IT'

            testManager.salvaTestCicloPassivo(nomeFileFattura, identificativoSdi, ente.getCodiceUfficio(), username);

            exchange.getIn().setHeader("idCodice", idCodice);
            exchange.getIn().setHeader("nomeFile", nomeFileFattura);
            exchange.getIn().setHeader("nomeFileMetadati", nomeFileMetadati);
            exchange.getIn().setHeader("codiceUfficio", ente.getCodiceUfficio());
            exchange.getIn().setHeader("tipoMessaggio", "FatturaElettronica");
            exchange.getIn().setHeader("identificativoSdI", identificativoSdi);
            exchange.getIn().setBody(identificativoSdi.intValue());
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

    private String generaNumeroSequenza(Long identificativoSdi) {
        //Se l'identificativoSdi supera 99999 prendo gli ultimi 5 caratteri
        if (identificativoSdi > 100000) {
            String identificativoSdiString = identificativoSdi + "";
            return identificativoSdiString.substring(identificativoSdiString.length() - 5);
        } else {
            return StringUtils.leftPad(identificativoSdi + "", 5, "0");
        }
    }

    public TipoCanaleCaList getAllTipoCanaleCa(Exchange exchange) throws FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPATokenNonValidoException, FatturaPAException, FatturaPACanaleNonTrovatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getAllTipoCanaleCa **********");

        String token = (String) parameters.get(0);

        try {
            Autenticazione.checkToken(token, utentiManager, durataToken);

            return enteManager.getAllTipoCanaleCa();

        } catch (FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

    public ConfigPassiveCycleDTO getConfigPassiveCycle(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getConfigPassiveCycle **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            return FunzioniConversione.enteEntityToConfigPassiveCycleDTO(enteEntity);

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

    public ConfigActiveCycleDTO getConfigActiveCycle(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getConfigActiveCycle **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            return FunzioniConversione.enteEntityToConfigActiveCycleDTO(enteEntity);

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }


    public CampiOpzionaliDto getAllCampiOpzionali(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getAllCampiOpzionali **********");

        String token = (String) parameters.get(0);

        CampiOpzionaliDto campiOpzionaliDto = new CampiOpzionaliDto();

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);

            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity);

            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            List<CampoOpzionaleFatturaEntity> campoOpzionaleFatturaEntityList = campoOpzionaleFatturaManager.getAll();

            return FunzioniConversione.campoOpzionaleFatturaToCampiOpzionaliDto(campoOpzionaleFatturaEntityList);


        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }


    public CampiOpzionaliDto getAllCampiOpzionaliEnte(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAAssociazioneEnteCampoOpzionaleNonTrovataException {
        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getAllCampiOpzionaliEnte **********");

        Integer idEnte = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            List<EnteCampoOpzionaleAssociazioneEntity> associazioniFromEnte = campoOpzionaleFatturaManager.getAssociazioniFromEnte(enteEntity);
            if(associazioniFromEnte != null){
                List<CampoOpzionaleFatturaEntity> campi = new ArrayList<>();
                for(EnteCampoOpzionaleAssociazioneEntity associazioneEntity : associazioniFromEnte){
                    campi.add(associazioneEntity.getCampoOpzionale());
                }
                return FunzioniConversione.campoOpzionaleFatturaToCampiOpzionaliDto(campi);
            }
            else{
                throw new FatturaPAAssociazioneEnteCampoOpzionaleNonTrovataException();
            }

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }

    public EntitiesIdResponse updateCampiOpzionaliEnte(Exchange exchange) throws FatturaPAUtenteNonTrovatoException, FatturaPaPersistenceException, FatturaPAException, FatturaPACampoOpzionaleNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAEnteNonTrovatoException, FatturaPATokenNonValidoException {
        return setCampiOpzionaliEnte(exchange);
    }

    public EntitiesIdResponse setCampiOpzionaliEnte(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException, FatturaPACampoOpzionaleNonTrovatoException {
        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** setCampiOpzionaliEnte **********");

        Integer idEnte = (Integer) parameters.get(0);
        CampiOpzionaliDto body = (CampiOpzionaliDto) parameters.get(1);
        String token = (String) parameters.get(2);

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity);
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            EnteEntity enteEntity = enteManager.getEnteById(BigInteger.valueOf(idEnte));
            campoOpzionaleFatturaManager.deleteAllEnteCampoOpzionaleAssociazioni(enteEntity);
            for(String campo : body.getCampi()){
                EnteCampoOpzionaleAssociazioneEntity associazioneEntity = new EnteCampoOpzionaleAssociazioneEntity();
                associazioneEntity.setEnte(enteEntity);
                associazioneEntity.setCampoOpzionale(campoOpzionaleFatturaManager.getCampoOpzionaleFromDescCampo(campo));
                campoOpzionaleFatturaManager.createEnteCampoOpzionaleAssociazione(associazioneEntity);
            }
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPACampoOpzionaleNonTrovatoException
                | FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

        EntitiesIdResponse entitiesIdResponse = new EntitiesIdResponse();
        entitiesIdResponse.setIdEnte(BigInteger.valueOf(idEnte));


        return entitiesIdResponse;
    }

    private boolean isUltimoStato(String ultimoStato) {
        switch (ultimoStato){
            case "PEC_CA_FATTURA_ACCETTATA" :
                return true;
            case "PEC_CA_FATTURA_CONSEGNATA" :
                return true;
            case "PEC_CA_FATTURA_INVIO_UNICO" :
                return true;
            case "PEC_CA_FATTURA_INOLTRATA_PROTOCOLLO" :
                return true;
            case "PEC_CA_FATTURA_INOLTRATA_REGISTRAZIONE" :
                return true;
            case "PEC_CA_EC_RICEVUTA_ACCETTAZIONE" :
                return true;
            case "PEC_CA_EC_RICEVUTO_RIFIUTO" :
                return true;
            case "WS_CA_FATTURA_INVIO_UNICO" :
                return true;
            case "WS_CA_FATTURA_PROTOCOLLATA" :
                return true;
            case "WS_CA_FATTURA_REGISTRATA" :
                return true;
            case "WS_CA_EC_RICEVUTA_ACCETTAZIONE" :
                return true;
            case "WS_CA_EC_RICEVUTO_RIFIUTO" :
                return true;
            case "FTP_CA_FATTURA_INVIO_UNICO" :
                return true;
            case "FTP_CA_FATTURA_INVIO_PROTOCOLLO" :
                return true;
            case "FTP_CA_FATTURA_INVIO_REGISTRAZIONE" :
                return true;
            case "FTP_CA_EC_RICEVUTA_ACCETTAZIONE" :
                return true;
            case "FTP_CA_EC_RICEVUTO_RIFIUTO" :
                return true;
            default:
                return false;
        }
    }

    private void setPCTestDtoFromLastState(PCTestDto pcTestDto, String stato, boolean invioUnico){
        //controlli a cascata
        //partenza da esito committente
        if(stato.equalsIgnoreCase("PEC_CA_EC_RICEVUTA_ACCETTAZIONE") ||
            stato.equalsIgnoreCase("PEC_CA_EC_RICEVUTO_RIFIUTO") ||
            stato.equalsIgnoreCase("WS_CA_EC_RICEVUTA_ACCETTAZIONE") ||
            stato.equalsIgnoreCase("WS_CA_EC_RICEVUTO_RIFIUTO") ||
            stato.equalsIgnoreCase("FTP_CA_EC_RICEVUTA_ACCETTAZIONE") ||
            stato.equalsIgnoreCase("FTP_CA_EC_RICEVUTO_RIFIUTO")) {
            pcTestDto.setEsitoCommittente(true);
            pcTestDto.setDataEsitoCommittente(new Date());
            if(!invioUnico){
                pcTestDto.setInvioUnico(null);
                pcTestDto.setRegistrazione(true);
                pcTestDto.setProtocollo(true);
            }
            else {
                pcTestDto.setRegistrazione(null);
                pcTestDto.setProtocollo(null);
                pcTestDto.setInvioUnico(true);
            }
        }
        else if(stato.equalsIgnoreCase("PEC_CA_FATTURA_CONSEGNATA") ||
                stato.equalsIgnoreCase("WS_CA_FATTURA_REGISTRATA") ||
                stato.equalsIgnoreCase("FTP_CA_FATTURA_INVIO_REGISTRAZIONE")){
            if(!invioUnico) {
                pcTestDto.setInvioUnico(null);
                pcTestDto.setRegistrazione(true);
                pcTestDto.setProtocollo(true);
            }
            else{
                pcTestDto.setInvioUnico(true);
                pcTestDto.setRegistrazione(null);
                pcTestDto.setProtocollo(null);

            }
        }
        else if(stato.equalsIgnoreCase("PEC_CA_FATTURA_INOLTRATA_REGISTRAZIONE") ||
                stato.equalsIgnoreCase("WS_CA_FATTURA_INVIO_UNICO") ||
                stato.equalsIgnoreCase("WS_CA_FATTURA_PROTOCOLLATA") ||
                stato.equalsIgnoreCase("FTP_CA_FATTURA_INVIO_UNICO") ||
                stato.equalsIgnoreCase("FTP_CA_FATTURA_INVIO_PROTOCOLLO")){
            if(!invioUnico) {
                pcTestDto.setProtocollo(true);
                pcTestDto.setInvioUnico(null);
                pcTestDto.setRegistrazione(null);
            }else{
                pcTestDto.setInvioUnico(true);
                pcTestDto.setRegistrazione(null);
                pcTestDto.setProtocollo(null);
            }
        }
        else if(stato.equalsIgnoreCase("PEC_CA_FATTURA_ACCETTATA") ||
                stato.equalsIgnoreCase("PEC_CA_FATTURA_INVIO_UNICO") ||
                stato.equalsIgnoreCase("PEC_CA_FATTURA_INOLTRATA_PROTOCOLLO")){
            if(!invioUnico){
                pcTestDto.setProtocollo(false);
                pcTestDto.setRegistrazione(false);
                pcTestDto.setInvioUnico(null);
            }
            else{
                pcTestDto.setInvioUnico(false);
                pcTestDto.setRegistrazione(null);
                pcTestDto.setProtocollo(null);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public UtentiManager getUtentiManager() {
        return utentiManager;
    }

    public void setUtentiManager(UtentiManager utentiManager) {
        this.utentiManager = utentiManager;
    }

    public String getDurataToken() {
        return durataToken;
    }

    public void setDurataToken(String durataToken) {
        this.durataToken = durataToken;
    }

    public TestManager getTestManager() {
        return testManager;
    }

    public void setTestManager(TestManager testManager) {
        this.testManager = testManager;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }

    public CodificaStati2Manager getCodificaStati2Manager() {
        return codificaStati2Manager;
    }

    public void setCodificaStati2Manager(CodificaStati2Manager codificaStati2Manager) {
        this.codificaStati2Manager = codificaStati2Manager;
    }

    public FatturaAttivaNotificheManager getFatturaAttivaNotificheManager() {
        return fatturaAttivaNotificheManager;
    }

    public void setFatturaAttivaNotificheManager(FatturaAttivaNotificheManager fatturaAttivaNotificheManager) {
        this.fatturaAttivaNotificheManager = fatturaAttivaNotificheManager;
    }

    public CampoOpzionaleFatturaManager getCampoOpzionaleFatturaManager() {
        return campoOpzionaleFatturaManager;
    }

    public void setCampoOpzionaleFatturaManager(CampoOpzionaleFatturaManager campoOpzionaleFatturaManager) {
        this.campoOpzionaleFatturaManager = campoOpzionaleFatturaManager;
    }

    public NotificaFromEntiManager getNotificaFromEntiManager() {
        return notificaFromEntiManager;
    }

    public void setNotificaFromEntiManager(NotificaFromEntiManager notificaFromEntiManager) {
        this.notificaFromEntiManager = notificaFromEntiManager;
    }

    public NotificheAttivaFromSdiManager getNotificheAttivaFromSdiManager() {
        return notificheAttivaFromSdiManager;
    }

    public void setNotificheAttivaFromSdiManager(NotificheAttivaFromSdiManager notificheAttivaFromSdiManager) {
        this.notificheAttivaFromSdiManager = notificheAttivaFromSdiManager;
    }
}
