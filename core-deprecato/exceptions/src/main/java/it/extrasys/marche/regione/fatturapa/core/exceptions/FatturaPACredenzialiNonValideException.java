package it.extrasys.marche.regione.fatturapa.core.exceptions;

public class FatturaPACredenzialiNonValideException extends FatturaPAFatalException {

    public FatturaPACredenzialiNonValideException() {
        super();
    }

    public FatturaPACredenzialiNonValideException(String message) {
        super(message);
    }

    public FatturaPACredenzialiNonValideException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPACredenzialiNonValideException(Throwable cause) {
        super(cause);
    }
}
