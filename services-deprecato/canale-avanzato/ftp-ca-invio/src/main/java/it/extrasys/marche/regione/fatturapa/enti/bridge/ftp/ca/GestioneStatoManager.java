package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model.FatturaFtpModel;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaNotificheManager;
import org.apache.camel.Exchange;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestioneStatoManager {

    private DatiFatturaManager datiFatturaManager;
    private FatturaAttivaNotificheManager fatturaAttivaNotificheManager;

    public void aggiornaStatoFattureFtp(Exchange exchange) throws FatturaPAException {
        String nomeFileZip = (String) exchange.getIn().getHeader("nomeFileZip");
        String codiceStatoFattura = (String) exchange.getIn().getHeader("codiceStatoFattura");
        Map<String, List<FatturaFtpModel>> mapFatture = (Map<String, List<FatturaFtpModel>>) exchange.getProperty("mapFattureModel");
        List<FatturaFtpModel> fatturaModelList = mapFatture.get(nomeFileZip);

        List<BigInteger> idFatture = fatturaModelList.stream()
                .map(f -> f.getIdFattura())
                .collect(Collectors.toList());

        String codiceEnte = fatturaModelList.get(0).getIdFiscaleEnte();

        datiFatturaManager.aggiornaStatoFattureFtp(idFatture, codiceStatoFattura, nomeFileZip, codiceEnte);
    }


    public void aggiornaStatoFattureAttiveFtp(Exchange exchange) throws FatturaPAException {
        String nomeFileZip = (String) exchange.getIn().getHeader("nomeFileZip");
        String codiceStatoFattura = (String) exchange.getIn().getHeader("codiceStatoFattura");
        //la mappa ha come chiave il nome del file zip e valori la lista delle notifiche delle fatture attive
        Map<String, List<FatturaFtpModel>> mapFatture = (Map<String, List<FatturaFtpModel>>) exchange.getProperty("mapFattureModel");

        List<FatturaFtpModel> fatturaModelList = mapFatture.get(nomeFileZip);

        //Gruppo per tipo di notifica per passarle al manager.
        Map<String, List<BigInteger>> mapNotifiche = fatturaModelList.stream()
                .collect(Collectors.groupingBy(FatturaFtpModel::getTipoFattura, Collectors.mapping(FatturaFtpModel::getIdFattura, Collectors.toList())));

        String codiceEnte = fatturaModelList.get(0).getIdFiscaleEnte();

        fatturaAttivaNotificheManager.aggiornaStatoFatturaAttivaNotifiche(mapNotifiche, nomeFileZip, codiceStatoFattura, codiceEnte);
    }


    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public FatturaAttivaNotificheManager getFatturaAttivaNotificheManager() {
        return fatturaAttivaNotificheManager;
    }

    public void setFatturaAttivaNotificheManager(FatturaAttivaNotificheManager fatturaAttivaNotificheManager) {
        this.fatturaAttivaNotificheManager = fatturaAttivaNotificheManager;
    }
}
