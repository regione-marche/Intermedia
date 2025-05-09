package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.api.rest.model.UserDto;
import it.extrasys.marche.regione.fatturapa.api.rest.model.UserDtoResponseList;
import it.extrasys.marche.regione.fatturapa.api.rest.model.UserIdResponse;
import it.extrasys.marche.regione.fatturapa.api.rest.model.UserTokenRequest;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.UtenteEnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteServizioEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.ChiaveManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.UtentiManager;
import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.BadRequestException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class AdministrationResourceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(AdministrationResourceImpl.class);

    private UtentiManager utentiManager;

    private String durataToken;

    private ChiaveManager chiaveManager;

    private EnteManager enteManager;

    public String getToken(Exchange exchange) throws FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getToken **********");

        UserTokenRequest body = (UserTokenRequest) parameters.get(0);

        try {

            UtenteServizioEntity utenteServizioEntity = utentiManager.getUtenteServizioByUsername(body.getUsernameServizio());
            if(!utenteServizioEntity.getPassword().equals(body.getPasswordServizio())) {
                throw new FatturaPAUtenteNonAutorizzatoException("credenziali servizio errate");
            }

            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(body.getUsernameUtente());
            long timestamp = new Date().getTime();
            String psw = utentiManager.decryptPassword(utenteEntity.getPassword());
            String payloadJson = "{ \"username\":\""+utenteEntity.getUsername()+"\", \"password\":\""+psw+"\", \"ruolo\":\""+utenteEntity.getRuolo()+"\", \"timestamp\":\""+timestamp+"\" }";
            String encodedPayload = Base64.getEncoder().encodeToString(payloadJson.getBytes());
            utenteEntity.setTokenAuth(encodedPayload);
            utentiManager.updateUtente(utenteEntity, false);
            return encodedPayload;

        } catch (FatturaPAException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException e){
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch ( NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }

    }

    public UserDtoResponseList getUser(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getUser **********");

        String username = (String) parameters.get(0);

        String token = (String) parameters.get(1);

        try {
            String usernameToken = Autenticazione.checkToken(token, utentiManager, durataToken);

            if(username == null){
                UtenteEntity utenteEntity1 = utentiManager.getUtenteByUsername(usernameToken);
                UtenteEnteEntity utenteEnteEntity1 = new UtenteEnteEntity();
                utenteEnteEntity1.setUtente(utenteEntity1);
                if(!utentiManager.checkAuth(utenteEnteEntity1, true)){
                    throw new FatturaPAUtenteNonAutorizzatoException();
                }
                List<UserDto> userDtoList = new ArrayList<>();
                List<UtenteEntity> utenteEntityList = utentiManager.getAllUsers();
                for(UtenteEntity utenteEntity : utenteEntityList){
                    if(!usernameToken.equalsIgnoreCase(utenteEntity.getUsername())) {
                        UserDto userDto = FunzioniConversione.utenteEntityToUserDto(utenteEntity);
                        List<String> codUff = new ArrayList<>();
                        List<UtenteEnteEntity> collegamenti = utentiManager.getUtenteEnteFromUtente(utenteEntity);
                        if(collegamenti != null){
                            for(UtenteEnteEntity utenteEnteEntity : collegamenti){
                                if(utenteEnteEntity.getEnte() != null) {
                                    codUff.add(utenteEnteEntity.getEnte().getCodiceUfficio());
                                }
                            }
                        }
                        userDto.setCodiciUfficio(codUff);
                        userDtoList.add(userDto);
                    }
                }
                UserDtoResponseList userDtoListRet = new UserDtoResponseList();
                userDtoListRet.setUserDtoList(userDtoList);
                return userDtoListRet;
            }
            else{
                UtenteEntity utenteEntity1 = utentiManager.getUtenteByUsername(usernameToken);
                UtenteEnteEntity utenteEnteEntity1 = new UtenteEnteEntity();
                utenteEnteEntity1.setUtente(utenteEntity1);
                if(!utentiManager.checkAuth(utenteEnteEntity1, true) && !username.equalsIgnoreCase(usernameToken)){
                    throw new FatturaPAUtenteNonAutorizzatoException();
                }
                UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
                if("superadmin".equalsIgnoreCase(utenteEntity.getRuolo())){
                    throw new FatturaPAUtenteNonAutorizzatoException();
                }
                List<UserDto> userDtoList = new ArrayList<>();
                UserDto userDto = FunzioniConversione.utenteEntityToUserDto(utenteEntity);
                List<String> codUff = new ArrayList<>();
                List<UtenteEnteEntity> collegamenti = utentiManager.getUtenteEnteFromUtente(utenteEntity);
                if(collegamenti != null){
                    for(UtenteEnteEntity utenteEnteEntity : collegamenti){
                        if(utenteEnteEntity.getEnte() != null) {
                            codUff.add(utenteEnteEntity.getEnte().getCodiceUfficio());
                        }
                    }
                }
                userDto.setCodiciUfficio(codUff);
                userDtoList.add(userDto);
                UserDtoResponseList userDtoListRet = new UserDtoResponseList();
                userDtoListRet.setUserDtoList(userDtoList);
                return userDtoListRet;
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

    public UserIdResponse updateUser(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** updateUser **********");

        UserDto body = (UserDto) parameters.get(0);

        String token = (String) parameters.get(1);

        boolean cifraPsw = false;

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);

            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(body.getUsername());
            UtenteEntity utenteEntity2 = utentiManager.getUtenteByUsername(username);

            boolean admin = false, uguali = false;

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity2);
            if(utentiManager.checkAuth(utenteEnteEntity, true)) {
                admin = true;
            }
            else if(username.equalsIgnoreCase(body.getUsername())) {
                uguali = true;
            }

            if(!admin && !uguali){
                throw new FatturaPAUtenteNonAutorizzatoException("Utente non autorizzato a modificare");
            }

            //utenteEntity.setUsername(body.getUsername());
            if(body.getPassword() != null){
                utenteEntity.setPassword(body.getPassword());
                cifraPsw = true;
            }
            utenteEntity.setNome(body.getNome());
            utenteEntity.setCognome(body.getCognome());

            if(admin) {
                List<UtenteEnteEntity> utenteEnteEntityListDB = utentiManager.getUtenteEnteFromUtente(utenteEntity);
                if(utenteEnteEntityListDB != null){

                    for(UtenteEnteEntity utenteEnteEntityRemove : utenteEnteEntityListDB){
                        utentiManager.deleteCollegamentoUtenteEnte(utenteEnteEntityRemove);
                    }
                    if(body.getCodiciUfficio() != null) {
                        for (String codiceUfficio : body.getCodiciUfficio()) {
                            UtenteEnteEntity utenteEnteEntity1 = new UtenteEnteEntity();
                            utenteEnteEntity1.setEnte(enteManager.getEnteByCodiceUfficio(codiceUfficio));
                            utenteEnteEntity1.setUtente(utenteEntity);
                            utentiManager.createUtenteEnte(utenteEnteEntity1);
                        }
                    }
                }

                utenteEntity.setRuolo(body.getRuolo());
            }

            utentiManager.updateUtente(utenteEntity, cifraPsw);

            UserIdResponse userIdResponse = new UserIdResponse();
            userIdResponse.setId(utenteEntity.getIdUtente());

            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);

            return userIdResponse;
        } catch (FatturaPAException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }

    public UserIdResponse createUser(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** createUser **********");

        UserDto body = (UserDto) parameters.get(0);

        String token = (String) parameters.get(1);

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utentiManager.getUtenteByUsername(username));
            if(!utentiManager.checkAuth(utenteEnteEntity, true)){
                throw new FatturaPAUtenteNonAutorizzatoException("utente non amministratore");
            }

            boolean esiste = true;

            try{
                utentiManager.getUtenteByUsername(body.getUsername());
            }
            catch (FatturaPAUtenteNonTrovatoException e){
                esiste = false;
            }

            if(esiste){
                throw new BadRequestException("Utente "+body.getUsername()+" già esistente!");
            }

            UtenteEntity utenteEntity = new UtenteEntity();
            utenteEntity.setUsername(body.getUsername());
            utenteEntity.setPassword(body.getPassword());
            utenteEntity.setNome(body.getNome());
            utenteEntity.setCognome(body.getCognome());
            utenteEntity.setRuolo(body.getRuolo());
            utentiManager.createUtente(utenteEntity);

            if(body.getCodiciUfficio() != null) {
                for (String codiceUfficio : body.getCodiciUfficio()) {
                    UtenteEnteEntity utenteEnteEntity1 = new UtenteEnteEntity();
                    utenteEnteEntity1.setEnte(enteManager.getEnteByCodiceUfficio(codiceUfficio));
                    utenteEnteEntity1.setUtente(utenteEntity);
                    utentiManager.createUtenteEnte(utenteEnteEntity1);
                }
            }

            UserIdResponse userIdResponse = new UserIdResponse();
            userIdResponse.setId(utentiManager.getUtenteByUsername(body.getUsername()).getIdUtente());

            return userIdResponse;
        } catch (FatturaPAException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPaPersistenceException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }

    public void reimpostaPsw(Exchange exchange) throws FatturaPAUtenteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonAutorizzatoException, FatturaPAEnteNonTrovatoException, FatturaPATokenNonValidoException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** reimpostaPsw **********");

        String link = (String) parameters.get(0);
        UserTokenRequest userTokenRequest = (UserTokenRequest) parameters.get(1);

        //---------- GetToken -------------------
        UtenteServizioEntity utenteServizioEntity = utentiManager.getUtenteServizioByUsername(userTokenRequest.getUsernameServizio());
        if(!utenteServizioEntity.getPassword().equals(userTokenRequest.getPasswordServizio())) {
            throw new FatturaPAUtenteNonAutorizzatoException("credenziali servizio errate");
        }

        UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(userTokenRequest.getUsernameUtente());
        long timestamp = new Date().getTime();
        String usernameMail = utenteEntity.getUsername();
        String payloadJson = "{ \"username\":\""+usernameMail+"\", \"timestamp\":\""+timestamp+"\" }";
        String encodedPayload = Base64.getEncoder().encodeToString(payloadJson.getBytes());
        utenteEntity.setTokenAuth(encodedPayload);
        utentiManager.updateUtente(utenteEntity, false);
        //----------------------------------------

        List<String> newData = new ArrayList<>();


        newData.add(encodedPayload);
        newData.add(usernameMail);
        newData.add(link+"?token="+encodedPayload);

        exchange.getIn().setBody(newData);


    }

    public UserIdResponse updateUserPassword(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** createUser **********");

        UserDto body = (UserDto) parameters.get(0);

        String token = (String) parameters.get(1);

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            utenteEntity.setPassword(body.getPassword());
            utentiManager.updateUtente(utenteEntity, true);
            UserIdResponse userIdResponse = new UserIdResponse();
            userIdResponse.setId(utenteEntity.getIdUtente());
            return userIdResponse;

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

    public void setUtentiManager(UtentiManager utentiManager) {
        this.utentiManager = utentiManager;
    }

    public UtentiManager getUtentiManager(){
        return this.utentiManager;
    }

    public void setDurataToken(String durataToken) {
        this.durataToken = durataToken;
    }

    public String getDurataToken(){
        return this.durataToken;
    }

    public void setChiaveManager(ChiaveManager chiaveManager) {
        this.chiaveManager = chiaveManager;
    }

    public ChiaveManager getChiaveManager(){
        return this.chiaveManager;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }
}
