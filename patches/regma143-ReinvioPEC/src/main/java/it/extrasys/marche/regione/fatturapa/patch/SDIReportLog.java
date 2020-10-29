package it.extrasys.marche.regione.fatturapa.patch;

/**
 * Created by gianfranco on 13/07/2016.
 */
public class SDIReportLog {

    private Integer SDI;
    private String tipo;
    private String subject;
    private String motivo;

    public SDIReportLog(Integer SDI, String tipo, String subject, String motivo) {
        this.SDI = SDI;
        this.tipo = tipo;
        this.subject = subject;
        this.motivo = motivo;
    }

    public Integer getSDI() {
        return SDI;
    }

    public void setSDI(Integer SDI) {
        this.SDI = SDI;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {

        String s = "**********************************\n\n";
               s += "SDI = [" + SDI + "]\n";
               s += "Tipo = [" + tipo + "]\n";
               s += "Subject = [" + subject + "]\n";
               s += "Motivo = [" + motivo + "]\n\n";
               s += "**********************************\n\n";

        return s;
    }
}
