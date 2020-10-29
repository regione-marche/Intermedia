package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class FatturaPaPersistenceExceptionHandler implements ExceptionMapper<FatturaPaPersistenceException> {

    public Response toResponse(FatturaPaPersistenceException exception) {
        Response.Status status;

        status = Response.Status.BAD_REQUEST;

        return Response.status(status).header("exception", exception.getMessage()).build();
    }

}

