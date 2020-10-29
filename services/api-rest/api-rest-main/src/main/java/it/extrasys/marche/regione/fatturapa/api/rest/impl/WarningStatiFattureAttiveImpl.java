package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.api.rest.model.*;
import it.extrasys.marche.regione.fatturapa.api.rest.models.FatturaBindy;
import it.extrasys.marche.regione.fatturapa.api.rest.models.FlagModel;
import it.extrasys.marche.regione.fatturapa.api.rest.utils.WarningStatiFatture;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodificaFlagWarningEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorFatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaNotificheManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MonitorFatturaAttivaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificheAttivaFromSdiManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.UtentiManager;
import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class WarningStatiFattureAttiveImpl {
    private static final Logger LOG = LoggerFactory.getLogger(WarningStatiFattureAttiveImpl.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private UtentiManager utentiManager;
    private NotificheAttivaFromSdiManager notificheAttivaFromSdiManager;
    private FatturaAttivaNotificheManager fatturaAttivaNotificheManager;
    private MonitorFatturaAttivaManager monitorFatturaAttivaManager;
    private String durataToken;
    private String interval;
    private Integer intervalDecTerm;
    private Integer intervalAttTrasm;


    public void acServizioWarningStatiFattureGET(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();
        String token = (String) parameters.get(0);
        OrderRequest request = (OrderRequest) parameters.get(1);
        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            List<MonitorFatturaAttivaEntity> monitorFattureAttive = monitorFatturaAttivaManager.getMonitorFattureAttiveWarning(request.getOrderBy(), request.getOrdering(), request.getNumberOfElements(), request.getPageNumber(), CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE.getValue());

            List<WarningStatiFattureResponse> response = mapWarningStatiFattureResponse(monitorFattureAttive);
            WarningStatiFatturaResponseList responseList = new WarningStatiFatturaResponseList();
            responseList.setWarningStatiFattureResponseList(response);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            exchange.getIn().setBody(responseList);
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
    }

    public void acServizioCountWarningStatiFattureGET(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();
        String token = (String) parameters.get(0);
        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            WarningStatiFatturaCountResponse response = new WarningStatiFatturaCountResponse();

            Long count = monitorFatturaAttivaManager.getCountMonitorFattureAttiveWarning(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE.getValue());
            response.setCount(count.intValue());
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            exchange.getIn().setBody(response);
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
    }

    public void acServizioStatiFattureGET(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();
        String token = (String) parameters.get(0);
        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            List<MonitorFatturaAttivaEntity> monitorFattureAttive = monitorFatturaAttivaManager.getMonitorFattureAttive(null, null, null, null, CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE.getValue());

            List<FatturaBindy> fatturaBindies = mapFatturaBindy(monitorFattureAttive);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            exchange.getIn().setHeader("username", usernameUtente);
            exchange.getIn().setBody(fatturaBindies);
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
    }


    public void salvaMonitorFatturaAttiva(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException {
        LOG.info("MONITOR FATTURA ATTIVA BATCH - START Popola tabella monitor_fattura_attiva");
        Map<BigInteger, List<MonitorFatturaAttivaEntity>> fattureAttive = recuperaFattureAttive(notificheAttivaFromSdiManager, fatturaAttivaNotificheManager, interval, intervalDecTerm, intervalAttTrasm);

        List<MonitorFatturaAttivaEntity> fattureDaSalvare = fattureAttive.values().parallelStream()
                .map(fa -> fa.get(0))
                .collect(Collectors.toList());

        monitorFatturaAttivaManager.aggiornaMonitorFatturaAttiva(fattureDaSalvare);
        LOG.info("MONITOR FATTURA ATTIVA BATCH - END Popola tabella monitor_fattura_attiva");
    }


    /*
    Cerca sul database tutte le fatture attive degli ultimi N giorni.
    Per ognuna calcola se è in uno stato di warning (FLAG_ROSSO) oppure no (FLAG_VERDE)
    Restituisce una mappa del tipo <idFattAttiva, List<NotificheAttiveModel>>
    List<NotificheAttiveModel> conterrà sempre un solo elemento. In caso di stati ricevuta-inviata, quello con ricevuta viene eliminato perchè inutile per il mapping.
    */
    public static Map<BigInteger, List<MonitorFatturaAttivaEntity>> recuperaFattureAttive(NotificheAttivaFromSdiManager notificheAttivaFromSdiManager, FatturaAttivaNotificheManager fatturaAttivaNotificheManager, String interval, Integer intervalDecTerm, Integer intervalAttTrasm) throws FatturaPAException, FatturaPaPersistenceException {

        List<Object[]> notificheAttiveAfterDate = notificheAttivaFromSdiManager.getNotificheAttiveAfterDate(interval);

        Map<BigInteger, List<MonitorFatturaAttivaEntity>> mapFattureAttive = notificheAttiveAfterDate.stream()
                .map(f -> {
                    MonitorFatturaAttivaEntity mfa = new MonitorFatturaAttivaEntity();
                    mfa.setIdFatturaAttiva(BigInteger.valueOf((Long) f[0]));
                    if (f[1] != null) {
                        mfa.setIdentificativoSdi(BigInteger.valueOf((Long) f[1]));
                    }
                    mfa.setDataCreazione((Timestamp) f[2]);
                    mfa.setStato((String) f[4]);
                    mfa.setNomeFile((String) f[5]);
                    mfa.setCodiceUfficioMittente((String) f[6]);
                    mfa.setTipoCanale((String) f[7]);
                    mfa.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO);//Default
                    mfa.setDescrizione("");
                    return mfa;

                }).collect(Collectors.groupingBy(MonitorFatturaAttivaEntity::getIdFatturaAttiva));

        //Fattura con solo lo stato 'RICEVUTA'
        mapFattureAttive.entrySet().forEach(f -> {
            //Controlla se per una fattura c'è lo stato INVIATA
            Optional<MonitorFatturaAttivaEntity> notificheAttiveModelInviata = f.getValue().stream()
                    .filter(ff -> CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.INVIATA.getValue().equals(ff.getStato()))
                    .findFirst();

            //Fattura con solo lo stato 'RICEVUTA'
            if (!notificheAttiveModelInviata.isPresent()) {
                f.getValue().get(0).setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.ROSSO);
                f.getValue().get(0).setDescrizione("Fattura solo RICEVUTA");
            } else {
                CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag = null;
                String descrizione = "";

                try {
                    FlagModel flagModel = checkNotificaFatturaAttiva(notificheAttiveModelInviata.get(), fatturaAttivaNotificheManager, intervalDecTerm, intervalAttTrasm);
                    flag = flagModel.getFlag();
                    descrizione = flagModel.getDescrizione();
                } catch (FatturaPAException e) {
                    LOG.error(e.getMessage());
                }
                notificheAttiveModelInviata.get().setFlag(flag);
                notificheAttiveModelInviata.get().setDescrizione(descrizione);
                //Elimina quelle con stato RICEVUTE: in questo modo ho la mappa 'pulita'
                boolean b = f.getValue().removeIf(ff -> CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.RICEVUTA.getValue().equals(ff.getStato()));
            }
        });

        return mapFattureAttive;
    }


    private static FlagModel checkNotificaFatturaAttiva(MonitorFatturaAttivaEntity notifica, FatturaAttivaNotificheManager fatturaAttivaNotificheManager, int intervalDecTermini, int intervalAttTrasm) throws FatturaPAException {
        FlagModel flagModel = new FlagModel();
        flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.ROSSO);

        FatturaAttivaNotificaScartoEntity notificaScarto = null;
        FatturaAttivaNotificaMancataConsegnaEntity notificaMancataConsegna = null;
        //Effettua le query per cercare le notifiche finale per una fattura attiva (sono esclusive tra di loro)

        FatturaAttivaRicevutaConsegnaEntity ricevutaConsegna = fatturaAttivaNotificheManager.getFatturaAttivaRicevutaConsegnaByIdFatturaAttiva(notifica.getIdFatturaAttiva());
        /*CASO RICEVUTA_CONSEGNA:
            1) Controlla se esiste DECORRENZA_TERMINI (stato inviata) oppure NOTIFICA_ESITO
            2) Se non esiste controlla se sono passati 15 giorni
            */
        if (ricevutaConsegna != null) {
            List<StatoAttivaNotificaDecorrenzaTerminiEntity> statiDecTermini = fatturaAttivaNotificheManager.getStatoFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(notifica.getIdFatturaAttiva());
            //Nessuna decorrenza termini -->cerca notifica_esito
            if (statiDecTermini == null || statiDecTermini.isEmpty()) {
                FatturaAttivaNotificaEsitoEntity notificaEsito = fatturaAttivaNotificheManager.getFatturaAttivaNotificaEsitoByIdFatturaAttiva(notifica.getIdFatturaAttiva());
                if (notificaEsito != null) {
                    flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE);
                    flagModel.setDescrizione("Ricevuta NOTIFICA_ESITO");
                } else {
                    flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO);
                    flagModel.setDescrizione("In attesa della NOTIFICA_DECORRENZA_TERMINI");
                    flagModel.setFlag(WarningStatiFatture.checkDecorrenzaTermini(notifica.getDataCreazione(), flagModel.getFlag(), intervalDecTermini));
                    if (CodificaFlagWarningEntity.CODICI_FLAG_WARNING.ROSSO.equals(flagModel.getFlag())) {
                        flagModel.setDescrizione("In attesa della NOTIFICA_DECORRENZA_TERMINI");
                    }
                }
            } else {
                Optional<StatoAttivaNotificaDecorrenzaTerminiEntity> decTerminiInviata = statiDecTermini.parallelStream()
                        .filter(f -> CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.INVIATA.getValue().equals(f.getStato().getCodStato().getValue()))
                        .findAny();

                //Trovata dec_termini INVIATA
                if (decTerminiInviata.isPresent()) {
                    flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE);
                    flagModel.setDescrizione("Inviata NOTIFICA_DECORRENZA_TERMINI");
                } else { //Trovata dec_termini RICEVUTA
                    flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.ROSSO);
                    flagModel.setDescrizione("Non inviata NOTIFICA_DECORRENZA_TERMINI");
                }
            }

            return flagModel;
        } else {
            //Cerca se esiste una notifica_scarto
            notificaScarto = fatturaAttivaNotificheManager.getFatturaAttivaNotificaScartoByIdFatturaAttiva(notifica.getIdFatturaAttiva());
        }

        /*
          CASO NOTIFICA_SCARTO: stato finale ok!
        */
        if (notificaScarto != null) {
            flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE);
            flagModel.setDescrizione("Inviata NOTIFICA_SCARTO");
            return flagModel;
        } else {
            //cerca se esiste una notifica_mancata_consegna
            notificaMancataConsegna = fatturaAttivaNotificheManager.getFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(notifica.getIdFatturaAttiva());
        }

        /*
        CASO NOTIFICA_MANCATA_CONSEGNA:
        1) controlla se esiste attestazione_mancata_trasmissione (stato=inviata)
        2) Se non esiste, controlla se sono passati 10 giorni
        */
        if (notificaMancataConsegna != null) {
            List<StatoAttivaAttestazioneTrasmissioneFatturaEntity> statiAttestazioneTrasmissioneFattura = fatturaAttivaNotificheManager.statoFatturaAttivaAttestazioneTrasmissioneFatturaUltimoStatoRicevutaByIdFatturaAttiva(notifica.getIdFatturaAttiva());

            Optional<StatoAttivaAttestazioneTrasmissioneFatturaEntity> attTrasmissioneInviata = statiAttestazioneTrasmissioneFattura.parallelStream()
                    .filter(f -> CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.INVIATA.getValue().equals(f.getStato().getCodStato().getValue()))
                    .findFirst();

            if (attTrasmissioneInviata.isPresent()) {
                flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE);
                flagModel.setDescrizione("Inviata NOTIFICA_ATTESTAZIONE_TRASMISSIONE");
            } else {
                flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO);
                flagModel.setDescrizione("In attesa della NOTIFICA_ATTESTAZIONE_TRASMISSIONE");

                flagModel.setFlag(WarningStatiFatture.checkDecorrenzaTermini(notifica.getDataCreazione(), flagModel.getFlag(), intervalAttTrasm));
                if (CodificaFlagWarningEntity.CODICI_FLAG_WARNING.ROSSO.equals(flagModel.getFlag())) {
                    flagModel.setDescrizione("Non inviata NOTIFICA_ATTESTAZIONE_TRASMISSIONE");
                }
            }
        } else {
            //A questo punto non ho nessuna notifica!
            //Se sono passati 2 giorni (da properties) la fattura è in errore
            flagModel.setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.ROSSO);
            flagModel.setDescrizione("Nessuna notifica");

            return flagModel;
        }
        return flagModel;
    }


    private static List<WarningStatiFattureResponse> mapWarningStatiFattureResponse(List<MonitorFatturaAttivaEntity> monitorFattureAttive) {
        List<WarningStatiFattureResponse> response = new ArrayList<>();
        for (MonitorFatturaAttivaEntity mfa : monitorFattureAttive) {
            WarningStatiFattureResponse w = new WarningStatiFattureResponse();
            w.setIdentificativoSdi(mfa.getIdentificativoSdi() + "");
            w.setDataCreazione(mfa.getDataCreazione());
            w.setCodiceUfficioDestinatario(mfa.getCodiceUfficioMittente());
            w.setNomeFile(mfa.getNomeFile());
            w.setTipoCanale(mfa.getTipoCanale());
            w.setStato(mfa.getStato());
            w.setFlag(mfa.getFlagWarning().getDescFlagWarning());

            response.add(w);
        }
        return response;
    }


    private static List<FatturaBindy> mapFatturaBindy(List<MonitorFatturaAttivaEntity> fattura) {
        return fattura.stream()
                .map(f -> {
                    FatturaBindy fb = new FatturaBindy();
                    fb.setIdentificativoSdi(f.getIdentificativoSdi()+"");
                    //fb.setDataUltimoStato(f.getDataUltimoStato());
                    fb.setCodiceUfficio(f.getCodiceUfficioMittente());
                    fb.setDataCreazione(sdf.format(f.getDataCreazione()));
                    fb.setNomeFile(f.getNomeFile());
                    fb.setTipoCanale(f.getTipoCanale());
                    fb.setStato(f.getStato());
                    fb.setFlag(f.getFlagWarning().getDescFlagWarning());

                    return fb;
                }).collect(Collectors.toList());

    }


    public void getCountWarningStatiFattureAttive(Exchange exchange) {

    }

    public NotificheAttivaFromSdiManager getNotificheAttivaFromSdiManager() {
        return notificheAttivaFromSdiManager;
    }

    public void setNotificheAttivaFromSdiManager(NotificheAttivaFromSdiManager notificheAttivaFromSdiManager) {
        this.notificheAttivaFromSdiManager = notificheAttivaFromSdiManager;
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

    public FatturaAttivaNotificheManager getFatturaAttivaNotificheManager() {
        return fatturaAttivaNotificheManager;
    }

    public void setFatturaAttivaNotificheManager(FatturaAttivaNotificheManager fatturaAttivaNotificheManager) {
        this.fatturaAttivaNotificheManager = fatturaAttivaNotificheManager;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Integer getIntervalDecTerm() {
        return intervalDecTerm;
    }

    public void setIntervalDecTerm(Integer intervalDecTerm) {
        this.intervalDecTerm = intervalDecTerm;
    }

    public Integer getIntervalAttTrasm() {
        return intervalAttTrasm;
    }

    public void setIntervalAttTrasm(Integer intervalAttTrasm) {
        this.intervalAttTrasm = intervalAttTrasm;
    }

    public MonitorFatturaAttivaManager getMonitorFatturaAttivaManager() {
        return monitorFatturaAttivaManager;
    }

    public void setMonitorFatturaAttivaManager(MonitorFatturaAttivaManager monitorFatturaAttivaManager) {
        this.monitorFatturaAttivaManager = monitorFatturaAttivaManager;
    }
}
