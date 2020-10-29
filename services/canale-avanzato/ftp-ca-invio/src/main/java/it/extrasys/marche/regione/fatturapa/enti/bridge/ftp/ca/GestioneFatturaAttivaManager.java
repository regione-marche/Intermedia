package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model.FatturaFtpModel;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.NotificheAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaNotificheManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificheAttivaFromSdiManager;
import org.apache.camel.Exchange;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestioneFatturaAttivaManager {

    private NotificheAttivaFromSdiManager notificheAttivaFromSdiManager;
    private FatturaAttivaNotificheManager fatturaAttivaNotificheManager;


    public void getFatturaAttivaByEnte(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException {
        EnteEntity ente = (EnteEntity) exchange.getIn().getBody();

        //Recupero tutte le notifiche attive per l'ente
        List<NotificheAttivaFromSdiEntity> notificheAttivaByEnte = notificheAttivaFromSdiManager.getNotificheAttivaByEnte(ente);

        //Le raggruppo per tipologia di notifica e le mappo in fatturaModel. La chiave è il tipo di notifica
        Map<String, List<FatturaFtpModel>> mapFatturaModel = notificheAttivaByEnte.stream()
                .map(f -> {
                    FatturaFtpModel fm = new FatturaFtpModel();
                    fm.setContenutoFattura(f.getOriginalMessage().getBytes(StandardCharsets.UTF_8));
                    fm.setIdFattura(f.getIdFatturaAttiva());
                    fm.setTipoFattura(f.getTipoNotificaAttivaFromSdiEntity().getCodTipoNotificaFromSdi().name());
                    fm.setNomeFile(f.getNomeFile());
                    fm.setIdFiscaleEnte(ente.getIdFiscaleCommittente());
                    return fm;
                }).collect(Collectors.groupingBy(FatturaFtpModel::getTipoFattura));


        List<FatturaFtpModel> fatturaModelsDaInviare = new ArrayList<>();

        //A secondo del tipo di notifica recupero quelle con ultimo stato = 'ricevuto'. Cioè solo quelle da inviare
        for (Map.Entry<String, List<FatturaFtpModel>> f : mapFatturaModel.entrySet()) {
            List<FatturaFtpModel> fatturaModelList = f.getValue();
            List<BigInteger> idFatturaAttive = fatturaModelList.stream().map(fm -> fm.getIdFattura()).collect(Collectors.toList());
            List<BigInteger> idFattureAttiveDaInviare = null;

            //Tutti gli id delle fatture attive con ultimo stato='ricevuto'
            if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.name().equalsIgnoreCase(f.getKey())) {
                idFattureAttiveDaInviare = fatturaAttivaNotificheManager.getIdFatturaAttivaRicevutaConsegnaUltimoStatoRicevutaByIdFatturaAttiva(idFatturaAttive);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.name().equalsIgnoreCase(f.getKey())) {
                idFattureAttiveDaInviare = fatturaAttivaNotificheManager.getIdFatturaAttivaNotificaMancataConsegnaUltimoStatoRicevutaByIdFatturaAttiva(idFatturaAttive);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.name().equalsIgnoreCase(f.getKey())) {
                idFattureAttiveDaInviare = fatturaAttivaNotificheManager.getIdFatturaAttivaNotificaScartoUltimoStatoRicevutaByIdFatturaAttiva(idFatturaAttive);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.name().equalsIgnoreCase(f.getKey())) {
                idFattureAttiveDaInviare = fatturaAttivaNotificheManager.getIdFatturaAttivaNotificaEsitoUltimoStatoRicevutaByIdFatturaAttiva(idFatturaAttive);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.name().equalsIgnoreCase(f.getKey())) {
                idFattureAttiveDaInviare = fatturaAttivaNotificheManager.getFatturaAttivaNotificaDecorrenzaTerminiUltimoStatoRicevutaByIdFatturaAttiva(idFatturaAttive);
            } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.name().equalsIgnoreCase(f.getKey())) {
                idFattureAttiveDaInviare = fatturaAttivaNotificheManager.getIdFatturaAttivaAttestazioneTrasmissioneFatturaUltimoStatoRicevutaByIdFatturaAttiva(idFatturaAttive);
            }

            //Rimuove da fatturaModelList quelle che sono già state inviate!
            fatturaModelsDaInviare.addAll(filtraFattureAttiveDaInviare(fatturaModelList, idFattureAttiveDaInviare));
        }

        exchange.getIn().setBody(fatturaModelsDaInviare);
    }


    private List<FatturaFtpModel> filtraFattureAttiveDaInviare(List<FatturaFtpModel> fatturaModelList, List<BigInteger> fattureAttiveDaInviare) {
        //nessuna notifica da inviare
        if (fattureAttiveDaInviare.isEmpty()) {
            fatturaModelList.clear();
        } else {
            //Rimuove dalla lista di fattireModel, tutte le notifiche che hanno ultimo stato='inviato'
            fatturaModelList.removeIf(f-> !fattureAttiveDaInviare.contains(f.getIdFattura()));
        }
        return fatturaModelList;
    }


    public NotificheAttivaFromSdiManager getNotificheAttivaFromSdiManager() {
        return notificheAttivaFromSdiManager;
    }

    public void setNotificheAttivaFromSdiManager(NotificheAttivaFromSdiManager notificheAttivaFromSdiManager) {
        this.notificheAttivaFromSdiManager = notificheAttivaFromSdiManager;
    }


    public FatturaAttivaNotificheManager getFatturaAttivaNotificheManager() {
        return fatturaAttivaNotificheManager;
    }

    public void setFatturaAttivaNotificheManager(FatturaAttivaNotificheManager fatturaAttivaNotificheManager) {
        this.fatturaAttivaNotificheManager = fatturaAttivaNotificheManager;
    }
}
