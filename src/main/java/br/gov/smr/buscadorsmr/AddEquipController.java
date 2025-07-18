package br.gov.smr.buscadorsmr;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEquipController {
	
	 @FXML 
	 private TextField txtNroBM, txtId, txtSerie, txtMarcaRadio, txtModeloRadio, txtPrefixoVtr,
     txtPlacas, txtModeloVtr, txtComando, txtOpm, txtMunicipio, txtObservacao;
	 
	 private Equipamento result = null;
	 
	    @FXML
	    private void onSave() {
	        result = new Equipamento(
	            txtNroBM.getText(), txtId.getText(), txtSerie.getText(),
	            txtMarcaRadio.getText(), txtModeloRadio.getText(),
	            txtPrefixoVtr.getText(), txtPlacas.getText(),
	            txtModeloVtr.getText(), txtComando.getText(),
	            txtOpm.getText(), txtMunicipio.getText(),
	            txtObservacao.getText()
	        );
	        closeWindow();
	    }
	    @FXML private void onCancel() {
	        result = null;
	        closeWindow();
	    }

	    private void closeWindow() {
	        Stage stage = (Stage) txtNroBM.getScene().getWindow();
	        stage.close();
	    }

	    public Equipamento getResult() {
	        return result;
	    }
	    
	    public void setEquipamento(Equipamento eq) {
	        txtNroBM.setText(eq.getNroBM());
	        txtId.setText(eq.getId());
	        txtSerie.setText(eq.getSerie());
	        txtMarcaRadio.setText(eq.getMarcaRadio());
	        txtModeloRadio.setText(eq.getModeloRadio());
	        txtPrefixoVtr.setText(eq.getPrefixoVtr());
	        txtPlacas.setText(eq.getPlacas());
	        txtModeloVtr.setText(eq.getModeloVtr());
	        txtComando.setText(eq.getComando());
	        txtOpm.setText(eq.getOpm());
	        txtMunicipio.setText(eq.getMunicipio());
	        txtObservacao.setText(eq.getObservacao());
	    }
	}
