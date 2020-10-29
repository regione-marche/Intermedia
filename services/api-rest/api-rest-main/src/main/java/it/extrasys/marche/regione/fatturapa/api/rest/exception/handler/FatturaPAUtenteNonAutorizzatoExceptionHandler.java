package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;


import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonAutorizzatoException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class FatturaPAUtenteNonAutorizzatoExceptionHandler implements ExceptionMapper<FatturaPAUtenteNonAutorizzatoException> {

    public Response toResponse(FatturaPAUtenteNonAutorizzatoException exception) {
        Response.Status status;

        status = Response.Status.UNAUTHORIZED;

        return Response.status(status).header("exception", exception.getMessage()).build();
    }

}

