package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table (name = "ENTE_CAMPO_OPZIONALE_ASSOCIAZIONE")
public class EnteCampoOpzionaleAssociazioneEntity {

    private static final long serialVersionUID = -42331234L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_ASSOCIAZIONE")
    private BigInteger idAssociazione;

    @ManyToOne
    @JoinColumn(name = "ID_CAMPO_OPZIONALE")
    private CampoOpzionaleFatturaEntity campoOpzionale;

    @ManyToOne
    @JoinColumn(name = "ID_ENTE")
    private EnteEntity ente;

    public BigInteger getIdAssociazione() {
        return idAssociazione;
    }

    public void setIdAssociazione(BigInteger idAssociazione) {
        this.idAssociazione = idAssociazione;
    }

    public EnteEntity getEnte() {
        return ente;
    }

    public void setEnte(EnteEntity ente) {
        this.ente = ente;
    }

    public CampoOpzionaleFatturaEntity getCampoOpzionale() {
        return campoOpzionale;
    }

    public void setCampoOpzionale(CampoOpzionaleFatturaEntity campoOpzionale) {
        this.campoOpzionale = campoOpzionale;
    }
}
