package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.converters.NotificaFromEntiToEntityCAConverter;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaStatiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.DatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.NotificaFromEntiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.StatoFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.ZipFtpEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Date;

public class FatturazionePassivaNotificheCAManagerXAImpl {

    private static final Logger LOG = LoggerFactory.getLogger(FatturazionePassivaNotificheCAManagerXAImpl.class);

    private EntityManagerFactory entityManagerFactory;

    NotificaFromEntiToEntityCAConverter notificaFromEntiToEntityCAConverter;

    NotificaFromEntiDao notificaDao;

    StatoFatturaDao statoFatturaDao;

    DatiFatturaDao datiFatturaDao;

    CodificaStatiDao codificaStatiDao;

    public NotificaFromEntiEntity salvaNotifica(EsitoFatturaMessageRequest esitoFatturaMessageRequest, NotificaEsitoCommittenteType notificaEsitoCommittente, String flussoSemplificato[]) throws FatturaPAException, FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException, FatturaPAFatturaNonTrovataException, IOException {

        if (flussoSemplificato == null || flussoSemplificato.length != 2) {
            throw new FatturaPAException("FatturazionePassivaNotificheCAManagerXAImpl - salvaNotifiche: String array flussoSemplificato NON valido");
        }

        EntityManager entityManager = null;

        StatoFatturaEntity statoFatturaEntity = null;

        NotificaFromEntiEntity notificaFromEntiEntity;

        EsitoFatturaMessageType esitoFatturaMessage = esitoFatturaMessageRequest.getEsitoFatturaMessage();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            notificaFromEntiEntity = notificaFromEntiToEntityCAConverter.convert(esitoFatturaMessage);
            notificaFromEntiEntity.setEsito(NotificaFromEntiEntity.CODICI_ESITO_NOTIFICA.parse(notificaEsitoCommittente.getEsito().value()));
            notificaFromEntiEntity.setDescrizione(notificaEsitoCommittente.getDescrizione());
            notificaFromEntiEntity.setMessageIdCommittente(notificaEsitoCommittente.getMessageIdCommittente());
            DatiFatturaEntity datiFatturaEntity = datiFatturaDao.getFattureByIdentificativoSdINumeroFattura(notificaEsitoCommittente.getIdentificativoSdI(), notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura(), entityManager);

            statoFatturaEntity = statoFatturaDao.getLastStato(datiFatturaEntity.getIdDatiFattura(), entityManager);

            notificaFromEntiEntity.setIdentificativoSdI(datiFatturaEntity.getIdentificativoSdI());
            //Non è salvato sul db
            notificaFromEntiEntity.setNomeCedentePrestatore(datiFatturaEntity.getNomeCedentePrestatore());
            //Info non utili e ridondanti!
            //notificaFromEntiEntity.setNumeroProtocollo(datiFatturaEntity.getNumeroProtocollo());
            //notificaFromEntiEntity.setNumeroFattura(datiFatturaEntity.getNumeroFattura());
            //notificaFromEntiEntity.setDataFattura(datiFatturaEntity.getDataFattura());

            //salvataggio nome file esito committente
            notificaFromEntiEntity.setNomeFile(esitoFatturaMessage.getNomeFile());

            //TODO regma 112 check per verificare se si tratti o meno di una fattura che ricade nel flusso semplificato
            if (datiFatturaEntity.getFatturazioneInterna()) {

                LOG.info("FatturazionePassivaNotificheCAManagerXAImpl - salvaNotifiche: flusso semplificato, fatturazione interna, nome file fattura: " + datiFatturaEntity.getNomeFile());
                flussoSemplificato[0] = "T";
                flussoSemplificato[1] = datiFatturaEntity.getNomeFile();
            } else {
                flussoSemplificato[0] = "F";
                flussoSemplificato[1] = datiFatturaEntity.getNomeFile();
            }

        } catch (NoResultException e) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            throw new FatturaPAFatturaNonTrovataException("Fattura Non Trovata: Identificativo SdI: " + notificaEsitoCommittente.getIdentificativoSdI() + " Numero Fattura: " + notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura());

        } catch (Exception e) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            throw new FatturaPAException("Errore Interno:" + e.getMessage(), e);
        }

        // Controllo che la fattura non sia sia stata accettata per Decorrenza Termini
        if (statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.ACCETTATA_PER_DECORRENZA_TERMINI)) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            throw new FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException("Fattura Già Accettata Per Decorrenza Termini: Identificativo SdI: " + notificaEsitoCommittente.getIdentificativoSdI() + " Numero Fattura: " + notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura());
        }

        // Controllo che la fattura non sia Già stata Accettata o Rifiutata
        if (statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE) || statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATO_RIFIUTO)) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            throw new FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException("Fattura Già Accettata Per Decorrenza Termini: Identificativo SdI: " + notificaEsitoCommittente.getIdentificativoSdI() + " Numero Fattura: " + notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura());
        }
        try {
            // Salvo la notifica
            notificaFromEntiEntity = notificaDao.create(notificaFromEntiEntity, entityManager);

            // Modifico Lo Stato
            StatoFatturaEntity nuovoStato = new StatoFatturaEntity();
            nuovoStato.setDatiFattura(statoFatturaEntity.getDatiFattura());
            if (notificaFromEntiEntity.getEsito().equals(NotificaFromEntiEntity.CODICI_ESITO_NOTIFICA.ACCETTATA)) {
                //CodificaStatiEntity statoAccettata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA_ACCETTAZIONE, entityManager);
                CodificaStatiEntity statoAccettata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_EC_RICEVUTA_ACCETTAZIONE, entityManager);
                nuovoStato.setStato(statoAccettata);
            } else {
                //CodificaStatiEntity statoRifiutata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTO_RIFIUTO, entityManager);
                CodificaStatiEntity statoRifiutata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_EC_RICEVUTO_RIFIUTO, entityManager);
                nuovoStato.setStato(statoRifiutata);
            }

            statoFatturaDao.create(nuovoStato, entityManager);

            /*
            entityManager.flush();
            entityManager.clear();
            */

        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage());
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return notificaFromEntiEntity;
    }


    public NotificaFromEntiEntity salvaNotificaFtp(NotificaEsitoCommittenteType notificaEsitoCommittente, String codiceUfficio, String zipFile, String nomeFile) throws FatturaPAException, FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException, FatturaPAFatturaNonTrovataException, IOException {

        EntityManager entityManager = null;
        StatoFatturaEntity statoFatturaEntity = null;
        NotificaFromEntiEntity notificaFromEntiEntity = new NotificaFromEntiEntity();
        //EsitoFatturaMessageType esitoFatturaMessage = esitoFatturaMessageRequest.getEsitoFatturaMessage();

        try {
            entityManager = entityManagerFactory.createEntityManager();

            DatiFatturaEntity datiFatturaEntity = datiFatturaDao.getFattureByIdentificativoSdINumeroFattura(notificaEsitoCommittente.getIdentificativoSdI(), notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura(), entityManager);

            statoFatturaEntity = statoFatturaDao.getLastStato(datiFatturaEntity.getIdDatiFattura(), entityManager);
            notificaFromEntiEntity.setEsito(NotificaFromEntiEntity.CODICI_ESITO_NOTIFICA.parse(notificaEsitoCommittente.getEsito().value()));
            notificaFromEntiEntity.setDescrizione(notificaEsitoCommittente.getDescrizione());
            notificaFromEntiEntity.setMessageIdCommittente(notificaEsitoCommittente.getMessageIdCommittente());
            notificaFromEntiEntity.setIdentificativoSdI(datiFatturaEntity.getIdentificativoSdI());
            notificaFromEntiEntity.setCodUfficio(codiceUfficio);
            notificaFromEntiEntity.setIdFiscaleCommittente(datiFatturaEntity.getCommittenteIdFiscaleIVA());
            notificaFromEntiEntity.setNumeroFattura(notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura());
            notificaFromEntiEntity.setNumeroProtocollo(datiFatturaEntity.getNumeroProtocollo());
            notificaFromEntiEntity.setDataFattura(datiFatturaEntity.getDataFattura());
            notificaFromEntiEntity.setDataRicezioneFromEnte(new Date());
            notificaFromEntiEntity.setNomeFile(nomeFile);
        } catch (NoResultException e) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            throw new FatturaPAFatturaNonTrovataException("Fattura Non Trovata: Identificativo SdI: " + notificaEsitoCommittente.getIdentificativoSdI() + " Numero Fattura: " + notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura());

        } catch (Exception e) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            throw new FatturaPAException("Errore Interno:" + e.getMessage(), e);
        }

        // Controllo che la fattura non sia sia stata accettata per Decorrenza Termini
        if (statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.ACCETTATA_PER_DECORRENZA_TERMINI)) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            throw new FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException("Fattura Già Accettata Per Decorrenza Termini: Identificativo SdI: " + notificaEsitoCommittente.getIdentificativoSdI() + " Numero Fattura: " + notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura());
        }

        // Controllo che la fattura non sia Già stata Accettata o Rifiutata
        if (statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE) || statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATO_RIFIUTO)) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            throw new FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException("Fattura Già Accettata Per Decorrenza Termini: Identificativo SdI: " + notificaEsitoCommittente.getIdentificativoSdI() + " Numero Fattura: " + notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura());
        }
        try {
            // Salvo la notifica
            notificaFromEntiEntity = notificaDao.create(notificaFromEntiEntity, entityManager);
            // Modifico Lo Stato
            StatoFatturaEntity nuovoStato = new StatoFatturaEntity();
            nuovoStato.setDatiFattura(statoFatturaEntity.getDatiFattura());

            ZipFtpEntity zipFtpEntity = new ZipFtpEntity();
            zipFtpEntity.setFtpIn(true);
            zipFtpEntity.setCodiceEnte(statoFatturaEntity.getDatiFattura().getCodiceDestinatario());
            zipFtpEntity.setNomeFileZip(zipFile);
            zipFtpEntity.setDataRicezione(new Date());
            nuovoStato.setZipFtpEntity(zipFtpEntity);

            if (notificaFromEntiEntity.getEsito().equals(NotificaFromEntiEntity.CODICI_ESITO_NOTIFICA.ACCETTATA)) {
                CodificaStatiEntity statoAccettata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_EC_RICEVUTA_ACCETTAZIONE, entityManager);
                nuovoStato.setStato(statoAccettata);
            } else {
                CodificaStatiEntity statoRifiutata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_EC_RICEVUTO_RIFIUTO, entityManager);
                nuovoStato.setStato(statoRifiutata);
            }
            statoFatturaDao.create(nuovoStato, entityManager);
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage());
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return notificaFromEntiEntity;
    }


    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public NotificaFromEntiToEntityCAConverter getNotificaFromEntiToEntityCAConverter() {
        return notificaFromEntiToEntityCAConverter;
    }

    public void setNotificaFromEntiToEntityCAConverter(NotificaFromEntiToEntityCAConverter notificaFromEntiToEntityCAConverter) {
        this.notificaFromEntiToEntityCAConverter = notificaFromEntiToEntityCAConverter;
    }

    public NotificaFromEntiDao getNotificaDao() {
        return notificaDao;
    }

    public void setNotificaDao(NotificaFromEntiDao notificaDao) {
        this.notificaDao = notificaDao;
    }

    public StatoFatturaDao getStatoFatturaDao() {
        return statoFatturaDao;
    }

    public void setStatoFatturaDao(StatoFatturaDao statoFatturaDao) {
        this.statoFatturaDao = statoFatturaDao;
    }

    public CodificaStatiDao getCodificaStatiDao() {
        return codificaStatiDao;
    }

    public void setCodificaStatiDao(CodificaStatiDao codificaStatiDao) {
        this.codificaStatiDao = codificaStatiDao;
    }

    public DatiFatturaDao getDatiFatturaDao() {
        return datiFatturaDao;
    }

    public void setDatiFatturaDao(DatiFatturaDao datiFatturaDao) {
        this.datiFatturaDao = datiFatturaDao;
    }
}