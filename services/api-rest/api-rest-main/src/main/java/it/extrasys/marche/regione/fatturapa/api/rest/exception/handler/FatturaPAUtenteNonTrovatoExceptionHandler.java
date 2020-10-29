package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonTrovatoException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class FatturaPAUtenteNonTrovatoExceptionHandler implements ExceptionMapper<FatturaPAUtenteNonTrovatoException> {

    public Response toResponse(FatturaPAUtenteNonTrovatoException exception) {
        Response.Status status;

        status = Response.Status.NOT_FOUND;

        return Response.status(status).header("exception", exception.getMessage()).build();
    }

}

