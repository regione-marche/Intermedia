package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.api.rest.model.StatsFatture;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.UtenteEnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.*;
import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsResourceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsResourceImpl.class);

    private static final Date now = new Date();
    private static Date monthAgo = new Date(now.getTime() - 2629800000L);
    private static Date yearAgo = new Date(now.getTime() - 31557600000L);

    private EnteManager enteManager;

    private UtentiManager utentiManager;

    private DatiFatturaManager datiFatturaManager;

    private String durataToken;

    private FatturaAttivaManagerImpl fatturaAttivaManager;

    private FatturazionePassivaStoricizzataManager fatturazionePassivaStoricizzataManager;

    private FatturazioneAttivaStoricizzataManager fatturazioneAttivaStoricizzataManager;


    public StatsFatture getStatisticheFatturePassiveUltimoMese(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getStatisticheFatturePassiveUltimoMese **********");

        String token = (String) parameters.get(0);
        StatsFatture statsFatture = new StatsFatture();

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity);

            List<Object[]> fatture = new ArrayList<>();

            if (utentiManager.checkAuth(utenteEnteEntity, true)) {
                fatture.addAll(datiFatturaManager.getFattureBetweenDate(monthAgo, now));
            } else {
                fatture.addAll(datiFatturaManager.getFattureBetweenDateUtente(monthAgo, now, utenteEntity));
            }

            for (Object[] obj : fatture) {
                statsFatture = FunzioniConversione.statsFatturePassive(obj, statsFatture);
            }


        } catch (FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPaPersistenceException
                | FatturaPAUtenteNonAutorizzatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (FatturaPAEnteNonTrovatoException e) {
            LOG.info(e.getStackTrace().toString());
        }
        return statsFatture;
    }


    public StatsFatture getStatisticheFattureAttiveUltimoMese(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getStatisticheFattureAttiveUltimoMese **********");

        String token = (String) parameters.get(0);
        StatsFatture statsFatture = new StatsFatture();

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);

            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity);

            List<Object[]> fatturaAttiva = new ArrayList<>();

            if (utentiManager.checkAuth(utenteEnteEntity, true)) {
                fatturaAttiva.addAll(fatturaAttivaManager.getFatturaAttivaBetweenDate(monthAgo, now));
            } else {
                fatturaAttiva.addAll(fatturaAttivaManager.getFatturaAttivaBetweenDateUtente(monthAgo, now, utenteEntity));
            }

            for (Object[] obj : fatturaAttiva) {
                statsFatture = FunzioniConversione.statsFattureAttive(obj, statsFatture);
            }

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPaPersistenceException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

        return statsFatture;
    }


    public StatsFatture getStatisticheFatturePassiveUltimoAnno(Exchange exchange) throws Exception {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getStatisticheFatturePassiveUltimoAnno **********");

        String token = (String) parameters.get(0);
        StatsFatture statsFatture = new StatsFatture();

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity);

            List<Object[]> fatture = new ArrayList<>();

            //Admin
            if (utentiManager.checkAuth(utenteEnteEntity, true)) {
                fatture.addAll(datiFatturaManager.getFattureBetweenDate(yearAgo, now));
                fatture.addAll(fatturazionePassivaStoricizzataManager.getFattureUltimAnno());
            } else {
                fatture.addAll(datiFatturaManager.getFattureBetweenDateUtente(yearAgo, now, utenteEntity));
                fatture.addAll(fatturazionePassivaStoricizzataManager.getFattureUltimAnnoUtente(utenteEntity));
            }

            for (Object[] obj : fatture) {
                statsFatture = FunzioniConversione.statsFatturePassive(obj, statsFatture);
            }
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPaPersistenceException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (Exception e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException();
        }
        return statsFatture;
    }


    public StatsFatture getStatisticheFattureAttiveUltimoAnno(Exchange exchange) throws
            FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getStatisticheFattureAttiveUltimoAnno **********");

        String token = (String) parameters.get(0);
        StatsFatture statsFatture = new StatsFatture();

        try {

            String username = Autenticazione.checkToken(token, utentiManager, durataToken);

            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity);

            List<Object[]> fatturaAttiva = new ArrayList<>();

            //Admin
            if (utentiManager.checkAuth(utenteEnteEntity, true)) {
                fatturaAttiva.addAll(fatturaAttivaManager.getFatturaAttivaBetweenDate(yearAgo, now));
                fatturaAttiva.addAll(fatturazioneAttivaStoricizzataManager.getFattureUltimAnno());
            } else {
                fatturaAttiva.addAll(fatturaAttivaManager.getFatturaAttivaBetweenDateUtente(yearAgo, now, utenteEntity));
                fatturaAttiva.addAll(fatturazioneAttivaStoricizzataManager.getFattureUltimAnnoUtente(utenteEntity));
            }

            for (Object[] obj : fatturaAttiva) {
                statsFatture = FunzioniConversione.statsFattureAttive(obj, statsFatture);

            }
        } catch (FatturaPAException
                | FatturaPaPersistenceException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        } catch (Exception e) {
            LOG.error(e.getStackTrace().toString());
            throw new FatturaPAException(e.getMessage());
        }
        return statsFatture;
    }


    public StatsFatture getStatisticheEnti(Exchange exchange) throws
            FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException {

        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getStatisticheEnti **********");

        String token = (String) parameters.get(0);

        try {

            String username = Autenticazione.checkToken(token, utentiManager, durataToken);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utentiManager.getUtenteByUsername(username));

            if (!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            List<EnteEntity> enteEntityList = enteManager.getEnteByUser(username);

            StatsFatture statsFatture = new StatsFatture();

            for (EnteEntity enteEntity : enteEntityList) {
                statsFatture = FunzioniConversione.statsFatturePassive(enteEntity, statsFatture);
            }

            return statsFatture;


        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPaPersistenceException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

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

    public FatturazionePassivaStoricizzataManager getFatturazionePassivaStoricizzataManager() {
        return fatturazionePassivaStoricizzataManager;
    }

    public void setFatturazionePassivaStoricizzataManager(FatturazionePassivaStoricizzataManager
                                                                  fatturazionePassivaStoricizzataManager) {
        this.fatturazionePassivaStoricizzataManager = fatturazionePassivaStoricizzataManager;
    }

    public FatturazioneAttivaStoricizzataManager getFatturazioneAttivaStoricizzataManager() {
        return fatturazioneAttivaStoricizzataManager;
    }

    public void setFatturazioneAttivaStoricizzataManager(FatturazioneAttivaStoricizzataManager
                                                                 fatturazioneAttivaStoricizzataManager) {
        this.fatturazioneAttivaStoricizzataManager = fatturazioneAttivaStoricizzataManager;
    }
}
