package it.extrasys.marche.regione.fatturapa.api.rest.service;

import it.extrasys.marche.regione.fatturapa.api.rest.model.*;

import javax.ws.rs.*;
import java.util.List;

public interface RestService {

    /*
    ADMINISTRATION
     */
    @Path ("/users/token")
    @POST
    @Produces ("application/json")
    @Consumes ("application/json")
    String getToken(UserTokenRequest userTokenRequest);

    @Path("/users")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    UserDtoResponseList getUser(@QueryParam("username") String username, @QueryParam("token") String token);

    @Path("/users")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    UserIdResponse updateUser(UserDto body, @QueryParam("token") String token);

    @Path("/users")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    UserIdResponse createUser(UserDto body, @QueryParam("token") String token);

    @Path("/users/reimpostaPsw")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Void reimpostaPsw(@QueryParam("link") String link,
                      UserTokenRequest userTokenRequest);

    @Path("/users/reimpostaPsw/psw")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    UserIdResponse updateUserPassword(UserDto body, @QueryParam("token") String token);

    /*
    STATISTICS
     */
    @Path ("/stats/fatturePassive/month")
    @GET
    @Produces ("application/json")
    @Consumes ("application/json")
    StatsFatture getStatisticheFatturePassiveUltimoMese(@QueryParam("token") String token);

    @Path ("/stats/fattureAttive/month")
    @GET
    @Produces ("application/json")
    @Consumes ("application/json")
    StatsFatture getStatisticheFattureAttiveUltimoMese(@QueryParam("token") String token);

    @Path ("/stats/fatturePassive/year")
    @GET
    @Produces ("application/json")
    @Consumes ("application/json")
    StatsFatture getStatisticheFatturePassiveUltimoAnno(@QueryParam("token") String token);

    @Path ("/stats/fattureAttive/year")
    @GET
    @Produces ("application/json")
    @Consumes ("application/json")
    StatsFatture getStatisticheFattureAttiveUltimoAnno(@QueryParam("token") String token);

    @Path ("/stats/enti")
    @GET
    @Produces ("application/json")
    @Consumes ("application/json")
    StatsFatture getStatisticheEnti(@QueryParam("token") String token);

/*
ENTITIES
 */
    /**
     * Servizio utilizzato per ricercare un ente
     */
    @Path("/entities")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesResponseList servizioEntitiesGET(@QueryParam("denominazione_ente") String denominazioneEnte,
                                             @QueryParam("id_fiscale_committente") String idFiscaleCommittente,
                                             @QueryParam("codice_ufficio") String codiceUfficio,
                                             @QueryParam("token") String token);

    /**
     * Servizio utilizzato per aggiornare la configurazione di un ente
     */
    @Path("/entities")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPUT(EntitiesRequestPut body,
                                           @QueryParam("token") String token);

    /**
     * Servizio utilizzato per creare un nuovo ente
     */
    @Path("/entities")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPOST(EntitiesRequest body,
                                            @QueryParam("token") String token);

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di ricezione delle fatture
     * Tipo restituito: ACinvoicesResponse
     */
    @Path("/entities/{id_ente}/acInvoices")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    ACinvoicesResponse servizioEntitiesActiveCycleRicezioneFattureGET(
            @PathParam("id_ente") Integer idEnte,
            @QueryParam("token") String token);

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione del sistema di ricezione delle fatture
     */
    @Path("/entities/{id_ente}/acInvoices")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesActiveCycleRicezioneFatturePUT(@PathParam("id_ente") Integer idEnte,
                                                                      ACinvoicesRequest body,
                                                                      @QueryParam("token") String token);


