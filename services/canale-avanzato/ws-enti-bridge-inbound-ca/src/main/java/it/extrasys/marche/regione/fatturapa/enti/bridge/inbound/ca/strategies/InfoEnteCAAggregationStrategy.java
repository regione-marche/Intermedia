package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.strategies;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class InfoEnteCAAggregationStrategy implements AggregationStrategy {

    private final String CODICE_UFFICIO_HEADER = "codiceUfficio";
    private final String CODA_PROTOCOLLO_CA_IN_HEADER = "codaProtocolloCAIn";
    private final String CODA_REGISTRAZIONE_CA_IN_HEADER = "codaGestionaleCaIn";

    private final String REQ_CAMPI_EX_HEADER = "reqCampiEX";
    private final String REQ_NOME_FILE_EX_HEADER = "reqNomeFileEX";
    private final String REQ_FILE_EX_HEADER = "reqFileEX";

    private final String ENTE_EX_HEADER = "enteEX";
    private final String ENTE_CREDENZIALE_EX_HEADER = "enteCredenzialiEX";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String reqCampiEX = newExchange.getIn().getHeader(REQ_CAMPI_EX_HEADER, String.class);
        String reqNomeFileEX = newExchange.getIn().getHeader(REQ_NOME_FILE_EX_HEADER, String.class);
        String reqFileEX = newExchange.getIn().getHeader(REQ_FILE_EX_HEADER, String.class);

        String enteEx = newExchange.getIn().getHeader(ENTE_EX_HEADER, String.class);
        String enteCredenzialiEX = newExchange.getIn().getHeader(ENTE_CREDENZIALE_EX_HEADER, String.class);

        String codiceUfficio = newExchange.getIn().getHeader(CODICE_UFFICIO_HEADER, String.class);
        String codaProtocolloCAIn = newExchange.getIn().getHeader(CODA_PROTOCOLLO_CA_IN_HEADER, String.class);
        String codaGestionaleCaIn = newExchange.getIn().getHeader(CODA_REGISTRAZIONE_CA_IN_HEADER, String.class);

        oldExchange.getIn().setHeader(REQ_CAMPI_EX_HEADER, reqCampiEX);
        oldExchange.getIn().setHeader(REQ_NOME_FILE_EX_HEADER, reqNomeFileEX);
        oldExchange.getIn().setHeader(REQ_FILE_EX_HEADER, reqFileEX);

        oldExchange.getIn().setHeader(ENTE_EX_HEADER, enteEx);
        oldExchange.getIn().setHeader(ENTE_CREDENZIALE_EX_HEADER, enteCredenzialiEX);

        oldExchange.getIn().setHeader(CODICE_UFFICIO_HEADER, codiceUfficio);
        oldExchange.getIn().setHeader(CODA_PROTOCOLLO_CA_IN_HEADER, codaProtocolloCAIn);
        oldExchange.getIn().setHeader(CODA_REGISTRAZIONE_CA_IN_HEADER, codaGestionaleCaIn);

        return oldExchange;
    }
}