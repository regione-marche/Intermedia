package it.extrasys.marche.regione.fatturapa.validator;

import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeNotificaEsitoCommittente;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by agosteeno on 15/04/15.
 */
public class ValidatoreNomeNotificaEsitoCommittenteTest {

    private static final String NOTIFICA_ESITO_COMMITTENTE_CORRETTO = "IT01234567890_10001_EC_123.xml";
    private static final String NOTIFICA_ESITO_COMMITTENTE_PROGRESSIVO_LUNGO = "IT01234567890_10001_EC_12345.xml";
    private static final String NOTIFICA_ESITO_COMMITTENTE_PROGRESSIVO_LUNGO_IN_FATTURA = "IT01234567890_1000123_EC_123.xml";
    private static final String NOTIFICA_ESITO_COMMITTENTE_PROGRESSIVO_NON_ALFANUMERICO = "IT01234567890_10001_EC_12#.xml";
    private static final String NOTIFICA_ESITO_COMMITTENTE_SENZA_ESTENSIONE = "IT01234567890_10001_EC_123";
    private static final String NOTIFICA_ESITO_COMMITTENTE_TIPO_MESSAGGIO_ERRATO = "IT01234567890_10001_XX_123";
    private static final String NOTIFICA_ESITO_COMMITTENTE_NOME_FILE_ERRATO = "AB_001_XX_123";
    private static final String NOTIFICA_ESITO_COMMITTENTE_NUMERO_PARTI_ERRATO = "IT01234567890_10001_123_234_XX_123.xml";
    private static final String NOTIFICA_ESITO_COMMITTENTE_CARATTERE_INIZIALE_ERRATO = "~IT01234567890_10001_EC_123.xml";

    private static final String NOTIFICA_PROVA="IT02098391200_8GX_EC_SUU.xml";

    @Test
    public void testCorrettoTrue(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_CORRETTO);

        Assert.assertTrue(result);

    }

    @Test
    public void testProgressivoLungoFalse(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_PROGRESSIVO_LUNGO);

        Assert.assertFalse(result);

    }

    @Test
    public void testProgressivoNonAlfanumericoFalse(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_PROGRESSIVO_NON_ALFANUMERICO);

        Assert.assertFalse(result);

    }

    @Test
    public void testProgressivoLungoInFatturaFalse(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_PROGRESSIVO_LUNGO_IN_FATTURA);

        Assert.assertFalse(result);

    }

    @Test
    public void testSenzaEstensioneFalse(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_SENZA_ESTENSIONE);

        Assert.assertFalse(result);

    }

    @Test
    public void testTipoMessaggioErratoFalse(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_TIPO_MESSAGGIO_ERRATO);

        Assert.assertFalse(result);

    }

    @Test
    public void testNomeFileErratoFalse(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_NOME_FILE_ERRATO);

        Assert.assertFalse(result);

    }

    @Test
    public void testNumeroPartiErratoFalse(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_NUMERO_PARTI_ERRATO);

        Assert.assertFalse(result);

    }

    @Test
    public void testProvaTrue(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_PROVA);

        Assert.assertTrue(result);

    }

    @Test
    public void testCarattereInizialeErratoFalse(){

        boolean result = ValidatoreNomeNotificaEsitoCommittente.validate(NOTIFICA_ESITO_COMMITTENTE_CARATTERE_INIZIALE_ERRATO);

        Assert.assertFalse(result);
    }
}
