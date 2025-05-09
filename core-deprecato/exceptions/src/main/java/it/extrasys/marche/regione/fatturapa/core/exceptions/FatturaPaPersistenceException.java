package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by agosteeno on 23/02/15.
 */
public class FatturaPaPersistenceException extends Exception {

    public FatturaPaPersistenceException(){
        super();
    }

    public FatturaPaPersistenceException(String message) {
        super(message);
    }

    public FatturaPaPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPaPersistenceException(Throwable cause){
        super(cause);
    }

}
