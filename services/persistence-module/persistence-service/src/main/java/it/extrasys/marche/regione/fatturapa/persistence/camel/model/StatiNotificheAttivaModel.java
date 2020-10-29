package it.extrasys.marche.regione.fatturapa.persistence.camel.model;

import java.util.Date;

public class StatiNotificheAttivaModel {

    private String stato;

    private Date dataRicezioneFromSdi;

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Date getDataRicezioneFromSdi() {
        return dataRicezioneFromSdi;
    }

    public void setDataRicezioneFromSdi(Date dataRicezioneFromSdi) {
        this.dataRicezioneFromSdi = dataRicezioneFromSdi;
    }
}
