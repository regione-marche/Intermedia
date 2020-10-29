package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca.aggregationstrategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class PecCAEsitoOperazioneEsitoCommittenteAggregationStrategy implements AggregationStrategy {

    private static final String NOME_FILE_ESITO_OPERAZIONE = "nomeFileEsitoOperazione";
    private static final String FILE_ESITO_OPERAZIONE = "fileEsitoOperazione";

    private static final String COD_ESITO_ESITO_OPERAZIONE = "codiceEsito";
    private static final String DESCRIZIONE_ESITO_ESITO_OPERAZIONE = "descrizioneEsito";
    private static final String NOME_FILE_FATTURA_ESITO_OPERAZIONE = "nomeFileFattura";
    private static final String NUMERO_FATTURA = "numeroFattura";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String fileEsitoOperazione = (String) newExchange.getIn().getBody();
        String nomeFileEsitoOperazione = (String) newExchange.getIn().getHeader(NOME_FILE_ESITO_OPERAZIONE);

        String nomeFileEsito = (String) newExchange.getIn().getHeader(NOME_FILE_ESITO_OPERAZIONE);
        String codiceEsito = (String) newExchange.getIn().getHeader(COD_ESITO_ESITO_OPERAZIONE);
        String descrizioneEsito = (String) newExchange.getIn().getHeader(DESCRIZIONE_ESITO_ESITO_OPERAZIONE);
        String nomeFileFattura = (String) newExchange.getIn().getHeader(NOME_FILE_FATTURA_ESITO_OPERAZIONE);
        String numeroFattura = (String) newExchange.getIn().getHeader(NUMERO_FATTURA);


        oldExchange.getIn().setHeader(FILE_ESITO_OPERAZIONE, fileEsitoOperazione);
        oldExchange.getIn().setHeader(NOME_FILE_ESITO_OPERAZIONE, nomeFileEsitoOperazione);

        oldExchange.getIn().setHeader(NOME_FILE_ESITO_OPERAZIONE, nomeFileEsito);
        oldExchange.getIn().setHeader(COD_ESITO_ESITO_OPERAZIONE, codiceEsito);
        oldExchange.getIn().setHeader(DESCRIZIONE_ESITO_ESITO_OPERAZIONE, descrizioneEsito);
        oldExchange.getIn().setHeader(NOME_FILE_FATTURA_ESITO_OPERAZIONE, nomeFileFattura);
        oldExchange.getIn().setHeader(NUMERO_FATTURA, numeroFattura);

        return oldExchange;
    }
}