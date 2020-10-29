package it.extrasys.marche.regione.fatturapa.core.exceptions;

public class FatturaPAStatoCodeNonTrovatoException extends Exception {
    public FatturaPAStatoCodeNonTrovatoException() {
        super();
    }

    public FatturaPAStatoCodeNonTrovatoException(String message) {
        super(message);
    }

    public FatturaPAStatoCodeNonTrovatoException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPAStatoCodeNonTrovatoException(Throwable cause) {
        super(cause);
    }
}
