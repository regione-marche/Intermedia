package it.extrasys.marche.regione.fatturapa.elaborazione.validazione.model;

/**
 * Project blueprint-test-examples
 *
 * @author Luigi De Masi  <ldemasi@redhat.com>
 * @since 21 ottobre 2014
 *
 */
public class MyEntity {

    private String Id;

    public MyEntity(String id, String description) {
        Id = id;
        this.description = description;
    }

    private String description;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
