package it.extrasys.marche.regione.fatturapa.core.exceptions;

public class FatturaPAMaxSizeException extends Exception {

    public FatturaPAMaxSizeException(){
        super();
    }

    public FatturaPAMaxSizeException(String message) {
        super(message);
    }

    public FatturaPAMaxSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatturaPAMaxSizeException(Throwable cause){
        super(cause);
    }

}