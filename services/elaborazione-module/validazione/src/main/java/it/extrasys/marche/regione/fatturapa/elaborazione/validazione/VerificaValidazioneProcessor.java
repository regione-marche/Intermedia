package it.extrasys.marche.regione.fatturapa.elaborazione.validazione;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by agosteeno on 05/09/17.
 */
public class VerificaValidazioneProcessor implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(VerificaValidazioneProcessor.class);

    private static final String TIPO_DOCUMENTO_HEADER = "tipoDocumento";
    private static final String NOTA_CREDITO_VALIDATE_HEADER = "notaCreditoValidate";

    private static final String VALIDA_FATTURA_CHECK_HEADER = "validaFatturaCheck";

    @Override
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();

        String tipoDocumentoHeader = (String) message.getHeader(TIPO_DOCUMENTO_HEADER);
        String notaCreditoValidateHeader = (String) message.getHeader(NOTA_CREDITO_VALIDATE_HEADER);

        String identificativoSdiHeader = (String) message.getHeader("identificativoSdI");

        LOG.info("VALIDAZIONE CHECK TIPO DOCUMENTO E IS NOTA CREDITO ATTIVA: tipoDocumento [" + tipoDocumentoHeader + "] " +
                " notaCreditoValidate [" + notaCreditoValidateHeader + "] identificativoSdI [" + identificativoSdiHeader + "]");


        //il seguente blocco non funziona correttamente, per ora non verifico mai se la nota di credito deve essere validata o no, semplicemente non le considero
        /*if ("TD01".equals(tipoDocumentoHeader) || ("TD04".equals(tipoDocumentoHeader) && "true".equals(notaCreditoValidateHeader))){
            message.setHeader(VALIDA_FATTURA_CHECK_HEADER, "true");
            LOG.info("VALIDAZIONE CHECK TIPO DOCUMENTO E IS NOTA CREDITO ATTIVA: tipoDocumento [" + tipoDocumentoHeader + "] " +
                    " notaCreditoValidate [" + notaCreditoValidateHeader + "] identificativoSdI [" + identificativoSdiHeader
                    + "] : VALIDAZIONE ATTIVA");

        } else {
            message.setHeader(VALIDA_FATTURA_CHECK_HEADER, "false");
            LOG.info("VALIDAZIONE CHECK TIPO DOCUMENTO E IS NOTA CREDITO ATTIVA: tipoDocumento [" + tipoDocumentoHeader + "] " +
                    " notaCreditoValidate [" + notaCreditoValidateHeader + "] identificativoSdI [" + identificativoSdiHeader
                    + "] : VALIDAZIONE NON ATTIVA");
        }*/

        if ("TD01".equals(tipoDocumentoHeader)){
            message.setHeader(VALIDA_FATTURA_CHECK_HEADER, "true");
            LOG.info("VALIDAZIONE CHECK TIPO DOCUMENTO ATTIVA: tipoDocumento [" + tipoDocumentoHeader + "] " +
                    " notaCreditoValidate [" + notaCreditoValidateHeader + "] identificativoSdI [" + identificativoSdiHeader
                    + "] : VALIDAZIONE ATTIVA");

        }

    }
}
