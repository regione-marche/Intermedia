package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model.FatturaFtpModel;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificaDecorrenzaTerminiManager;
import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestioneNotificaDecorrenzaTerminiManager {

    private FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager;
    private DatiFatturaManager datiFatturaManager;

    public void getDecorrenzaTerminiByEnte(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException {
        EnteEntity ente = (EnteEntity) exchange.getIn().getBody();
        List<FatturaFtpModel> fatturaModels = new ArrayList<>();

        List<NotificaDecorrenzaTerminiEntity> notificaDecorrenzaTerminiFtpByEnte = fatturazionePassivaNotificaDecorrenzaTerminiManager.getNotificaDecorrenzaTerminiFtpByEnte(ente.getCodiceUfficio());

        if (notificaDecorrenzaTerminiFtpByEnte.size() > 0) {
            fatturaModels = mapNotificaDecorrenzaTerminiToFatturaModel(notificaDecorrenzaTerminiFtpByEnte, ente.getCodiceUfficio());
        }

        exchange.getIn().setBody(fatturaModels);
    }


    private List<FatturaFtpModel> mapNotificaDecorrenzaTerminiToFatturaModel(List<NotificaDecorrenzaTerminiEntity> decorrenzaTermini, String codiceUfficio) {
        String codEnte = decorrenzaTermini.get(0).getIdFiscaleCommittente();

        return decorrenzaTermini.stream()
                .map(d -> {
                    FatturaFtpModel fm = new FatturaFtpModel();
                    try {
                        List<DatiFatturaEntity> fatturaByIdentificativoSDI = datiFatturaManager.getFatturaByIdentificativoSDI(d.getIdentificativoSdi());
                        fm.setContenutoFattura(d.getContenutoFile());
                        fm.setIdFattura(fatturaByIdentificativoSDI.get(0).getIdDatiFattura()/*d.getIdNotificaDecorrenzaTermini()*/);
                        fm.setTipoFattura(FatturaFtpModel.DECORRENZA_TERMINI);
                        fm.setNomeFile(d.getNomeFile());
                        fm.setIdFiscaleEnte(codEnte);
                        fm.setCodiceUfficio(codiceUfficio);
                        fm.setIdentificativoSdI(d.getIdentificativoSdi());

                    } catch (FatturaPaPersistenceException e) {
                        e.printStackTrace();
                    } catch (FatturaPAException e) {
                        e.printStackTrace();
                    }



                    return fm;
                }).collect(Collectors.toList());
    }


    public FatturazionePassivaNotificaDecorrenzaTerminiManager getFatturazionePassivaNotificaDecorrenzaTerminiManager() {
        return fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public void setFatturazionePassivaNotificaDecorrenzaTerminiManager(FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager) {
        this.fatturazionePassivaNotificaDecorrenzaTerminiManager = fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}
