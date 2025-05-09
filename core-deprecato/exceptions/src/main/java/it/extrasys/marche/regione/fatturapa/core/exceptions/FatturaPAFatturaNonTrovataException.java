package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 16/02/15.
 */
public class FatturaPAFatturaNonTrovataException extends FatturaPAFatalException {

    public FatturaPAFatturaNonTrovataException() {
        super();
    }

    public FatturaPAFatturaNonTrovataException(String message) {
        super(message);
    }

    public FatturaPAFatturaNonTrovataException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPAFatturaNonTrovataException(Throwable cause) {
        super(cause);
    }
}
