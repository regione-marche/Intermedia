<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="utf-8" indent="yes"/>

    <xsl:param name="codiceUfficio"/>
    <xsl:param name="identificativoSdI"/>
    <xsl:param name="nomeFile"/>
    <xsl:param name="dataOraRicezione"/>
    <xsl:param name="dataOraConsegna"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="IdentificativoSdI">
        <IdentificativoSdI>
            <xsl:value-of select="$identificativoSdI" />
        </IdentificativoSdI>
    </xsl:template>
    <xsl:template match="NomeFile">
        <NomeFile>
            <xsl:value-of select="$nomeFile"/>
        </NomeFile>
    </xsl:template>
    <xsl:template match="DataOraRicezione">
        <DataOraRicezione>
            <xsl:value-of select="$dataOraRicezione"/>
        </DataOraRicezione>
    </xsl:template>
    <xsl:template match="DataOraConsegna">
        <DataOraConsegna>
            <xsl:value-of select="$dataOraConsegna"/>
        </DataOraConsegna>
    </xsl:template>
    <xsl:template match="Destinatario/Codice">
        <Codice>
            <xsl:value-of select="$codiceUfficio"/>
        </Codice>
    </xsl:template>

</xsl:stylesheet>