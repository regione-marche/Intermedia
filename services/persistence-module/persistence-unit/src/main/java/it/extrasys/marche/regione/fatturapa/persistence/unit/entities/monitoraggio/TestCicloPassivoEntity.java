package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "TEST_CICLO_PASSIVO")
public class TestCicloPassivoEntity implements IEntity {

    private static final long serialVersionUID = -434652755596642067L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_TEST_CICLO_PASSIVO")
    private BigInteger idTestCicloPassivo;

    @Column(name = "NOME_FILE")
    private String nomeFile;

    @Column(name = "IDENTIFICATIVO_SDI")
    private BigInteger identificativoSdi;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_ENTE")
    private EnteEntity ente;

    @Column(name = "DATA_TEST")
    private Timestamp dataTest;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_UTENTE")
    private UtenteEntity utente;


    public BigInteger getIdTestCicloPassivo() {
        return idTestCicloPassivo;
    }

    public void setIdTestCicloPassivo(BigInteger idTestCicloPassivo) {
        this.idTestCicloPassivo = idTestCicloPassivo;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(BigInteger identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public EnteEntity getEnte() {
        return ente;
    }

    public void setEnte(EnteEntity ente) {
        this.ente = ente;
    }

    public Timestamp getDataTest() {
        return dataTest;
    }

    public void setDataTest(Timestamp dataTest) {
        this.dataTest = dataTest;
    }

    public UtenteEntity getUtente() {
        return utente;
    }

    public void setUtente(UtenteEntity utente) {
        this.utente = utente;
    }
}
