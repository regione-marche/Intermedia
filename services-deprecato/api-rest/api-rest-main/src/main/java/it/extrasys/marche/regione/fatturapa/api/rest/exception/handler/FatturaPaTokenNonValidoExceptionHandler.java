package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPATokenNonValidoException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class FatturaPaTokenNonValidoExceptionHandler implements ExceptionMapper<FatturaPATokenNonValidoException> {

    @Override
    public Response toResponse(FatturaPATokenNonValidoException fatturaPATokenNonValidoException) {
        Response.Status status;

        status = Response.Status.UNAUTHORIZED;

        return Response.status(status).header("exception", fatturaPATokenNonValidoException.getMessage()).build();
    }
}
