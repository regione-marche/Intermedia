package it.extrasys.marche.regione.fatturapa.api.rest.client;

import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import org.apache.camel.Exchange;
import org.jolokia.client.BasicAuthenticator;
import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MalformedObjectNameException;

public class JolokiaClient {
    private static final Logger LOG = LoggerFactory.getLogger(JolokiaClient.class);

    public void getJolokiaDLQ(Exchange exchange) throws MalformedObjectNameException, FatturaPAStatoCodeNonTrovatoException {

        try {
            J4pClient j4p = J4pClient.url((String) exchange.getIn().getHeader("jolokiaUrl"))
                    .user((String) exchange.getIn().getHeader("jolokiaUsername"))
                    .password((String) exchange.getIn().getHeader("jolokiaPassword"))
                    .authenticator(new BasicAuthenticator().preemptive())
                    .connectionTimeout(3000)
                    .build();

            String brokerFirstPart = (String) exchange.getIn().getHeader("brokerFirstPart");
            String brokerSecondPart = (String) exchange.getIn().getHeader("brokerSecondPart") + (String) exchange.getIn().getHeader("destinationName");
            String brokerAttribute = (String) exchange.getIn().getHeader("brokerAttribute");

            J4pReadRequest request = new J4pReadRequest(brokerFirstPart + "," + brokerSecondPart, brokerAttribute);

            J4pResponse<J4pReadRequest> response = j4p.execute(request);

            exchange.getIn().setBody(response);

        } catch (J4pException i) {
           // throw new FatturaPAStatoCodeNonTrovatoException(i.getMessage());

            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getIn().setBody(null);
        } catch (Exception e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
    }
}
