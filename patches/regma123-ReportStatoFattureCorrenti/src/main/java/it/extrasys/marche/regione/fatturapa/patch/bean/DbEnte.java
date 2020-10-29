package it.extrasys.marche.regione.fatturapa.patch.bean;

import org.apache.camel.Exchange;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by Antonio on 30/07/2015.
 */
public class DbEnte {
    public void dbToEnte(Exchange msgExchange) {

        @SuppressWarnings("unchecked")
        Map<String, Object> row = (Map<String, Object>) msgExchange.getIn().getBody();

        Ente ente = new Ente();

        ente.setIdentificativoSdi(BigInteger.valueOf((Long) row.get("identificativo_sdi")));
        ente.setCodiceDestinatario((String) row.get("codice_destinatario"));
        if(row.get("data").toString() != null){
            ente.setData(((Date) row.get("data")).toString());
        }
        if(row.get("data_fattura").toString() != null){
            ente.setDataFattura(((Date) row.get("data_fattura")).toString());
        }
        ente.setNomeFile((String) row.get("nome_file"));
        ente.setDescStato((String) row.get("desc_stato"));
        ente.setNumeroProtocollo((String) row.get("numero_protocollo"));
        ente.setCedenteIdFiscaleIva((String) row.get("cedente_id_fiscale_iva"));
        ente.setNumeroFattura((String) row.get("numero_fattura"));

        msgExchange.getIn().setBody(ente);
    }

}
