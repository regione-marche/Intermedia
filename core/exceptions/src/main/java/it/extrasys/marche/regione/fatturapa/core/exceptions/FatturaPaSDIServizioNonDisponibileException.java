package it.extrasys.marche.regione.fatturapa.core.exceptions;

/**
 * Created by agosteeno on 23/02/15.
 */
public class FatturaPaSDIServizioNonDisponibileException extends Exception {

    public FatturaPaSDIServizioNonDisponibileException(){
        super();
    }

    public FatturaPaSDIServizioNonDisponibileException(String message) {
        super(message);
    }

    public FatturaPaSDIServizioNonDisponibileException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPaSDIServizioNonDisponibileException(Throwable cause){
        super(cause);
    }

}
