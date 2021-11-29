package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model.FatturaFtpModel;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.utils.FtpConstants;
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

    public void getDecorrenzaTerminiByEnte(Exchange exchange) throws FatturaPAException {
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
                        fm.setIdFattura(fatturaByIdentificativoSDI.get(0).getIdDatiFattura());
                        fm.setTipoFattura(FatturaFtpModel.DECORRENZA_TERMINI);

                        //fm.setNomeFile(d.getNomeFile());
                        //Sistemo il nome file DT
                        fm.setNomeFile(getNomeFileDT(d.getNomeFile()));

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

    private String getNomeFileDT(String nomeFile){

        String nomeFileDT = "";

        if(nomeFile.contains(FtpConstants.NOTIFICA_DT)){
            nomeFileDT = nomeFile;
        }else{
            //Fa schifo lo so... ma non ho tempo ora :(
            if(nomeFile.contains(".XML")){
                String[] split = nomeFile.split(".XML");

                if(split.length == 0){
                    nomeFileDT = nomeFile;
                }else{
                    if(!split[0].contains(".XML")) {
                        nomeFileDT = split[0] + FtpConstants.NOTIFICA_DT2;
                    }else{
                        nomeFileDT = nomeFile;
                    }
                }
            }else{
                String[] split = nomeFile.split(".xml");

                if(split.length == 0){
                    nomeFileDT = nomeFile;
                }else{
                    if(!split[0].contains(".xml")) {
                        nomeFileDT = split[0] + FtpConstants.NOTIFICA_DT2;
                    }else{
                        nomeFileDT = nomeFile;
                    }
                }
            }
        }

        return nomeFileDT;
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
