package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CANALE_CA")
public class CanaleCaEntity implements IEntity {

	public enum CANALE_CA {

		WS_PALEO("001"),
		WS_AGID("002"),
		WS("003"),
		PEC("004"),
		FTP("005");

		private String value;

		CANALE_CA(String value){this.value=value;}

		public String getValue() { return value; }

		public static CanaleCaEntity.CANALE_CA parse(String codCanale) {
			CanaleCaEntity.CANALE_CA codCanaleTmp = null; // Default
			for (CanaleCaEntity.CANALE_CA temp : CanaleCaEntity.CANALE_CA.values()) {
				if (temp.getValue().equals(codCanale)) {
					codCanaleTmp = temp;
					break;
				}
			}
			return codCanaleTmp;
		}


	};

	@Id
	@Column(name="COD_CANALE", nullable = false)
	private String codCanale;

	@Column(name="DESC_CANALE", nullable = false)
	private String descCanale;

	public CanaleCaEntity(){
		//needed fro jpa
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getCodCanale() {
		return codCanale;
	}

	public void setCodCanale(String codCanale) {
		this.codCanale = codCanale;
	}

	public String getDescCanale() {
		return descCanale;
	}

	public void setDescCanale(String descCanale) {
		this.descCanale = descCanale;
	}
}