    /**
     * Servizio utilizzato per configurare l’integrazione del sistema di ricezione delle fatture
     */
    @Path("/entities/{id_ente}/acInvoices")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesActiveCycleRicezioneFatturePOST(@PathParam("id_ente") Integer idEnte,
                                                                       ACinvoicesRequest body,
                                                                       @QueryParam("token") String token);

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di inoltro delle notifiche
     */
    @Path("/entities/{id_ente}/acNotifications")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    ACnotificationsResponse servizioEntitiesActiveCycleInoltroNotificheGET(
            @PathParam("id_ente") Integer idEnte,
            @QueryParam("token") String token);

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione del sistema di inoltro delle notifiche
     */
    @Path("/entities/{id_ente}/acNotifications")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesActiveCycleInoltroNotifichePUT(@PathParam("id_ente") Integer idEnte,
                                                                      ACnotificationsRequest body,
                                                                      @QueryParam("token") String token);

    /**
     * Servizio utilizzato per configurare l’integrazione del sistema di inoltro delle notifiche
     */
    @Path("/entities/{id_ente}/acNotifications")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesActiveCycleInoltroNotifichePOST(@PathParam("id_ente") Integer idEnte,
                                                                       ACnotificationsRequest body,
                                                                       @QueryParam("token") String token);

    //--------------------------------------------------------------------------------------------------------------------

    /**
     * Servizio utilizzato per gestire il passaggio dall’ambiente di staging all’ambiente di produzione del Ciclo Attivo
     */
    @Path("/entities/{id_ente}/acProduction")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesActiveCyclePassaggioProduzionePUT(@PathParam("id_ente") Integer idEnte,
                                                                         @QueryParam("token") String token);

    /**
     * Servizio utilizzato per recuperare lo storico dei test effettuati nel ciclo attivo
     */
    @Path("/entities/{id_ente}/acTestHistory")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    TestACHistoryDto servizioEntitiesActiveCycleStoricoTestGET(@PathParam("id_ente") Integer idEnte,
                                                               @QueryParam("token") String token);

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di ricezione esito committente
     */
    @Path("/entities/{id_ente}/pcEc")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    PCesitoCommittenteResponse servizioEntitiesPassiveCycleEsitoCommittenteGET(
            @PathParam("id_ente") Integer idEnte,
            @QueryParam("token") String token);

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione del sistema di ricezione esito committente
     */
    @Path("/entities/{id_ente}/pcEc")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPassiveCycleEsitoCommittentePUT(@PathParam("id_ente") Integer idEnte,
                                                                       PCesitoCommittenteRequest body,
                                                                       @QueryParam("token") String token);

    /**
     * Servizio utilizzato per configurare l’integrazione del sistema di ricezione esito committente
     */
    @Path("/entities/{id_ente}/pcEc")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPassiveCycleEsitoCommittentePOST(@PathParam("id_ente") Integer idEnte,
                                                                        PCesitoCommittenteRequest body,
                                                                        @QueryParam("token") String token);

    /**
     * Servizio utilizzato per gestire il passaggio dall’ambiente di staging all’ambiente di produzione
     */
    @Path("/entities/{id_ente}/pcProduction")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPassiveCyclePassaggioProduzionePUT(@PathParam("id_ente") Integer idEnte,
                                                                          @QueryParam("token") String token);

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di protocollo
     */
    @Path("/entities/{id_ente}/pcProtocol")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    PCprotocolResponse servizioEntitiesPassiveCycleProtocolloGET(@PathParam("id_ente") Integer idEnte,
                                                                 @QueryParam("token") String token);

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione con il sistema di protocollo
     */
    @Path("/entities/{id_ente}/pcProtocol")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPassiveCycleProtocolloPUT(@PathParam("id_ente") Integer idEnte,
                                                                 PCprotocolRequest body,
                                                                 @QueryParam("token") String token);

    /**
     * Servizio utilizzato per configurare l’integrazione con il sistema di protocollo
     */
    @Path("/entities/{id_ente}/pcProtocol")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPassiveCycleProtocolloPOST(@PathParam("id_ente") Integer idEnte,
                                                                  PCprotocolRequest body,
                                                                  @QueryParam("token") String token);

    /**
     * Servizio utilizzato per recuperare la configurazione dell’integrazione del sistema di registrazione
     */
    @Path("/entities/{id_ente}/pcRegistration")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    PCregistrationResponse servizioEntitiesPassiveCycleRegistrazioneGET(
            @PathParam("id_ente") Integer idEnte,
            @QueryParam("token") String token);

