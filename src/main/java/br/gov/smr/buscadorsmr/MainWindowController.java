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

	@FXML
	private javafx.scene.control.ComboBox<String> cmbFiltro;
	@FXML
	private TextField txtSearch;
	@FXML
	private Label lblCount;
	@FXML
	private Button btnSearch, btnLoadFile, btnAdd, btnAltSitRadio, btnEdit, btnSave, btnExportIds, btnVerListaId;
	@FXML
	private TableView<Equipamento> tableEquip;
	@FXML
	private TableColumn<Equipamento, String> colNroBM, colId, colSerie, colMarcaRadio, colModeloRadio, colPrefixoVtr,
			colPlacas, colModeloVtr, colComando, colOpm, colMunicipio, colObservacao;

	private ObservableList<Equipamento> masterList = FXCollections.observableArrayList();
	private ObservableList<String> listaIdsLivres = FXCollections.observableArrayList();
	private ExcelService service = new ExcelService();

	@FXML
	public void initialize() {
		cmbFiltro.setValue("Nº BM");
	    cmbFiltro.getItems().addAll("Nº BM", "ID", "SÉRIE");
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
						case "QAP- Circulando":
							color = "rgba(0,220,0,0.20)";
							break;
						case "FA - baixado":
							color = "rgba(255,0,0,0.20)";
							break;
						case "Recolhido ao CMTEC":
							color = "rgba(0,100,255,0.20)";
							break;
						case "Garantia":
							color = "rgba(255,255,0,0.20)";
							break;
						default:
							color = "transparent";
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
		String filtro = cmbFiltro.getValue();

		if (term.isEmpty()) {
			tableEquip.setItems(masterList);
			lblCount.setText("Total: " + masterList.size());
		} else {
			ObservableList<Equipamento> filtered;
			if ("ID".equals(filtro)) {
				filtered = masterList.filtered(eq -> eq.idProperty().get().toLowerCase().contains(term));
			} else if ("SÉRIE".equals(filtro)) {
				filtered = masterList.filtered(eq -> eq.serieProperty().get().toLowerCase().contains(term));
			} else {
				filtered = masterList.filtered(eq -> eq.nroBMProperty().get().toLowerCase().contains(term));
			}
			tableEquip.setItems(filtered);
			lblCount.setText("Total: " + filtered.size());
		}
	}

	@FXML
	private void onLoadFileClicked() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Selecionar arquivo Excel");
		chooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("Aquivos Excel", "*.xlsx", "*.xls", "*.xlsm"));
		File file = chooser.showOpenDialog(txtSearch.getScene().getWindow());
		if (file != null) {
			loadFromFile(file);
		}
	}

	@FXML
	private void onAddClicked() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEquip.fxml"));
			Scene scene = new Scene(loader.load(), 350, 500);
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

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	private void loadFromFile(File file) {
		System.out.println("📂 loadFromFile chamado com arquivo: " + file.getAbsolutePath());
		masterList.clear();
		listaIdsLivres.clear();
		try {
			// le Equipamento
			List<Equipamento> lista = service.load(file);
			System.out.println("🔢 Objetos Equipamento lidos: " + lista.size());
			masterList.addAll(lista);
			// Le IDs Livres
			List<String> ids = service.loadIdsLivres(file);
			listaIdsLivres.addAll(ids);
						
			
			tableEquip.setItems(masterList);
			lblCount.setText("Total: " + masterList.size());
		} catch (IOException e) {
			e.printStackTrace();
			alerta("Erro ao carregar arquivo!", "Falha ao ler o arquivo Excel.");
		}
		masterList.removeIf(Equipamento::isEmpty);
	}

	@FXML
	private void onAltSitRadioClicked() {
		Equipamento selecionado = tableEquip.getSelectionModel().getSelectedItem();
		if (selecionado == null) {
			// mostrar alerta de nenhum item selecionado (fazer depois talvez)
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
			Scene scene = new Scene(loader.load(), 350, 500);
			Stage dialog = new Stage();
			dialog.setTitle("Editar Equipamento");
			dialog.initOwner(txtSearch.getScene().getWindow());
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.setScene(scene);

			AddEquipController ctr = loader.getController();
			ctr.setEquipamento(selecionado);

			dialog.showAndWait();

			Equipamento atualizado = ctr.getResult();
			if (atualizado != null) {
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
	private void onSalvarIdsExcelMenu() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Salvar tabela com IDs Livres");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo Excel", "*.xlsx"));
		File file = fileChooser.showSaveDialog(tableEquip.getScene().getWindow());
		if (file == null) return;
		
		try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()){
		//Criar aba principal do Programa
		org.apache.poi.ss.usermodel.Sheet sheetEquip = workbook.createSheet("Equipamentos Buscador SMR");
		// cabeçalho em negrito
		org.apache.poi.ss.usermodel.CellStyle boldStyle = workbook.createCellStyle();
		org.apache.poi.ss.usermodel.Font font = workbook.createFont();
		font.setBold(true);
		boldStyle.setFont(font);
		
		//Cria estilo cor da situação
		org.apache.poi.ss.usermodel.CellStyle styleQAP = workbook.createCellStyle();
		org.apache.poi.ss.usermodel.CellStyle styleFA = workbook.createCellStyle();
		org.apache.poi.ss.usermodel.CellStyle styleCMTEC = workbook.createCellStyle();
	    org.apache.poi.ss.usermodel.CellStyle styleGarantia = workbook.createCellStyle();
	    // função cor de fundo
		java.util.function.BiConsumer<org.apache.poi.ss.usermodel.CellStyle, Short> colorSetter = (style, color) -> {
			style.setFillForegroundColor(color);
			style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
		};
		
	    colorSetter.accept(styleQAP, org.apache.poi.ss.usermodel.IndexedColors.LIGHT_GREEN.getIndex());
	    colorSetter.accept(styleFA, org.apache.poi.ss.usermodel.IndexedColors.ROSE.getIndex());
	    colorSetter.accept(styleCMTEC, org.apache.poi.ss.usermodel.IndexedColors.PALE_BLUE.getIndex());
	    colorSetter.accept(styleGarantia, org.apache.poi.ss.usermodel.IndexedColors.LIGHT_YELLOW.getIndex());
		
		org.apache.poi.ss.usermodel.Row headerRow = sheetEquip.createRow(0);		
		String[] headers = { "Nº BM", "ID", "SÉRIE", "MARCA RADIO", "MODELO RADIO", "PREFIXO VTR",
	                "PLACAS", "MODELO VTR", "COMANDO", "OPM", "MUNICÍPIO", "OBSERVAÇÃO" };
		
		
		for(int i = 0; i < headers.length; i++) {
		    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
	        cell.setCellValue(headers[i]);
	        cell.setCellStyle(boldStyle);
	        // Definindo largura da coluna
	        sheetEquip.setColumnWidth(i, 15 * 256);
		}
		//Dados dos equipamentos para a tabela criada 
		int rowIndex = 1;
        for (Equipamento eq : masterList) {
        	
        	org.apache.poi.ss.usermodel.Row row = sheetEquip.createRow(rowIndex++);

            // Cria as células normalmente
            org.apache.poi.ss.usermodel.Cell cellNroBM = row.createCell(0);
            cellNroBM.setCellValue(eq.getNroBM());
        	
        	// Define a cor conforme situação
            String situacao = eq.getSituacao();
            if (situacao != null) {
                switch (situacao) {
                    case "QAP- Circulando":
                        cellNroBM.setCellStyle(styleQAP);
                        break;
                    case "FA - baixado":
                        cellNroBM.setCellStyle(styleFA);
                        break;
                    case "Recolhido ao CMTEC":
                        cellNroBM.setCellStyle(styleCMTEC);
                        break;
                    case "Garantia":
                        cellNroBM.setCellStyle(styleGarantia);
                        break;
                }
            }
      	
            row.createCell(0).setCellValue(eq.getNroBM());
            row.createCell(1).setCellValue(eq.getId());
            row.createCell(2).setCellValue(eq.getSerie());
            row.createCell(3).setCellValue(eq.getMarcaRadio());
            row.createCell(4).setCellValue(eq.getModeloRadio());
            row.createCell(5).setCellValue(eq.getPrefixoVtr());
            row.createCell(6).setCellValue(eq.getPlacas());
            row.createCell(7).setCellValue(eq.getModeloVtr());
            row.createCell(8).setCellValue(eq.getComando());
            row.createCell(9).setCellValue(eq.getOpm());
            row.createCell(10).setCellValue(eq.getMunicipio());
            row.createCell(11).setCellValue(eq.getObservacao());
        }
        // Aba das Ids Livres
        org.apache.poi.ss.usermodel.Sheet sheetIds = workbook.createSheet("IDs Livres");
        sheetIds.createRow(0).createCell(0).setCellValue("ID Livre");
        int idRow = 1;
        for (String id : listaIdsLivres) {
        	sheetIds.createRow(idRow).createCell(0).setCellValue(id);
        	idRow++;
        }
        //Salvar o arquivo
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
        	workbook.write(fos);
        }
        alerta("Sucesso!","Arquivo salvo com a tabela IDs livres!");
	}catch (Exception e) {
		e.printStackTrace();
		alerta("Erro!", "Falha ao salvar arquivo Excel.");
	}
		}

	@FXML
	private void onExportIdsClicked() {
		Equipamento selecionado = tableEquip.getSelectionModel().getSelectedItem();
		if (selecionado == null) {
			alerta("Nenhum item selecionado!", "Selecione um equipamento para remover o ID.");
			return;
		}
		String idRemovida = selecionado.getId();
		if (idRemovida == null || idRemovida.isBlank() || idRemovida.equals("ID removida")) {
			alerta("ID já removida!", "Este equipamento já está sem ID.");
			return;
		}
		listaIdsLivres.add(idRemovida); // Adiciona alista de Id removida
		selecionado.idProperty().set("ID removida"); // Altera na tabela
		tableEquip.refresh();
	}

	@FXML
	private void onVerListaIdsMenu() {

		javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
		dialog.setTitle("IDs Livres");
		dialog.setHeaderText("Estas são as IDs livres no momento:");

		javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea();
		textArea.setEditable(false); // nao deixa editar, mas permite selecionar e copiar
		textArea.setWrapText(true);

		StringBuilder sb = new StringBuilder("IDs livres:\n");
		if (listaIdsLivres.isEmpty()) {
			alerta("Nenhuma ID livre", "Não há IDs livres disponíveis para exibir.");
			return;
		}
		for (String id : listaIdsLivres) {
			sb.append(id).append("\n");
		}
		textArea.setText(sb.toString().trim());
		textArea.setPrefRowCount(Math.max(10, listaIdsLivres.size()));

		dialog.getDialogPane().setContent(textArea);
		dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

		dialog.showAndWait();
	}

	@FXML
	private void onAtribuirIdMenu() {
		Equipamento selecionado = tableEquip.getSelectionModel().getSelectedItem();
		if (selecionado == null) {
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
		escolhido.ifPresent(id -> {
			selecionado.idProperty().set(id);
			listaIdsLivres.remove(id);
			tableEquip.refresh();
		});
	}

	private void alerta(String titulo, String texto) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.INFORMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText(texto);
		alert.showAndWait();
	}
}
