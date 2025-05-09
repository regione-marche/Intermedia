package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by Agosteeno <agostino.leoni@extrasys.it> on 06/02/2019.
 */
public class FatturaPAValidazioneFallitaException extends Exception {

    public FatturaPAValidazioneFallitaException(){
        super();
    }

    public FatturaPAValidazioneFallitaException(String message) {
        super(message);
    }

    public FatturaPAValidazioneFallitaException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPAValidazioneFallitaException(Throwable cause){
        super(cause);
    }

}
