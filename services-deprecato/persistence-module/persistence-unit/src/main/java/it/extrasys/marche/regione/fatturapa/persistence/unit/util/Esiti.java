package it.extrasys.marche.regione.fatturapa.persistence.unit.util;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 07/02/15.
 */
public enum Esiti {

    SCARTATA("EC02"),
    ACCETTATA("EC01");

    private String codice;

    public String getCodice() {
        return this.codice;
    }

    private Esiti(String codice) {
        this.codice = codice;
    }

    public static Esiti fromCodice(String v) {
        for (Esiti c: Esiti.values()) {
            if (c.codice.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v+"");
    }
}
