package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 29/01/15.
 */
public class FatturaPARecoverableException extends Exception {

    public FatturaPARecoverableException(){
        super();
    }

    public FatturaPARecoverableException(String message) {
        super(message);
    }

    public FatturaPARecoverableException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPARecoverableException(Throwable cause){
        super(cause);
    }
}
