package it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.utils;

import java.text.SimpleDateFormat;

public class AgidConstant {
    public static final String ADDRESS_HEADER = "address";
    public static final String NOME_ENTE_HEADER = "nomeEnte";
    public static final String ID_SDI_HEADER = "idSDI";
    public static final String WSDL_URL_HEADER = "wsdlURL";
    public static final String AUTHORIZATION = "Authorization";
    public static final String AGID_ERROR_HEADER = "AGID_ERROR_MESSAGE";
    public static final String NOME_ENTE_DESTINAZIONE = "nomeEnteDestinazione";

    public static final SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    public static final SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd");
}
