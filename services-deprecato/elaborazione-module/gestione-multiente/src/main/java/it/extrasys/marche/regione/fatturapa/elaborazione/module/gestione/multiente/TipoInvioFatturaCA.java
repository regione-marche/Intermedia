package it.extrasys.marche.regione.fatturapa.elaborazione.module.gestione.multiente;

public enum TipoInvioFatturaCA {

    INVIO_SINGOLO("InvioSingolo"),
    PROTOCOLLAZIONE("Protocollazione"),
    REGISTRAZIONE("Registrazione");

    private String value;

    TipoInvioFatturaCA(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public static TipoInvioFatturaCA fromValue(String text) {
        for (TipoInvioFatturaCA b : TipoInvioFatturaCA.values()) {
            if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}