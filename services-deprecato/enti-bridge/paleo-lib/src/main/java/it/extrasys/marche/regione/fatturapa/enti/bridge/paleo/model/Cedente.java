package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CedentePrestatoreType;

import java.io.Serializable;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 06/03/15.
 */
public class Cedente  implements Serializable{

    private String codiceRubrica;

    private CedentePrestatoreType cedentePrestatoreType;

    public String getCodiceRubrica() {
        return codiceRubrica;
    }

    public void setCodiceRubrica(String codiceRubrica) {
        this.codiceRubrica = codiceRubrica;
    }

    public CedentePrestatoreType getCedentePrestatoreType() {
        return cedentePrestatoreType;
    }

    public void setCedentePrestatoreType(CedentePrestatoreType cedentePrestatoreType) {
        this.cedentePrestatoreType = cedentePrestatoreType;
    }
}
