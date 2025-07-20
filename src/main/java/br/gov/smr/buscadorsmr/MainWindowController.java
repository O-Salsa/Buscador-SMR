package br.gov.smr.buscadorsmr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
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
    private ObservableList<String> listaIdsLivres = FXCollections.observableArrayList();
    private ExcelService service = new ExcelService();
    
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
        
        colNroBM.setCellFactory(col -> new TableCell<Equipamento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Equipamento eq = getTableView().getItems().get(getIndex());
                    String situacao = eq.getSituacao();
                    String color = "transparent";
                    if (situacao != null) {
                        switch (situacao) {
                            case "QAP- Circulando": color = "rgba(0,220,0,0.20)"; break;
                            case "FA - baixado": color = "rgba(255,0,0,0.20)"; break;
                            case "Recolhido ao CMTEC": color = "rgba(0,100,255,0.20)"; break;
                            case "Garantia": color = "rgba(255,255,0,0.20)"; break;
                            default: color = "transparent";
                        }
                    }
                    setStyle("-fx-background-color: " + color + ";");
                }
            }
            
        });
    
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
        masterList.removeIf(Equipamento::isEmpty);
    }

    @FXML
    private void onAltSitRadioClicked() { 
    	Equipamento selecionado = tableEquip.getSelectionModel().getSelectedItem();
    	if (selecionado == null) {
    		//mostrar alerta de nenhum item selecionado (fazer depois talvez)
    		return;
    	}
        List<String> opcoes = Arrays.asList("QAP- Circulando", "FA - baixado", "Recolhido ao CMTEC", "Garantia");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(selecionado.getSituacao(), opcoes);
        dialog.setTitle("Alterar Situação");
        dialog.setHeaderText("Selecione nova situação para o Radio:");
        dialog.setContentText("Situação: ");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selecionado::setSituacao);
        tableEquip.refresh();

    }

    @FXML
    private void onEditClicked() { 
    	Equipamento selecionado = tableEquip.getSelectionModel().getSelectedItem();
    	if (selecionado == null) {
    		// mostrar alerta que nao esta selecionado
    		return;
    	}
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEquip.fxml"));
    		Scene scene = new Scene(loader.load(), 350,500);
    		Stage dialog = new Stage();
    		dialog.setTitle("Editar Equipamento");
    		dialog.initOwner(txtSearch.getScene().getWindow());
    		dialog.initModality(Modality.APPLICATION_MODAL);
    		dialog.setScene(scene);
    		
    		AddEquipController ctr = loader.getController();
    		ctr.setEquipamento(selecionado);
    		
    		dialog.showAndWait();
    		
    		Equipamento atualizado = ctr.getResult();
    		if(atualizado != null) {
    			selecionado.nroBMProperty().set(atualizado.getNroBM());
                selecionado.idProperty().set(atualizado.getId());
                selecionado.serieProperty().set(atualizado.getSerie());
                selecionado.marcaRadioProperty().set(atualizado.getMarcaRadio());
                selecionado.modeloRadioProperty().set(atualizado.getModeloRadio());
                selecionado.prefixoVtrProperty().set(atualizado.getPrefixoVtr());
                selecionado.placasProperty().set(atualizado.getPlacas());
                selecionado.modeloVtrProperty().set(atualizado.getModeloVtr());
                selecionado.comandoProperty().set(atualizado.getComando());
                selecionado.opmProperty().set(atualizado.getOpm());
                selecionado.municipioProperty().set(atualizado.getMunicipio());
                selecionado.observacaoProperty().set(atualizado.getObservacao());
                
                tableEquip.refresh();
    		}
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    @FXML
    private void onSaveClicked() { }

    @FXML
    private void onExportIdsClicked() {
    	Equipamento selecionado = tableEquip.getSelectionModel().getSelectedItem();
    	if(selecionado == null ) {
            alerta("Nenhum item selecionado!", "Selecione um equipamento para remover o ID.");
   		return;
    	}
    	String idRemovida = selecionado.getId();
    	if (idRemovida == null || idRemovida.isBlank() || idRemovida.equals("ID removida")) {
            alerta("ID já removida!", "Este equipamento já está sem ID.");
    		return;
    	}
    	listaIdsLivres.add(idRemovida); //Adiciona alista de Id removida
    	selecionado.idProperty().set("ID removida"); // Altera na tabela 
    	tableEquip.refresh();
    	}

    @FXML
    private void onVerListaIdsMenu() {
        StringBuilder sb = new StringBuilder("IDs livres:\n");
        if (listaIdsLivres.isEmpty()) {
            sb.append("Nenhum ID livre disponível.");
        } else {
            for (String id : listaIdsLivres) {
                sb.append(id).append("\n");
            }
        }
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Lista de IDs livres");
        alert.setHeaderText("IDs disponíveis para uso:");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    @FXML
    private void onAtribuirIdMenu() {
    	Equipamento selecionado = tableEquip.getSelectionModel().getSelectedItem();
    	if(selecionado == null) {
    		alerta("Nenhum item selecionado!", "Selecione um equipamento para atribuir um ID");
    		return;
    	}
    	String idAtual = selecionado.getId();
    	if (idAtual != null && !idAtual.isBlank() && !idAtual.equals("ID removida")) {
    		alerta("Item ja possui ID!", "Selecione um equipamento com ID em branco");
    		return;
    	}
    	if (listaIdsLivres.isEmpty()) {
    		alerta("Sem IDs livres", "Não há Ids livres para atribuir.");
    		return;
    	}
    	// Fazer a escolha do ID para atribuir
    	ChoiceDialog<String> escolha = new ChoiceDialog<>(listaIdsLivres.get(0), listaIdsLivres);
    	escolha.setTitle("Atribuir ID Livre");
    	escolha.setHeaderText("Escolha um ID livre para atribuir:");
    	escolha.setContentText("ID:");
    	Optional<String> escolhido = escolha.showAndWait();
    	escolhido.ifPresent(id ->{
    		selecionado.idProperty().set(id);
    		listaIdsLivres.remove(id);
    		tableEquip.refresh();
    	});
    }
    
    private void alerta(String titulo, String texto) {
    	javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
    	alert.setTitle(titulo);
    	alert.setHeaderText(null);
    	alert.setContentText(texto);
    	alert.showAndWait();
    }
}
    

