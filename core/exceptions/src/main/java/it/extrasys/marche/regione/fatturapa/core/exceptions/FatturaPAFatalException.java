package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 29/01/15.
 */
public class FatturaPAFatalException extends Exception {

    public FatturaPAFatalException(){
        super();
    }

    public FatturaPAFatalException(String message) {
        super(message);
    }

    public FatturaPAFatalException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPAFatalException(Throwable cause){
        super(cause);
    }

}
