package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import org.apache.camel.builder.RouteBuilder;

public class EndpointStartDynamicRoutes extends RouteBuilder{
    @Override
    public void configure() throws Exception {

        from("{{fatturapa.ftp.ca.timer.dynamic.routes}}")
                .routeId("ftp-ca-fattura-timer-inizialize-dynamic-routes-ricezione")
                .log("[ROUTE ${routeId}] Started dynamic routes [STARTED]")

                .bean("dynamicStartRoutes","startRoutes")

                .log("[ROUTE ${routeId}] Started dynamic routes [FINISHED]");


        /*
        EndPoint chiamato dal servizio del cruscotto
        Puo' essere utilizzato per forzare la ripulitura delle rotte 'zombie' per gli enti che non esistono più!
         */
        from("cxfrs:bean:apiRestServerFtp")
                .routeId("ftp-ca-fattura-api.rest.service")

                .choice()
                .when(simple("${headers.operationName} == 'servizioCreaRottaPOST'"))
                    .log("[ROUTE ${routeId}] Start dynamic routes [START]")
                    .bean("dynamicStartRoutes","startRoutes")
                    .setBody(constant("OK"))

                .when(simple("${headers.operationName} == 'servizioCreaRottaPUT'"))
                    .log("[ROUTE ${routeId}] Update dynamic routes [START]")
                    .setBody(simple("${body[0]}"))
                    .bean("dynamicStartRoutes","updateRoutes")
                    .setBody(constant("OK"))
                .end()

                .log("[ROUTE ${routeId}] Started dynamic routes [FINISHED]");
    }
}
