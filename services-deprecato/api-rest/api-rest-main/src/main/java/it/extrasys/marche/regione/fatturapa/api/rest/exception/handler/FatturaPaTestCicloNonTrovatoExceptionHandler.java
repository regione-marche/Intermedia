package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaTestCicloNonTrovatoException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class FatturaPaTestCicloNonTrovatoExceptionHandler implements ExceptionMapper<FatturaPaTestCicloNonTrovatoException> {

    public Response toResponse(FatturaPaTestCicloNonTrovatoException exception) {
        Response.Status status;

        status = Response.Status.NOT_FOUND;

        return Response.status(status).header("exception", exception.getMessage()).build();
    }

}

