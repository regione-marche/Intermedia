package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;


import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class BadRequestExceptionHandler implements ExceptionMapper<BadRequestException> {

    public Response toResponse(BadRequestException exception) {
        Response.Status status;

        status = Response.Status.BAD_REQUEST;

        return Response.status(status).header("exception", exception.getMessage()).build();
    }

}

