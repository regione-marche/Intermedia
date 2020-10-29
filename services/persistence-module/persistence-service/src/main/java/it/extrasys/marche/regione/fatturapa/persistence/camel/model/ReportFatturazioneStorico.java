package it.extrasys.marche.regione.fatturapa.persistence.camel.model;

public class ReportFatturazioneStorico {

    private String identificativoSdI;
    private String esito;


    public String getIdentificativoSdI() {
        return identificativoSdI;
    }

    public void setIdentificativoSdI(String identificativoSdI) {
        this.identificativoSdI = identificativoSdI;
    }

    public String getEsito() {
        return esito;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }
}
