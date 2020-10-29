package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by agosteeno on 14/03/15.
 */
public class FatturaPAEnteNonTrovatoException extends FatturaPAFatalException {

    public FatturaPAEnteNonTrovatoException() {
        super();
    }

    public FatturaPAEnteNonTrovatoException(String message) {
        super(message);
    }

    public FatturaPAEnteNonTrovatoException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPAEnteNonTrovatoException(Throwable cause) {
        super(cause);
    }
}
