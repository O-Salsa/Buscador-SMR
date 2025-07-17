package br.gov.smr.buscadorsmr;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainWindowController {

    @FXML private TextField txtSearch;
    @FXML private Label lblCount;
    @FXML private Button btnSearch, btnLoadFile, btnAdd, btnAltSitRadio, btnEdit, btnSave, btnExportIds, btnVerListaId;
    @FXML private TableView<Equipamento> tableEquip;
    @FXML private TableColumn<Equipamento, String> colNroBM, colId, colSerie, colMarcaRadio,
    colModeloRadio, colPrefixoVtr, colPlacas, colModeloVtr,
    colComando, colOpm, colMunicipio, colObservacao;
    
    private ObservableList<Equipamento> masterList = FXCollections.observableArrayList();
    private ExcelService service = new ExcelService();
    private File currentFile;


    @FXML
    public void initialize() {
        colNroBM.setCellValueFactory(data -> data.getValue().nroBMProperty());
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colSerie.setCellValueFactory(data -> data.getValue().serieProperty());
        colMarcaRadio.setCellValueFactory(data -> data.getValue().marcaRadioProperty());
        colModeloRadio.setCellValueFactory(data -> data.getValue().modeloRadioProperty());
        colPrefixoVtr.setCellValueFactory(data -> data.getValue().prefixoVtrProperty());
        colPlacas.setCellValueFactory(data -> data.getValue().placasProperty());
        colModeloVtr.setCellValueFactory(data -> data.getValue().modeloVtrProperty());
        colComando.setCellValueFactory(data -> data.getValue().comandoProperty());
        colOpm.setCellValueFactory(data -> data.getValue().opmProperty());
        colMunicipio.setCellValueFactory(data -> data.getValue().municipioProperty());
        colObservacao.setCellValueFactory(data -> data.getValue().observacaoProperty());
        try {
            File file = new File("equipamentos.xlsx");
            masterList.addAll(service.load(file));
            tableEquip.setItems(masterList);
            
        } catch (IOException e) {
            e.printStackTrace();
            // opcional: mostrar alerta ao usuário
        }
    }

    @FXML
    private void onSearchClicked() {
    	String term = txtSearch.getText().trim().toLowerCase();
    	if( term.isEmpty()) {
    		tableEquip.setItems(masterList);
    	} else {
    		ObservableList<Equipamento> filtered = masterList.filtered(eq -> 
    		eq.nroBMProperty().get().toLowerCase().contains(term) || 
    		eq.serieProperty().get().toLowerCase().contains(term) || 
    		eq.idProperty().get().toLowerCase().contains(term)
    	);
      	tableEquip.setItems(filtered);
      	lblCount.setText("Total: " + filtered.size());
    	}  	
    }
    
    @FXML
    private void onLoadFileClicked() {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Selecionar arquivo Excel");
    	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Aquivos Excel", "*.xlsx","*.xls","*.xlsm"));
    	File file = chooser.showOpenDialog(txtSearch.getScene().getWindow());
    	if (file != null) {
    		currentFile = file;
    		loadFromFile(file);
    	}
    }

    @FXML
    private void onAddClicked() { 
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEquip.fxml"));
    		Scene scene = new Scene(loader.load(),350, 500);
    		Stage dialog = new Stage();
    		dialog.setTitle("Adicionar novo Equipamento");
    		dialog.initOwner(txtSearch.getScene().getWindow());
    		dialog.initModality(Modality.APPLICATION_MODAL);
    		dialog.setScene(scene);
    		dialog.showAndWait();
    		
    		AddEquipController ctr = loader.getController();
    		Equipamento novo = ctr.getResult();
    		if (novo != null) {
    			masterList.add(novo);
    			tableEquip.setItems(masterList);
    			lblCount.setText("Total: " + masterList.size());
    		}
    		
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	
    }
    
    @FXML
    private void loadFromFile(File file) {
        System.out.println("📂 loadFromFile chamado com arquivo: " + file.getAbsolutePath());
        masterList.clear();
        try {
            List<Equipamento> lista = service.load(file);
            System.out.println("🔢 Objetos Equipamento lidos: " + lista.size());
            masterList.addAll(lista);
            tableEquip.setItems(masterList);
            System.out.println("✅ masterList size: " + masterList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        lblCount.setText("Total: " + masterList.size());
    }

    @FXML
    private void onAltSitRadioClicked() { }

    @FXML
    private void onEditClicked() { }

    @FXML
    private void onSaveClicked() { }

    @FXML
    private void onExportIdsClicked() { }

    @FXML
    private void onVerListaIdClicked() { } 
}
    

