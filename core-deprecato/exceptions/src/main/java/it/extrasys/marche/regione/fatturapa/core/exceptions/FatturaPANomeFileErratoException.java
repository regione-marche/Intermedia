package it.extrasys.marche.regione.fatturapa.core.exceptions;

public class FatturaPANomeFileErratoException extends FatturaPAFatalException {

    public FatturaPANomeFileErratoException() {
        super();
    }

    public FatturaPANomeFileErratoException(String message) {
        super(message);
    }

    public FatturaPANomeFileErratoException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPANomeFileErratoException(Throwable cause) {
        super(cause);
    }
}