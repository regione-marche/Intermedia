package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils;

public class FtpConstants {

    public static final String DOWNLOAD = "DOWNLOAD";
    public static final String DIR_SOSPESI = "SOSPESI";
    public static final String EXT_ZIP_DONE = ".zip.done";
    public static final String DIR_ELABORATI = "ELABORATI";
    public static final String DIR_DA_ELABORARE = "DA_ELABORARE";
    public static final String NOTIFICA_ESITO_COMMITTENTE = "NotificaEsitoCommittente";

    public static final String REPORT_ST_INSERT_FI = "insertFI";
    public static final String REPORT_ST_UPDATE_FI_SUPPORTO = "updateFISupporto";
    public static final String REPORT_ST_UPDATE_FI_ESITO = "updateFIEsito";
    public static final String ID_FTP_REPORT_ST_FI = "idFtpReportStFI";

    //NOMI HEADERS
    public static final String ORA_RICEZIONE = "oraRicezioneZip";
    public static final String COD_FISCALE_ENTE = "codFiscaleEnte";
    public static final String ESITO_FTP = "esitoFtp";
    public static final String FILE_NAME_ZIP = "fileNameZip";
    public static final String DIR_UNZIP = "dirUnzip";
    public static final String DIR_ZIP = "dirZip";
    public static final String NUMERO_FILE_TOTALE = "numeroFileTotale";
    public static final String FILE_QUADRATURA = "fileQuadratura";
    public static final String NOME_FILE_FATTURA = "nomeFileFatture";
    public static final String VALIDATED = "validated";
    public static final String TIPO_FILE = "tipoFile";
    public static final String NOME_FILE = "nomeFile";
    public static final String TIPO_MESSAGGIO = "tipoMessaggio";
    public static final String CANALE_AVANZATO = "canaleAvanzato";
    public static final String GIORNI ="giorni";
    public static final String FTP="ftp";
    public static final String ENDPOINT_FTP="endpointFtp";
    public static final String DIR_ROOT="dirRoot";
    public static final String DIR_OUT="dirOut";
    public static final String USERNAME="username";
    public static final String PASSWORD="password";
    public static final String ABSOLUTE_PATH_FILE_FATTURA="absolutePathFileFattura";

    public static final String ROUTE = "route-";
    public static final String DIRECT = "direct:";
    public static final String SEDA = "seda:";

    public static final String FTP_REPORT_ST_TIPO_OPERZIONE = "ftpReportStTipoOperazione";
    public static final String FTP_REPORT_ST_ERRORE = "ftpReportStErrore";
    public static final String FTP_REPORT_ST_ENTE = "ente";
    public static final String FTP_REPORT_ST_EXCEPTION = "exception";

    //Endpoint
    public static final String UNZIP_FILE_ENDPOINT = SEDA.concat("ftp-unzip-file");
    public static final String INVIA_FILE_ESITO_ENDPOINT = DIRECT.concat("crea-invia-file-esito");
    public static final String VALIDA_FILE_QUADRATURA_ENDPOINT = DIRECT.concat("valida-file-quadratura");
    public static final String VALIDA_FILE_FATTURE_ENDPOINT = DIRECT.concat("valida-file-fatture");
    public static final String FTP_ELABORA_ESITO_COMMITTENTE_ENDPOINT = DIRECT.concat("ftp-elabora-esito-committente");
    public static final String FTP_ELABORA_FATTURA_ELETTRONICA_ENDPOINT = DIRECT.concat("ftp-elabora-fattura-elettronica");
    public static final String RIPULITURA_FILE_FTP_ENDPOINT = DIRECT.concat("ripulitura_file_ftp");

    //Rotte
    public static final String UNZIP_FILE_ROUTE = ROUTE.concat("seda-unzip-file-");
    public static final String FTP_FILE_ROUTE = ROUTE.concat("ftp-ricezione-");
    public static final String INVIA_FILE_ESITO_ROUTE = ROUTE.concat("crea-invia-file-esito");
    public static final String VALIDA_FILE_QUADRATURA_ROUTE = ROUTE.concat("valida-file-quadratura");
    public static final String ELABORA_FILE_ROUTE = ROUTE.concat("elabora-file-");
    public static final String VALIDA_FILE_FATTURE_ROUTE = ROUTE.concat("valida-file-fatture");
    public static final String FTP_ELABORA_ESITO_COMMITTENTE_ROUTE = ROUTE.concat("ftp-elabora-esito-committente");
    public static final String FTP_ELABORA_FATTURA_ELETTRONICA_ROUTE = ROUTE.concat("ftp-elabora-fattura-elettronica");
    public static final String RIPULITURA_FILE_FTP_QUARTZ_ROUTE = "route-quartz-ripulitura-file-ftp";
    public static final String RIPULITURA_FILE_FTP_JETTY_ROUTE = "route-jetty-ripulitura-file-ftp";
    public static final String RIPULITURA_FILE_FTP_ROUTE = ROUTE.concat("ripulitura_file_ftp");

}