    /**
     * Servizio utilizzato per aggiornare la configurazione dell’integrazione del sistema di registrazione
     */
    @Path("/entities/{id_ente}/pcRegistration")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPassiveCycleRegistrazionePUT(@PathParam("id_ente") Integer idEnte,
                                                                    PCregistrationRequest body,
                                                                    @QueryParam("token") String token);

    /**
     * Servizio utilizzato per configurare l’integrazione del sistema di registrazione
     */
    @Path("/entities/{id_ente}/pcRegistration")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse servizioEntitiesPassiveCycleRegistrazionePOST(@PathParam("id_ente") Integer idEnte,
                                                                     PCregistrationRequest body,
                                                                     @QueryParam("token") String token);

    /**
     * Servizio utilizzato per recuperare lo storico dei test effettuati nel ciclo passivo
     */
    @Path("/entities/{id_ente}/pcTestHistory")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    TestPCHistoryDto servizioEntitiesPassiveCycleStoricoTestGET(@PathParam("id_ente") Integer idEnte,
                                                                @QueryParam("token") String token);

    /**
     * Servizio utilizzato per effettuare test del ciclo passivo
     */
    @Path("/entities/{id_ente}/pcTestRun")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    int servizioEntitiesPassiveCycleTestRunPOST(@PathParam("id_ente") Integer idEnte,
                                                @QueryParam("token") String token);

    @Path("/entities/TipoCanaleCa")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    TipoCanaleCaList getAllTipoCanaleCa(@QueryParam("token") String token);

    @Path("/entities/{id_ente}/configPassiveCycle")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    ConfigPassiveCycleDTO getConfigPassiveCycle(@PathParam("id_ente") Integer idEnte,
                                                @QueryParam("token") String token);

    @Path("/entities/{id_ente}/configActiveCycle")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    ConfigActiveCycleDTO getConfigActiveCycle(@PathParam("id_ente") Integer idEnte,
                                              @QueryParam("token") String token);

    @Path("/entities/campiOpzionali")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    CampiOpzionaliDto getAllCampiOpzionali(@QueryParam("token") String token);


    @Path("/entities/{id_ente}/campiOpzionali")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    CampiOpzionaliDto getAllCampiOpzionaliEnte(@PathParam("id_ente") Integer idEnte,
                                               @QueryParam("token") String token);


    @Path("/entities/{id_ente}/campiOpzionali")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse setCampiOpzionaliEnte(@PathParam("id_ente") Integer idEnte,
                                             CampiOpzionaliDto body,
                                             @QueryParam("token") String token);

    @Path("/entities/{id_ente}/campiOpzionali")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    EntitiesIdResponse updateCampiOpzionaliEnte(@PathParam("id_ente") Integer idEnte,
                                                CampiOpzionaliDto body,
                                                @QueryParam("token") String token);

    /*
    INVOICES
     */
    /**
     * Servizio utilizzato per recuperare gli id e Nome tag XML del File Fattura
     */
    @Path("/invoices")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    List<InvoiceFieldsDetail> servizioInvoicesCampiGET(@QueryParam("token") String token);

    /**
     * Servizio utilizzato per recuperare il numero di fatture passive
     */
    @Path("/invoices/pc/count")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    int servizioCountInvoicesPassiveCyclePOST(InvoicesPCRequest body,
                                              @QueryParam("token") String token);
    /**
     * Servizio utilizzato per ricercare le fatture passive
     */
    @Path("/invoices/pc")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    InvoicesPCResponse servizioInvoicesPassiveCyclePOST(InvoicesPCRequest body,
                                                        @QueryParam("token") String token);

    /**
     * Servizio utilizzato per mostrare il dettaglio delle fatture passive
     */
    @Path("/invoices/pc/{identificativo_sdi}")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    InvoicePCDetail servizioInvoicesPassiveCycleDettaglioGET(
            @PathParam("identificativo_sdi") Integer identificativoSdi,
            @QueryParam("token") String token);

