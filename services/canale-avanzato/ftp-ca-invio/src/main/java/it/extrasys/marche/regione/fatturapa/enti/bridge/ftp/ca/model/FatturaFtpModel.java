package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model;

import java.math.BigInteger;

public class FatturaFtpModel {

    public static final String FATTURA_PASSIVA = "fatturaPassiva";
    public static final String FATTURA_ATTIVA = "fatturaAttiva";
    public static final String DECORRENZA_TERMINI = "decorrenzaTermini";

    //nel caso 'attiva', contiene l'id di 'fatturaAttiva' non della notificaAttiva
    private BigInteger idFattura;
    //nel caso 'attiva', contiene il tipo di notifica
    private String tipoFattura;
    private String nomeFile;
    private String idFiscaleEnte;
    private byte[] contenutoFattura;
    private BigInteger identificativoSdI;
    private String codiceUfficio;
    private byte[] contenutoMetadati;
    private String nomeFileMetadati;


    public byte[] getContenutoFattura() {
        return contenutoFattura;
    }

    public void setContenutoFattura(byte[] contenutoFattura) {
        this.contenutoFattura = contenutoFattura;
    }

    public BigInteger getIdFattura() {
        return idFattura;
    }

    public void setIdFattura(BigInteger idFattura) {
        this.idFattura = idFattura;
    }

    public String getTipoFattura() {
        return tipoFattura;
    }

    public void setTipoFattura(String tipoFattura) {
        this.tipoFattura = tipoFattura;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getIdFiscaleEnte() {
        return idFiscaleEnte;
    }

    public void setIdFiscaleEnte(String idFiscaleEnte) {
        this.idFiscaleEnte = idFiscaleEnte;
    }

    public BigInteger getIdentificativoSdI() {
        return identificativoSdI;
    }

    public void setIdentificativoSdI(BigInteger identificativoSdI) {
        this.identificativoSdI = identificativoSdI;
    }

    public String getCodiceUfficio() {
        return codiceUfficio;
    }

    public void setCodiceUfficio(String codiceUfficio) {
        this.codiceUfficio = codiceUfficio;
    }

    public byte[] getContenutoMetadati() {
        return contenutoMetadati;
    }

    public void setContenutoMetadati(byte[] contenutoMetadati) {
        this.contenutoMetadati = contenutoMetadati;
    }

    public String getNomeFileMetadati() {
        return nomeFileMetadati;
    }

    public void setNomeFileMetadati(String nomeFileMetadati) {
        this.nomeFileMetadati = nomeFileMetadati;
    }
}
