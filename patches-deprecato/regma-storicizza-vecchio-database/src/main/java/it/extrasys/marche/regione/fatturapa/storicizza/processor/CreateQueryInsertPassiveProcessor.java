package it.extrasys.marche.regione.fatturapa.storicizza.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CreateQueryInsertPassiveProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(CreateQueryInsertPassiveProcessor.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void process(Exchange exchange) throws Exception {

        Map<String, Object> datiFatturaMap = (Map<String, Object>) exchange.getProperty("datiFattura");
        List<Map<String, Timestamp>> statoFatturaMap = (List<Map<String, Timestamp>>) exchange.getProperty("statoFattura");

        String stato = getSequenzaStatiFatturaFromIdentificativoSdI(statoFatturaMap);

        String nomeFile = (String) datiFatturaMap.get("nome_file");
        String codiceUfficio = (String) datiFatturaMap.get("codice_destinatario");
        String dataRicezione = sdf.format((Date) datiFatturaMap.get("data_creazione"));
        BigInteger identificativoSdi = BigInteger.valueOf((Long) exchange.getIn().getHeader("identificativoSdI"));
        String fatturazioneInterna = (String) datiFatturaMap.get("fatturazione_interna");
        String dataFattura = sdf.format((Date) datiFatturaMap.get("data_fattura"));
        String numeroFattura = (String) datiFatturaMap.get("numero_fattura");
        String numeroProtocollo = (String) datiFatturaMap.get("numero_protocollo");
        String cedenteIdFiscale = (String) datiFatturaMap.get("cedente_id_fiscale_iva");
        String committenteIdFiscale = (String) datiFatturaMap.get("committente_id_fiscale_iva");
        String statoFattura = stato;
        String tipoCanale = "";
        if (datiFatturaMap.get("id_tipo_canale") != null) {
            //Ci possono essere id_tipo_canale con senza gli zeri iniziali
            String id_tipo_canale = (String) datiFatturaMap.get("id_tipo_canale");
            tipoCanale = TIPO_CANALE.parse( StringUtils.leftPad(id_tipo_canale,3, '0'));
        }

        String dataInserimento = (String) exchange.getIn().getHeader("data_inserimento");

        String query = "INSERT INTO FATTURA_PASSIVA_STORICIZZATA (NOME_FILE_FATTURA, CODICE_UFFICIO, DATA_RICEZIONE_SDI, IDENTIFICATIVO_SDI, FATTURAZIONE_INTERNA, DATA_FATTURA, NUMERO_FATTURA," +
                " NUMERO_PROTOCOLLO, CEDENTE_ID_FISCALE_IVA, COMMITTENTE_ID_FISCALE_IVA, STATO_FATTURA, TIPO_CANALE, DATA_INSERIMENTO) " +
                " VALUES (";

        query += "'" + nomeFile + "','" + codiceUfficio + "','" + dataRicezione + "'," + identificativoSdi + ",'" + fatturazioneInterna + "','" + dataFattura + "','" + numeroFattura + "','"
                + numeroProtocollo + "','" + cedenteIdFiscale + "','" + committenteIdFiscale + "','" + statoFattura + "','" + tipoCanale + "','" + dataInserimento + "')";

        exchange.getIn().setBody(query);
    }

    private String getSequenzaStatiFatturaFromIdentificativoSdI(List<Map<String, Timestamp>> statoFatturaEntityList) {

        if (statoFatturaEntityList == null || statoFatturaEntityList.isEmpty()) {
            return "";
        }

        String sequenzaStati = "";

        for (Map<String, Timestamp> sfe : statoFatturaEntityList) {
            sequenzaStati = sequenzaStati + sfe.get("desc_stato") + " - " + (Timestamp) sfe.get("data") + "; ";
        }

        return sequenzaStati;
    }

    public enum TIPO_CANALE {

        PEC("001"),
        WS("002"),
        MAIL("003"),
        CA("004"),
        FTP("005");

        private String value;

        TIPO_CANALE(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static String parse(String codTipoCanale) {
            String codTipoCanaleTmp = ""; // Default
            for (TIPO_CANALE temp : TIPO_CANALE.values()) {
                if (temp.getValue().equals(codTipoCanale)) {
                    codTipoCanaleTmp = temp.name();
                    break;
                }
            }
            return codTipoCanaleTmp;
        }


    }

    ;

}
