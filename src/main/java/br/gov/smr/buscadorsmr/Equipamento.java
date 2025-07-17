package br.gov.smr.buscadorsmr;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Equipamento {
    private  StringProperty nroBM = new SimpleStringProperty();
    private  StringProperty id = new SimpleStringProperty();
    private  StringProperty serie = new SimpleStringProperty();
    private  StringProperty marcaRadio = new SimpleStringProperty();
    private  StringProperty modeloRadio = new SimpleStringProperty();
    private  StringProperty prefixoVtr = new SimpleStringProperty();
    private  StringProperty placas = new SimpleStringProperty();
    private  StringProperty modeloVtr = new SimpleStringProperty();
    private  StringProperty comando = new SimpleStringProperty();
    private  StringProperty opm = new SimpleStringProperty();
    private  StringProperty municipio = new SimpleStringProperty();
    private  StringProperty observacao = new SimpleStringProperty();
	
    
    
    public Equipamento(String nroBM, String id, String serie,
            String marcaRadio, String modeloRadio,
            String prefixoVtr, String placas,
            String modeloVtr, String comando,
            String opm, String municipio, String observacao) {
        this.nroBM = new SimpleStringProperty(nroBM);
        this.id = new SimpleStringProperty(id);
        this.serie = new SimpleStringProperty(serie);
        this.marcaRadio = new SimpleStringProperty(marcaRadio);
        this.modeloRadio = new SimpleStringProperty(modeloRadio);
        this.prefixoVtr = new SimpleStringProperty(prefixoVtr);
        this.placas = new SimpleStringProperty(placas);
        this.modeloVtr = new SimpleStringProperty(modeloVtr);
        this.comando = new SimpleStringProperty(comando);
        this.opm = new SimpleStringProperty(opm);
        this.municipio = new SimpleStringProperty(municipio);
        this.observacao = new SimpleStringProperty(observacao);
    }

	public Equipamento() {
    	
    }

    public StringProperty nroBMProperty() { return nroBM; }
    public StringProperty idProperty() { return id; }
    public StringProperty serieProperty() { return serie; }
    public StringProperty marcaRadioProperty() { return marcaRadio; }
    public StringProperty modeloRadioProperty() { return modeloRadio; }
    public StringProperty prefixoVtrProperty() { return prefixoVtr; }
    public StringProperty placasProperty() { return placas; }
    public StringProperty modeloVtrProperty() { return modeloVtr; }
    public StringProperty comandoProperty() { return comando; }
    public StringProperty opmProperty() { return opm; }
    public StringProperty municipioProperty() { return municipio; }
    public StringProperty observacaoProperty() { return observacao; }
}


