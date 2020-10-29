package it.extrasys.marche.regione.fatturapa.api.rest.model;

import java.util.ArrayList;
import java.util.List;

public class TipoCanaleCaList {

    private List<String> tipoCanaleCaList;

    public TipoCanaleCaList(){
        this.tipoCanaleCaList = new ArrayList<>();
    }

    public List<String> getTipoCanaleCaList() {
        return tipoCanaleCaList;
    }

    public void setTipoCanaleCaList(List<String> tipoCanaleCaList) {
        this.tipoCanaleCaList = tipoCanaleCaList;
    }

    public void addTipo2List(String tipo){
        this.tipoCanaleCaList.add(tipo);
    }
}
