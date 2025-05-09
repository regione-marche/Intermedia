package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model.FatturaFtpModel;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.sdi.NotificaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificaFromSdiManager;
import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestioneScartoEsitoManager {

    private NotificaFromSdiManager notificaFromSdiManager;
    private DatiFatturaManager datiFatturaManager;

    public void getScartoEsitoByEnte(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException {
        EnteEntity ente = (EnteEntity) exchange.getIn().getBody();
        List<FatturaFtpModel> fatturaModels = new ArrayList<>();
        //List<NotificaFromSdiEntity> scartoEsitoFtpByEnte = notificaFromSdiManager.getScartoEsitoFtpByEnte(ente.getCodiceUfficio());
        List<NotificaFromSdiEntity> scartoEsitoFtpByEnte = notificaFromSdiManager.getScartoEsitoFtpByEnteG1G4(ente.getCodiceUfficio());
        if (scartoEsitoFtpByEnte.size() > 0) {

            fatturaModels = mapScartoEsitoToFatturaModels(scartoEsitoFtpByEnte, ente.getCodiceUfficio());
        }
        exchange.getIn().setBody(fatturaModels);
    }


    private List<FatturaFtpModel> mapScartoEsitoToFatturaModels(List<NotificaFromSdiEntity> scartoEsitoFtpByEnte, String codiceUfficio) {
        return scartoEsitoFtpByEnte.stream()
                .map(d -> {
                    FatturaFtpModel fm = new FatturaFtpModel();

                    //Recupera l'id_dati_fattura
                    try {
                        List<DatiFatturaEntity> fatturaByIdentificativoSDI = datiFatturaManager.getFatturaByIdentificativoSDI(d.getIdentificativoSdI());
                        fm.setContenutoFattura(d.getContenutoFile().getBytes());
                        fm.setIdFattura(fatturaByIdentificativoSDI.get(0).getIdDatiFattura());
                        fm.setTipoFattura(FatturaFtpModel.DECORRENZA_TERMINI);
                        fm.setNomeFile(d.getNomeFileScarto());
                        fm.setIdFiscaleEnte(fatturaByIdentificativoSDI.get(0).getCommittenteIdFiscaleIVA());
                        fm.setCodiceUfficio(codiceUfficio);
                        fm.setIdentificativoSdI(d.getIdentificativoSdI());
                    } catch (FatturaPaPersistenceException e) {
                        e.printStackTrace();
                    } catch (FatturaPAException e) {
                        e.printStackTrace();
                    }
                    return fm;
                }).collect(Collectors.toList());

    }

    public NotificaFromSdiManager getNotificaFromSdiManager() {
        return notificaFromSdiManager;
    }

    public void setNotificaFromSdiManager(NotificaFromSdiManager notificaFromSdiManager) {
        this.notificaFromSdiManager = notificaFromSdiManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}
