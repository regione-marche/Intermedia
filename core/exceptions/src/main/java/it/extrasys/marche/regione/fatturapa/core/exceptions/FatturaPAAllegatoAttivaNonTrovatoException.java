package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by agosteeno on 17/03/15.
 */
public class FatturaPAAllegatoAttivaNonTrovatoException extends FatturaPAFatalException {
    public FatturaPAAllegatoAttivaNonTrovatoException() {
        super();
    }

    public FatturaPAAllegatoAttivaNonTrovatoException(String message) {
        super(message);
    }

    public FatturaPAAllegatoAttivaNonTrovatoException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPAAllegatoAttivaNonTrovatoException(Throwable cause) {
        super(cause);
    }
}
