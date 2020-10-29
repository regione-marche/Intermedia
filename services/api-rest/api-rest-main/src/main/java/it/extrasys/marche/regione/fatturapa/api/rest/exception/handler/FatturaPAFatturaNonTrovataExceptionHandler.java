package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class FatturaPAFatturaNonTrovataExceptionHandler implements ExceptionMapper<FatturaPAFatturaNonTrovataException> {


    @Override
    public Response toResponse(FatturaPAFatturaNonTrovataException e) {
        Response.Status status;

        status = Response.Status.NOT_FOUND;

        return Response.status(status).header("exception", e.getMessage()).build();
    }
}