    /**
     * Servizio utilizzato per ricercare le fatture attive
     */
    @Path("/invoices/ac/")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    InvoicesACResponse servizioInvoicesActiveCyclePOST(InvoicesACRequest body,
                                                       @QueryParam("token") String token);

    /**
     * Servizio utilizzato per mostrare il dettaglio delle fatture attive
     */
    @Path("/invoices/ac/{identificativo_sdi}/")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    InvoiceACDetail servizioInvoicesActiveCycleDettaglioGET(
            @PathParam("identificativo_sdi") Integer identificativoSdi,
            @QueryParam("token") String token);

    @Path("/invoices/fileFatturaPassiva")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    public FileFattura getFileFatturaPassiva(@QueryParam("identificativo_sdi") Integer identificativoSdi,
                                             @QueryParam("token") String token);

    @Path("/invoices/fileFatturaAttiva")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    public FileFattura getFileFatturaAttiva(@QueryParam("identificativo_sdi") Integer identificativoSdi,
                                            @QueryParam("token") String token);


    /*
    MONITORAGGIO
     */

    /*GUI M0 - Messaggi da Ri-Elaborare Attiva
    Restituisce tutte le code DLQ con il numero di messaggi presenti
    */
    @Path("/monitoraggio/acStatoCode")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    MonitoraggioResponseList acServizioMessaggiGET(@QueryParam("token") String token);


    /*GUI M0 - Messaggi da Ri-Elaborare Passiva
  Restituisce tutte le code DLQ con il numero di messaggi presenti
  */
    @Path("/monitoraggio/pcStatoCode")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    MonitoraggioResponseList pcServizioMessaggiGET(@QueryParam("token") String token);


    /*GUI M0 - Messaggi da Ri-Elaborare Attiva e Passiva
    Forza la rielaborazione dei messaggi presenti in una o tutte le code
   */
    @Path("/monitoraggio/statoCode")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    RielaboraMessaggiResponse servizioMessaggiPOST(RielaboraMessaggiRequest body, @QueryParam("token") String token);


    /*GUI M0 - Warning Stati Fatture Attive tabella e download
     */
    @Path("/monitoraggio/acStatoFatture/warning")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    WarningStatiFatturaResponseList acServizioWarningStatiFattureGET(@QueryParam("token") String token, OrderRequest orderRequest);


    @Path("/monitoraggio/acStatoFatture/warning/count")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    WarningStatiFatturaCountResponse acServizioCountWarningStatiFattureGET(@QueryParam("token") String token);


    /*GUI M0 -  Stati Fatture Attive per download
     */
    @Path("/monitoraggio/acStatoFatture")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    MonitoraggioReportResponse acServizioStatiFattureGET(@QueryParam("token") String token);

    /*GUI M0 - Warning Stati Fatture Passive tabella e download
     */
    @Path("/monitoraggio/pcStatoFatture/warning")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    WarningStatiFatturaResponseList pcServizioWarningStatiFattureGET(@QueryParam("token") String token, OrderRequest orderRequest);

    @Path("/monitoraggio/pcStatoFatture/warning/count")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    WarningStatiFatturaCountResponse pcServizioCountWarningStatiFattureGET(@QueryParam("token") String token);

    /*GUI M0 -  Stati Fatture Passive per download
     */
    @Path("/monitoraggio/pcStatoFatture")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    MonitoraggioReportResponse pcServizioStatiFattureGET(@QueryParam("token") String token);

    /*Servizio per reinvio fatture
     */
    @Path("/monitoraggio/reinviaFattura")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    String reinviaFattura(@QueryParam("token") String token, ReinviaFatturaRequest request);

    /*Servizio per reinvio fatture
     */
    @Path("/monitoraggio/reinviaDecorrenzaTermini")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    String reinviaDecorrenzaTermini(@QueryParam("token") String token, ReinviaFatturaRequest request);

    @Path("/monitoraggio/reinviaPecNonConsegnate")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    void reinviaPecNonConsegnate(@QueryParam("token") String token);

    @Path("/monitoraggio/reinviaPecCaNonConsegnate")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    void reinviaPecCaNonConsegnate(@QueryParam("token") String token);

}
