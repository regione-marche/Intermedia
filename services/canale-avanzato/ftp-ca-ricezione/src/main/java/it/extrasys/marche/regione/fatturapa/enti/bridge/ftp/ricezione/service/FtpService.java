package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.service;

import javax.ws.rs.*;

@Path("/ftp")
public interface FtpService {

    /*
    Servizio che crea le nuove rotte dinamiche in caso di inserimento di un nuovo ente FTP
    Inoltre puo' essere utilizzata per forzare la pulizia delle rotte 'zombie'
     */
    @Path("/creaRotta")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String servizioCreaRottaPOST();

    /*
    Servizio che aggiorna le rotte dinamiche in caso di modifica delle informazioni un ente
     */
    @Path("/aggiornaRotta/{id_ente}")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    public String servizioCreaRottaPUT(@PathParam("id_ente") Integer idEnte);
}
