package it.extrasys.marche.regione.fatturapa.api.rest.exception.handler;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAAssociazioneEnteCampoOpzionaleNonTrovataException;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;

public class FatturaPAAssociazioneEnteCampoOpzionaleNonTrovataExceptionHandler implements ExceptionMapper<FatturaPAAssociazioneEnteCampoOpzionaleNonTrovataException> {
    public Response toResponse(FatturaPAAssociazioneEnteCampoOpzionaleNonTrovataException exception){
        Response.Status status;
        status = Response.Status.NOT_FOUND;
        return Response.status(status).header("exception", exception.getMessage()).build();
    }
}

