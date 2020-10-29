package it.extrasys.marche.regione.fatturapa.mock.sdi.setup;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.*;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for setNotificaImpostata complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setNotificaImpostata"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="notifica" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}rispostaSdINotificaEsito_Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setNotificaImpostata", propOrder = { "notifica" })
public class SetNotificaImpostata implements Serializable {

	private final static long serialVersionUID = 1L;
	protected RispostaSdINotificaEsitoType notifica;

	/**
	 * Gets the value of the notifica property.
	 * 
	 * @return possible object is {@link RispostaSdINotificaEsitoType }
	 * 
	 */
	public RispostaSdINotificaEsitoType getNotifica() {
		return notifica;
	}

	/**
	 * Sets the value of the notifica property.
	 * 
	 * @param value
	 *            allowed object is {@link RispostaSdINotificaEsitoType }
	 * 
	 */
	public void setNotifica(RispostaSdINotificaEsitoType value) {
		this.notifica = value;
	}

}
