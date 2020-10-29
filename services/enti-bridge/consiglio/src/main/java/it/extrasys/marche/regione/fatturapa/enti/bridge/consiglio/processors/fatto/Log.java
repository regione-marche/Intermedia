package it.extrasys.marche.regione.fatturapa.enti.bridge.consiglio.processors.fatto;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.cxf.transport.http.HTTPException;

public class Log implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        if(exchange.getProperty("CamelExceptionCaught") instanceof HttpOperationFailedException){

            HttpOperationFailedException exception = (HttpOperationFailedException) exchange.getProperty("CamelExceptionCaught");
            int code = exception.getStatusCode();
            String statusText = exception.getStatusText();
            String responseBody = exception.getResponseBody();

            exchange.getIn().setHeader("response", responseBody + "");
            exchange.getIn().setHeader("rejectCode", code + "");
            exchange.getIn().setHeader("rejectDescription", statusText);

            if("409".equals(code)) {
                exchange.getIn().setHeader("fattoConsiglio409", true);
            }

            return;
        }

        if(exchange.getProperty("CamelExceptionCaught") instanceof HTTPException){

            HTTPException exception = (HTTPException) exchange.getProperty("CamelExceptionCaught");

            int code = 500;
            String statusText = exception.getMessage();

            exchange.getIn().setHeader("px98RejectCode", code + "");
            exchange.getIn().setHeader("px98RejectDescription", statusText);

            return;
        }

        if(exchange.getProperty("CamelExceptionCaught") instanceof org.apache.camel.component.http4.HttpOperationFailedException){

            HttpOperationFailedException exception = (HttpOperationFailedException) exchange.getProperty("CamelExceptionCaught");
            int code = exception.getStatusCode();
            String statusText = exception.getStatusText();
            String responseBody = exception.getResponseBody();

            exchange.getIn().setHeader("response", responseBody + "");
            exchange.getIn().setHeader("px98RejectCode", code + "");
            exchange.getIn().setHeader("px98RejectDescription", statusText);

            if("409".equals(code)) {
                exchange.getIn().setHeader("fattoConsiglio409", true);
            }

            return;
        }
    }
}
