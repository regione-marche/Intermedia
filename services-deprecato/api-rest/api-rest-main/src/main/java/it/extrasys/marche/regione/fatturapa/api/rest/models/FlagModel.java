package it.extrasys.marche.regione.fatturapa.api.rest.models;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodificaFlagWarningEntity;

public class FlagModel {
    private CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag;
    private String descrizione;

    public CodificaFlagWarningEntity.CODICI_FLAG_WARNING getFlag() {
        return flag;
    }

    public void setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag) {
        this.flag = flag;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
