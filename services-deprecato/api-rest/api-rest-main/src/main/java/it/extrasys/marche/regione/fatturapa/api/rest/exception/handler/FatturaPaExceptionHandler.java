package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;


import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class FatturaPaExceptionHandler implements ExceptionMapper<FatturaPAException> {

    public Response toResponse(FatturaPAException exception) {
        Response.Status status;

        status = Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(status).header("exception", exception.getMessage()).build();
    }

}

