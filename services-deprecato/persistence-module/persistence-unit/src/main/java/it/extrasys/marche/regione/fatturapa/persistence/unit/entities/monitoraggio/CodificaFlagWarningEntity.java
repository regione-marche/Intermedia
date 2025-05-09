package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CODIFICA_FLAG_WARNING")
public class CodificaFlagWarningEntity implements IEntity {

    private static final long serialVersionUID = 5696996847238548115L;

    @Id
    @Column(name="CODICE_FLAG_WARNING", nullable = false)
    private String codiceFlagWarning;

    @Column(name="DESC_FLAG_WARNING", nullable = false)
    private String descFlagWarning;


    public CodificaFlagWarningEntity.CODICI_FLAG_WARNING getCodiceFlagWarning() {
        return CodificaFlagWarningEntity.CODICI_FLAG_WARNING.parse(codiceFlagWarning);
    }

    public void setCodiceFlagWarning(CodificaFlagWarningEntity.CODICI_FLAG_WARNING codiceFlagWarning) {
        this.codiceFlagWarning = codiceFlagWarning.getValue();
    }
    public String getDescFlagWarning() {
        return descFlagWarning;
    }

    public void setDescFlagWarning(String descFlagWarning) {
        this.descFlagWarning = descFlagWarning;
    }

    public enum CODICI_FLAG_WARNING {

        VERDE("001"),
        GIALLO("002"),
        ROSSO("003");

        private String value;

        CODICI_FLAG_WARNING(String value) {
            this.value = value;
        }

        public static CODICI_FLAG_WARNING parse(String codStato) {
            CODICI_FLAG_WARNING codFlagWarning = null; // Default
            for (CODICI_FLAG_WARNING temp : CODICI_FLAG_WARNING.values()) {
                if (temp.getValue().equals(codStato)) {
                    codFlagWarning = temp;
                    break;
                }
            }
            return codFlagWarning;
        }

        public String getValue() {
            return value;
        }
    }
}
