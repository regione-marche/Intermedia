package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 29/01/15.
 */
public class FatturaPAException extends Exception {

    public FatturaPAException(){
        super();
    }

    public FatturaPAException(String message) {
        super(message);
    }

    public FatturaPAException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPAException(Throwable cause){
        super(cause);
    }



}
