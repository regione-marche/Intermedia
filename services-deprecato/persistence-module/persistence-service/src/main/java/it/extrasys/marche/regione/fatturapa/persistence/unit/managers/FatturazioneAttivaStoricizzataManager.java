package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.camel.model.NotificheFatturaAttivaModel;
import it.extrasys.marche.regione.fatturapa.persistence.camel.model.ReportFatturazioneStorico;
import it.extrasys.marche.regione.fatturapa.persistence.camel.model.StatiNotificheAttivaModel;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.storico.FatturaAttivaStoricizzataEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FatturazioneAttivaStoricizzataManager {
    private static final Logger LOG = LoggerFactory.getLogger(FatturazioneAttivaStoricizzataManager.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private EntityManagerFactory entityManagerFactory;
    private FatturaAttivaStoricizzataDao fatturaAttivaStoricizzataDao;
    private NotificheAttivaFromSdiDao notificheAttivaFromSdiDao;
    private FatturaAttivaRicevutaConsegnaDao fatturaAttivaRicevutaConsegnaDao;
    private FatturaAttivaNotificaMancataConsegnaDao fatturaAttivaNotificaMancataConsegnaDao;
    private FatturaAttivaNotificaScartoDao fatturaAttivaNotificaScartoDao;
    private FatturaAttivaNotificaEsitoDao fatturaAttivaNotificaEsitoDao;
    private FatturaAttivaNotificaDecorrenzaTerminiDao fatturaAttivaNotificaDecorrenzaTerminiDao;
    private FatturaAttivaAttestaioneTrasmissioneFatturaDao fatturaAttivaAttestaioneTrasmissioneFatturaDao;
    private StatoAttivaAttestazioneTrasmissioneFatturaDao statoAttivaAttestazioneTrasmissioneFatturaDao;
    private StatoAttivaRicevutaConsegnaDao statoAttivaRicevutaConsegnaDao;
    private StatoAttivaNotificaMancataConsegnaDao statoAttivaNotificaMancataConsegnaDao;
    private StatoAttivaNotificaScartoDao statoAttivaNotificaScartoDao;
    private StatoAttivaNotificaEsitoDao statoAttivaNotificaEsitoDao;
    private StatoAttivaNotificaDecorrenzaTerminiDao statoAttivaNotificaDecorrenzaTerminiDao;
    private FatturaAttivaDao fatturaAttivaDao;


    public List<ReportFatturazioneStorico> storicizzaFatturaAttivaPerIdentificativoSdi(Object[] fatturaAttiva) throws Exception {
        EntityManager entityManager = null;
        List<ReportFatturazioneStorico> reportList = new ArrayList<>();
        ReportFatturazioneStorico report = new ReportFatturazioneStorico();

        BigInteger idFatturaAttiva = BigInteger.valueOf((Long) fatturaAttiva[0]);
        BigInteger identificativoSdi = BigInteger.valueOf((Long) fatturaAttiva[1]);
        report.setIdentificativoSdI(identificativoSdi + "");
        try {
            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            FatturaAttivaStoricizzataEntity fatturaAttivaStoricizzataEntity = new FatturaAttivaStoricizzataEntity();
            fatturaAttivaStoricizzataEntity.setNomeFileFattura((String) fatturaAttiva[2]); //da fattura_attiva
            fatturaAttivaStoricizzataEntity.setCodiceUfficioMittente((String) fatturaAttiva[8]);//codice_ufficio da Ente
            fatturaAttivaStoricizzataEntity.setDataRicezione((Date) fatturaAttiva[3]);//da fattura_attiva
            fatturaAttivaStoricizzataEntity.setIdentificativoSdI(identificativoSdi);//da fattura_attiva
            if ("T".equalsIgnoreCase((String) fatturaAttiva[4])) {
                fatturaAttivaStoricizzataEntity.setFatturazioneInterna(Boolean.TRUE);//da fattura_attiva
            } else if ("F".equalsIgnoreCase((String) fatturaAttiva[4])) {
                fatturaAttivaStoricizzataEntity.setFatturazioneInterna(Boolean.FALSE);//da fattura_attiva
            }
            fatturaAttivaStoricizzataEntity.setCodiceDestinatario((String) fatturaAttiva[5]);//da fattura_attiva
            fatturaAttivaStoricizzataEntity.setPecDestinatario((String) fatturaAttiva[6]);//da fattura_attiva
            fatturaAttivaStoricizzataEntity.setRicevutaComunicazione((String) fatturaAttiva[7]);//da fattura_attiva

            //Recupera tutti i tipi di notifiche per l'identificativoSdi;
            List<String> notificheFromSdI = notificheAttivaFromSdiDao.getTipoNotificheFromSdIByIdentificativoSdI(identificativoSdi, entityManager);

            //Compone la stringa con gli stati delle varie notifiche
            List<NotificheFatturaAttivaModel> statiAndNotifiche = getStatiAndNotifiche(notificheFromSdI, idFatturaAttiva, entityManager);
            String notifiche = StringUtils.left(creaNotificheFattura(statiAndNotifiche), 500);
            fatturaAttivaStoricizzataEntity.setNotificheFattura(notifiche);
            fatturaAttivaStoricizzataEntity.setDataInserimento(new Date());

            fatturaAttivaStoricizzataDao.create(fatturaAttivaStoricizzataEntity, entityManager);
            //Delete fatture attive notifiche e stati
            deleteNotificheAndStato(statiAndNotifiche, entityManager);
            //Delete notifiche_attive_from_sdi
            notificheAttivaFromSdiDao.deleteByIdentificativoSdi(identificativoSdi, entityManager);
            //Delete fattura_attiva
            fatturaAttivaDao.deleteByIdentificativoSdi(identificativoSdi, entityManager);

            entityManager.getTransaction().commit();

            report.setEsito("Success");
            reportList.add(report);
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            report.setEsito("Error");
            reportList.add(report);
            LOG.error("STORICIZZAZIONE FATTURE ATTIVA - Errore " + e.getMessage() + " durante salvataggio identificativo sdi: [" + identificativoSdi + "" + "]", e);
        }

        return reportList;
    }

    public List<Object[]> getFattureUltimAnno() throws Exception {

        LOG.info("********** FatturaAttivaStoricizzataManager: getFattureUltimAnno **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();
        List<Object[]> fatturaAttivaStoricizzataEntityList = null;

        try {

            fatturaAttivaStoricizzataEntityList = fatturaAttivaStoricizzataDao.getFattureUltimAnno(entityManager);

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }

        }
        return fatturaAttivaStoricizzataEntityList;
    }

    public List<Object[]> getFattureUltimAnnoUtente(UtenteEntity utenteEntity) throws Exception {

        LOG.info("********** FatturaAttivaStoricizzataManager: getFattureUltimAnnoUtente **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();
        List<Object[]> fatturaAttivaStoricizzataEntityList = null;

        try {

            fatturaAttivaStoricizzataEntityList = fatturaAttivaStoricizzataDao.getFattureUltimAnnoUtente(utenteEntity, entityManager);

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }

        }
        return fatturaAttivaStoricizzataEntityList;
    }

    //Una lista di stringhe del formatoFORMATO: tipo_notifica-stato1,stato2-data;
    private static String creaNotificheFattura(List<NotificheFatturaAttivaModel> notificheFatturaAttivaModel) {
        String notifiche = "";

        for (int i = 0; i < notificheFatturaAttivaModel.size(); i++) {

            notifiche += NotificheFatturaAttivaModel.TIPO_NOTIFICA_FROM_SDI_SHORT.parse(notificheFatturaAttivaModel.get(i).getTipoNotifica()).name();
            notifiche += "-";
            //Ogni stato di una notifica
            for (int j = 0; j < notificheFatturaAttivaModel.get(i).getStato().size(); j++) {

                StatiNotificheAttivaModel stato = notificheFatturaAttivaModel.get(i).getStato().get(j);

                if (CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.RICEVUTA.getValue().equals(stato.getStato())) {
                    notifiche += "R";
                    if (j == 0) {
                        notifiche += ",";
                    }
                } else if (CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.INVIATA.getValue().equals(stato.getStato())) {
                    notifiche += "I";
                    if (j == 0) {
                        notifiche += ",";
                    }
                }
                if (j == 1 || notificheFatturaAttivaModel.get(i).getStato().size() == 1) {
                    notifiche += "-";
                }
                //un solo stato
                if (notificheFatturaAttivaModel.get(i).getStato().size() == 1) {
                    notifiche += sdf.format(notificheFatturaAttivaModel.get(i).getStato().get(j).getDataRicezioneFromSdi());
                } else {
                    //Metto solo la data del secondo
                    if (j == 1) {
                        notifiche += sdf.format(notificheFatturaAttivaModel.get(i).getStato().get(j).getDataRicezioneFromSdi());
                    }
                }
            }
            notifiche += ";";
        }
        return notifiche;
    }

    //tipo_notifica-data_ricezione_risposta_from_sdi (da notifiche_attive_from_sdi)
    //obj[0]:data_ricezione_risposta_sdi -  obj[1]:id_tipo_notifica_from_sdi
  /*  private static String creaNotificheFattura(List<Object[]> tipoNotifiche) {
        String notificheFattura = "";

        for (Object[] obj : tipoNotifiche) {
            notificheFattura += TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.parse((String) obj[1]) + "-" + (Date) obj[0] + ";";
        }
        return notificheFattura;
    }*/


    private List<NotificheFatturaAttivaModel> getStatiAndNotifiche(List<String> tipoNotifiche, BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {
        List<NotificheFatturaAttivaModel> listNotifiche = new ArrayList<>();

        for (String tipoNotifica : tipoNotifiche) {

            if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue().equalsIgnoreCase(tipoNotifica)) {
                //Recupera tutte le notifiche
                List<FatturaAttivaRicevutaConsegnaEntity> fatturaAttivaRicevutaConsegna = fatturaAttivaRicevutaConsegnaDao.getListFatturaAttivaRicevutaConsegnaByIdFatturaAttiva(idFatturaAttiva, entityManager);
                //Recupera gli stati per ogni notifica
                for (FatturaAttivaRicevutaConsegnaEntity rc : fatturaAttivaRicevutaConsegna) {
                    List<StatoAttivaRicevutaConsegnaEntity> statiRicevuteConsegna = statoAttivaRicevutaConsegnaDao.getListStatiByRicevutaConsegna(rc, entityManager);

                    NotificheFatturaAttivaModel notificheFatturaAttivaModel = new NotificheFatturaAttivaModel();
                    List<StatiNotificheAttivaModel> statiList = new ArrayList<>();

                    for (StatoAttivaRicevutaConsegnaEntity s : statiRicevuteConsegna) {
                        StatiNotificheAttivaModel stati = new StatiNotificheAttivaModel();
                        stati.setStato(s.getStato().getCodStato().getValue());
                        stati.setDataRicezioneFromSdi(new Date(s.getData().getTime()));
                        statiList.add(stati);
                    }

                    notificheFatturaAttivaModel.setStato(statiList);
                    notificheFatturaAttivaModel.setIdNotifica(rc.getIdRicevutaConsegna());
                    notificheFatturaAttivaModel.setTipoNotifica(tipoNotifica);

                    listNotifiche.add(notificheFatturaAttivaModel);
                }

            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.getValue().equalsIgnoreCase(tipoNotifica)) {
                List<FatturaAttivaNotificaMancataConsegnaEntity> fatturaAttivaMancataConsegna = fatturaAttivaNotificaMancataConsegnaDao.getListFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(idFatturaAttiva, entityManager);

                for (FatturaAttivaNotificaMancataConsegnaEntity mc : fatturaAttivaMancataConsegna) {
                    List<StatoAttivaNotificaMancataConsegnaEntity> statiMancataConsegna = statoAttivaNotificaMancataConsegnaDao.getListStatiByMancataConsegna(mc, entityManager);

                    NotificheFatturaAttivaModel notificheFatturaAttivaModel = new NotificheFatturaAttivaModel();
                    List<StatiNotificheAttivaModel> statiList = new ArrayList<>();

                    for (StatoAttivaNotificaMancataConsegnaEntity s : statiMancataConsegna) {
                        StatiNotificheAttivaModel stati = new StatiNotificheAttivaModel();
                        stati.setStato(s.getStato().getCodStato().getValue());
                        stati.setDataRicezioneFromSdi(new Date(s.getData().getTime()));
                        statiList.add(stati);
                    }

                    notificheFatturaAttivaModel.setStato(statiList);
                    notificheFatturaAttivaModel.setIdNotifica(mc.getIdNotificaMancataConsegna());
                    notificheFatturaAttivaModel.setTipoNotifica(tipoNotifica);

                    listNotifiche.add(notificheFatturaAttivaModel);
                }
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.getValue().equalsIgnoreCase(tipoNotifica)) {
                List<FatturaAttivaNotificaScartoEntity> fatturaAttivaNotificaScarto = fatturaAttivaNotificaScartoDao.getListFatturaAttivaNotificaScartoByIdFatturaAttiva(idFatturaAttiva, entityManager);

                for (FatturaAttivaNotificaScartoEntity ns : fatturaAttivaNotificaScarto) {
                    List<StatoAttivaNotificaScartoEntity> statiNotificaScarto = statoAttivaNotificaScartoDao.getListStatiByNotificaScarto(ns, entityManager);

                    NotificheFatturaAttivaModel notificheFatturaAttivaModel = new NotificheFatturaAttivaModel();
                    List<StatiNotificheAttivaModel> statiList = new ArrayList<>();

                    for (StatoAttivaNotificaScartoEntity s : statiNotificaScarto) {
                        StatiNotificheAttivaModel stati = new StatiNotificheAttivaModel();
                        stati.setStato(s.getStato().getCodStato().getValue());
                        stati.setDataRicezioneFromSdi(new Date(s.getData().getTime()));
                        statiList.add(stati);
                    }

                    notificheFatturaAttivaModel.setStato(statiList);
                    notificheFatturaAttivaModel.setIdNotifica(ns.getIdNotificaScarto());
                    notificheFatturaAttivaModel.setTipoNotifica(tipoNotifica);

                    listNotifiche.add(notificheFatturaAttivaModel);
                }
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.getValue().equalsIgnoreCase(tipoNotifica)) {
                List<FatturaAttivaNotificaEsitoEntity> fatturaAttivaNotificaEsito = fatturaAttivaNotificaEsitoDao.getListFatturaAttivaNotificaEsitoByIdFatturaAttiva(idFatturaAttiva, entityManager);

                for (FatturaAttivaNotificaEsitoEntity ne : fatturaAttivaNotificaEsito) {
                    List<StatoAttivaNotificaEsitoEntity> statiNotificaEsito = statoAttivaNotificaEsitoDao.getListStatiByNotificaEsito(ne, entityManager);

                    NotificheFatturaAttivaModel notificheFatturaAttivaModel = new NotificheFatturaAttivaModel();
                    List<StatiNotificheAttivaModel> statiList = new ArrayList<>();

                    for (StatoAttivaNotificaEsitoEntity s : statiNotificaEsito) {
                        StatiNotificheAttivaModel stati = new StatiNotificheAttivaModel();
                        stati.setStato(s.getStato().getCodStato().getValue());
                        stati.setDataRicezioneFromSdi(new Date(s.getData().getTime()));
                        statiList.add(stati);
                    }

                    notificheFatturaAttivaModel.setStato(statiList);
                    notificheFatturaAttivaModel.setIdNotifica(ne.getIdNotificaEsito());
                    notificheFatturaAttivaModel.setTipoNotifica(tipoNotifica);

                    listNotifiche.add(notificheFatturaAttivaModel);
                }
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.getValue().equalsIgnoreCase(tipoNotifica)) {
                List<FatturaAttivaNotificaDecorrenzaTerminiEntity> fatturaAttivaNotificaDecorrenzaTermini = fatturaAttivaNotificaDecorrenzaTerminiDao.getListFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(idFatturaAttiva, entityManager);
                for (FatturaAttivaNotificaDecorrenzaTerminiEntity dc : fatturaAttivaNotificaDecorrenzaTermini) {
                    List<StatoAttivaNotificaDecorrenzaTerminiEntity> statiNotificaDecTermini = statoAttivaNotificaDecorrenzaTerminiDao.getListStatiByNotificaDecTermini(dc, entityManager);

                    NotificheFatturaAttivaModel notificheFatturaAttivaModel = new NotificheFatturaAttivaModel();
                    List<StatiNotificheAttivaModel> statiList = new ArrayList<>();

                    for (StatoAttivaNotificaDecorrenzaTerminiEntity s : statiNotificaDecTermini) {
                        StatiNotificheAttivaModel stati = new StatiNotificheAttivaModel();
                        stati.setStato(s.getStato().getCodStato().getValue());
                        stati.setDataRicezioneFromSdi(new Date(s.getData().getTime()));
                        statiList.add(stati);
                    }

                    notificheFatturaAttivaModel.setStato(statiList);
                    notificheFatturaAttivaModel.setIdNotifica(dc.getIdNotificaDecorrenzaTermini());
                    notificheFatturaAttivaModel.setTipoNotifica(tipoNotifica);

                    listNotifiche.add(notificheFatturaAttivaModel);
                }
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.getValue().equalsIgnoreCase(tipoNotifica)) {
                List<FatturaAttivaAttestazioneTrasmissioneFatturaEntity> fatturaAttivaAttestazioneTrasmissione = fatturaAttivaAttestaioneTrasmissioneFatturaDao.getListFatturaAttivaAttestazioneTrasmissioneByIdFatturaAttiva(idFatturaAttiva, entityManager);

                for (FatturaAttivaAttestazioneTrasmissioneFatturaEntity at : fatturaAttivaAttestazioneTrasmissione) {
                    List<StatoAttivaAttestazioneTrasmissioneFatturaEntity> statiAttestazioneTrasmissione = statoAttivaAttestazioneTrasmissioneFatturaDao.getListStatiByAttestazioneTrasmissione(at, entityManager);
                    NotificheFatturaAttivaModel notificheFatturaAttivaModel = new NotificheFatturaAttivaModel();
                    List<StatiNotificheAttivaModel> statiList = new ArrayList<>();

                    for (StatoAttivaAttestazioneTrasmissioneFatturaEntity s : statiAttestazioneTrasmissione) {
                        StatiNotificheAttivaModel stati = new StatiNotificheAttivaModel();
                        stati.setStato(s.getStato().getCodStato().getValue());
                        stati.setDataRicezioneFromSdi(new Date(s.getData().getTime()));
                        statiList.add(stati);
                    }
                    notificheFatturaAttivaModel.setIdNotifica(at.getIdAttestazioneTrasmissioneFattura());
                    notificheFatturaAttivaModel.setTipoNotifica(tipoNotifica);

                    listNotifiche.add(notificheFatturaAttivaModel);
                }
            }

        }
        return listNotifiche;
    }


    private void deleteNotificheAndStato(List<NotificheFatturaAttivaModel> tipoNotifiche, EntityManager entityManager) throws FatturaPaPersistenceException {

        for (NotificheFatturaAttivaModel notifica : tipoNotifiche) {
            String tipoNotifica = notifica.getTipoNotifica();

            if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue().equalsIgnoreCase(tipoNotifica)) {
                int i1 = statoAttivaRicevutaConsegnaDao.deleteStatoRicevutaconsegnaByIdFatturaAttiva(notifica.getIdNotifica(), entityManager);
                fatturaAttivaRicevutaConsegnaDao.deleteRicevutaConsegnaById(notifica.getIdNotifica(), entityManager);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.getValue().equalsIgnoreCase(tipoNotifica)) {
                statoAttivaNotificaMancataConsegnaDao.deleteStatoMancataConsegnaByIdMancataConsegna(notifica.getIdNotifica(), entityManager);
                fatturaAttivaNotificaMancataConsegnaDao.deleteMancataConsegnaByIdMancataConsegna(notifica.getIdNotifica(), entityManager);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.getValue().equalsIgnoreCase(tipoNotifica)) {
                statoAttivaNotificaScartoDao.deleteStatoNotificaScartoByIdNotificaScarto(notifica.getIdNotifica(), entityManager);
                fatturaAttivaNotificaScartoDao.deleteNotificaScartoById(notifica.getIdNotifica(), entityManager);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.getValue().equalsIgnoreCase(tipoNotifica)) {
                statoAttivaNotificaEsitoDao.deleteStatoEsitoByIdNotificaEsito(notifica.getIdNotifica(), entityManager);
                fatturaAttivaNotificaEsitoDao.deleteNotificaEsitoById(notifica.getIdNotifica(), entityManager);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.getValue().equalsIgnoreCase(tipoNotifica)) {
                statoAttivaNotificaDecorrenzaTerminiDao.deleteStatoNotificadecTerminiByIdDecTermini(notifica.getIdNotifica(), entityManager);
                fatturaAttivaNotificaDecorrenzaTerminiDao.deleteNotificaDecTerminiById(notifica.getIdNotifica(), entityManager);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.getValue().equalsIgnoreCase(tipoNotifica)) {
                statoAttivaAttestazioneTrasmissioneFatturaDao.deleteStatoAttestazioneTrasmByIdTrasmissione(notifica.getIdNotifica(), entityManager);
                fatturaAttivaAttestaioneTrasmissioneFatturaDao.deleteAttestazioneTrasmById(notifica.getIdNotifica(), entityManager);
            }
        }
    }

    public NotificheAttivaFromSdiDao getNotificheAttivaFromSdiDao() {
        return notificheAttivaFromSdiDao;
    }

    public void setNotificheAttivaFromSdiDao(NotificheAttivaFromSdiDao notificheAttivaFromSdiDao) {
        this.notificheAttivaFromSdiDao = notificheAttivaFromSdiDao;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public FatturaAttivaStoricizzataDao getFatturaAttivaStoricizzataDao() {
        return fatturaAttivaStoricizzataDao;
    }

    public void setFatturaAttivaStoricizzataDao(FatturaAttivaStoricizzataDao fatturaAttivaStoricizzataDao) {
        this.fatturaAttivaStoricizzataDao = fatturaAttivaStoricizzataDao;
    }

    public FatturaAttivaRicevutaConsegnaDao getFatturaAttivaRicevutaConsegnaDao() {
        return fatturaAttivaRicevutaConsegnaDao;
    }

    public void setFatturaAttivaRicevutaConsegnaDao(FatturaAttivaRicevutaConsegnaDao fatturaAttivaRicevutaConsegnaDao) {
        this.fatturaAttivaRicevutaConsegnaDao = fatturaAttivaRicevutaConsegnaDao;
    }

    public FatturaAttivaNotificaMancataConsegnaDao getFatturaAttivaNotificaMancataConsegnaDao() {
        return fatturaAttivaNotificaMancataConsegnaDao;
    }

    public void setFatturaAttivaNotificaMancataConsegnaDao(FatturaAttivaNotificaMancataConsegnaDao fatturaAttivaNotificaMancataConsegnaDao) {
        this.fatturaAttivaNotificaMancataConsegnaDao = fatturaAttivaNotificaMancataConsegnaDao;
    }

    public FatturaAttivaNotificaScartoDao getFatturaAttivaNotificaScartoDao() {
        return fatturaAttivaNotificaScartoDao;
    }

    public void setFatturaAttivaNotificaScartoDao(FatturaAttivaNotificaScartoDao fatturaAttivaNotificaScartoDao) {
        this.fatturaAttivaNotificaScartoDao = fatturaAttivaNotificaScartoDao;
    }

    public FatturaAttivaNotificaEsitoDao getFatturaAttivaNotificaEsitoDao() {
        return fatturaAttivaNotificaEsitoDao;
    }

    public void setFatturaAttivaNotificaEsitoDao(FatturaAttivaNotificaEsitoDao fatturaAttivaNotificaEsitoDao) {
        this.fatturaAttivaNotificaEsitoDao = fatturaAttivaNotificaEsitoDao;
    }

    public FatturaAttivaNotificaDecorrenzaTerminiDao getFatturaAttivaNotificaDecorrenzaTerminiDao() {
        return fatturaAttivaNotificaDecorrenzaTerminiDao;
    }

    public void setFatturaAttivaNotificaDecorrenzaTerminiDao(FatturaAttivaNotificaDecorrenzaTerminiDao fatturaAttivaNotificaDecorrenzaTerminiDao) {
        this.fatturaAttivaNotificaDecorrenzaTerminiDao = fatturaAttivaNotificaDecorrenzaTerminiDao;
    }

    public FatturaAttivaAttestaioneTrasmissioneFatturaDao getFatturaAttivaAttestaioneTrasmissioneFatturaDao() {
        return fatturaAttivaAttestaioneTrasmissioneFatturaDao;
    }

    public void setFatturaAttivaAttestaioneTrasmissioneFatturaDao(FatturaAttivaAttestaioneTrasmissioneFatturaDao fatturaAttivaAttestaioneTrasmissioneFatturaDao) {
        this.fatturaAttivaAttestaioneTrasmissioneFatturaDao = fatturaAttivaAttestaioneTrasmissioneFatturaDao;
    }

    public StatoAttivaAttestazioneTrasmissioneFatturaDao getStatoAttivaAttestazioneTrasmissioneFatturaDao() {
        return statoAttivaAttestazioneTrasmissioneFatturaDao;
    }

    public void setStatoAttivaAttestazioneTrasmissioneFatturaDao(StatoAttivaAttestazioneTrasmissioneFatturaDao statoAttivaAttestazioneTrasmissioneFatturaDao) {
        this.statoAttivaAttestazioneTrasmissioneFatturaDao = statoAttivaAttestazioneTrasmissioneFatturaDao;
    }

    public StatoAttivaRicevutaConsegnaDao getStatoAttivaRicevutaConsegnaDao() {
        return statoAttivaRicevutaConsegnaDao;
    }

    public void setStatoAttivaRicevutaConsegnaDao(StatoAttivaRicevutaConsegnaDao statoAttivaRicevutaConsegnaDao) {
        this.statoAttivaRicevutaConsegnaDao = statoAttivaRicevutaConsegnaDao;
    }

    public StatoAttivaNotificaMancataConsegnaDao getStatoAttivaNotificaMancataConsegnaDao() {
        return statoAttivaNotificaMancataConsegnaDao;
    }

    public void setStatoAttivaNotificaMancataConsegnaDao(StatoAttivaNotificaMancataConsegnaDao statoAttivaNotificaMancataConsegnaDao) {
        this.statoAttivaNotificaMancataConsegnaDao = statoAttivaNotificaMancataConsegnaDao;
    }

    public StatoAttivaNotificaScartoDao getStatoAttivaNotificaScartoDao() {
        return statoAttivaNotificaScartoDao;
    }

    public void setStatoAttivaNotificaScartoDao(StatoAttivaNotificaScartoDao statoAttivaNotificaScartoDao) {
        this.statoAttivaNotificaScartoDao = statoAttivaNotificaScartoDao;
    }

    public StatoAttivaNotificaEsitoDao getStatoAttivaNotificaEsitoDao() {
        return statoAttivaNotificaEsitoDao;
    }

    public void setStatoAttivaNotificaEsitoDao(StatoAttivaNotificaEsitoDao statoAttivaNotificaEsitoDao) {
        this.statoAttivaNotificaEsitoDao = statoAttivaNotificaEsitoDao;
    }

    public StatoAttivaNotificaDecorrenzaTerminiDao getStatoAttivaNotificaDecorrenzaTerminiDao() {
        return statoAttivaNotificaDecorrenzaTerminiDao;
    }

    public void setStatoAttivaNotificaDecorrenzaTerminiDao(StatoAttivaNotificaDecorrenzaTerminiDao statoAttivaNotificaDecorrenzaTerminiDao) {
        this.statoAttivaNotificaDecorrenzaTerminiDao = statoAttivaNotificaDecorrenzaTerminiDao;
    }

    public FatturaAttivaDao getFatturaAttivaDao() {
        return fatturaAttivaDao;
    }

    public void setFatturaAttivaDao(FatturaAttivaDao fatturaAttivaDao) {
        this.fatturaAttivaDao = fatturaAttivaDao;
    }
}
