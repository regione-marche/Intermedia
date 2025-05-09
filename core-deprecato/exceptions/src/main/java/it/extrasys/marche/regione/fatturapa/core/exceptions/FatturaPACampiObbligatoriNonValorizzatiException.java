package it.extrasys.marche.regione.fatturapa.core.exceptions;

public class FatturaPACampiObbligatoriNonValorizzatiException extends FatturaPAFatalException {

    public FatturaPACampiObbligatoriNonValorizzatiException() {
        super();
    }

    public FatturaPACampiObbligatoriNonValorizzatiException(String message) {
        super(message);
    }

    public FatturaPACampiObbligatoriNonValorizzatiException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPACampiObbligatoriNonValorizzatiException(Throwable cause) {
        super(cause);
    }
}
