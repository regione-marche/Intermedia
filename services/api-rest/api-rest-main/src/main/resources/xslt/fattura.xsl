<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="utf-8"/>

  <xsl:param name="codiceUfficio"/>
    <xsl:param name="dataFattura"/>
    <xsl:param name="idCodice"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="FatturaElettronicaHeader/DatiTrasmissione/CodiceDestinatario">
        <CodiceDestinatario>
            <xsl:value-of select="$codiceUfficio"/>
        </CodiceDestinatario>
    </xsl:template>
    <xsl:template match="FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice">
        <IdCodice>
            <xsl:value-of select="$idCodice"/>
        </IdCodice>
    </xsl:template>
    <xsl:template match="FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdCodice">
        <IdCodice>
            <xsl:value-of select="$idCodice"/>
        </IdCodice>
    </xsl:template>
    <xsl:template match="FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Data">
        <Data>
            <xsl:value-of select="$dataFattura"/>
        </Data>
    </xsl:template>

</xsl:stylesheet>