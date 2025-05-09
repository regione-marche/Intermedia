<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="utf-8" indent="yes"/>

    <xsl:param name="codiceUfficio"/>
    <xsl:param name="identificativoSdI"/>
    <xsl:param name="nomeFile"/>

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
    <xsl:template match="CodiceDestinatario">
        <CodiceDestinatario>
            <xsl:value-of select="$codiceUfficio"/>
        </CodiceDestinatario>
    </xsl:template>
    <xsl:template match="NomeFile">
        <NomeFile>
            <xsl:value-of select="$nomeFile"/>
        </NomeFile>
    </xsl:template>
</xsl:stylesheet>