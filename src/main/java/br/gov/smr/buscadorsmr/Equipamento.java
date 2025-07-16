package br.gov.smr.buscadorsmr;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Equipamento {
    private final StringProperty nroBM = new SimpleStringProperty();
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty serie = new SimpleStringProperty();
    private final StringProperty marcaRadio = new SimpleStringProperty();
    private final StringProperty modeloRadio = new SimpleStringProperty();
    private final StringProperty prefixoVtr = new SimpleStringProperty();
    private final StringProperty placas = new SimpleStringProperty();
    private final StringProperty modeloVtr = new SimpleStringProperty();
    private final StringProperty comando = new SimpleStringProperty();
    private final StringProperty opm = new SimpleStringProperty();
    private final StringProperty municipio = new SimpleStringProperty();
    private final StringProperty observacao = new SimpleStringProperty();
	
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


