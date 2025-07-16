package br.gov.smr.buscadorsmr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelService {
	
	//vai ler o arquivo Excel e converter para lista de Equipamento
	public List<Equipamento> load(File file) throws IOException {
		List<Equipamento> lista = new ArrayList<>();
		
		try (InputStream is = new FileInputStream(file);
				Workbook wb = new XSSFWorkbook(is)){
			
			Sheet sheet = wb.getSheetAt(0);
			
			boolean primeiraLinha = true;
			for(Row row : sheet){
				if (primeiraLinha) {
					primeiraLinha = false;
					continue;
				}
				Equipamento eq = new Equipamento();
				eq.nroBMProperty().set(getCellAsString(row, 0));
                eq.idProperty().set(getCellAsString(row, 1));
                eq.serieProperty().set(getCellAsString(row, 2));
                eq.marcaRadioProperty().set(getCellAsString(row, 3));
                eq.modeloRadioProperty().set(getCellAsString(row, 4));
                eq.prefixoVtrProperty().set(getCellAsString(row, 5));
                eq.placasProperty().set(getCellAsString(row, 6));
                eq.modeloVtrProperty().set(getCellAsString(row, 7));
                eq.comandoProperty().set(getCellAsString(row, 8));
                eq.opmProperty().set(getCellAsString(row, 9));
                eq.municipioProperty().set(getCellAsString(row, 10));
                eq.observacaoProperty().set(getCellAsString(row, 11));
				
                lista.add(eq);
			}
		}
		return lista;
	}
	
	//vai converter uma celula em String, mantendo o tipo.
	private String getCellAsString(Row row, int col) {
	    Cell cell = row.getCell(col);
	    if (cell == null) return "";

	    return switch (cell.getCellType()) {
	        case STRING -> cell.getStringCellValue();
	        case NUMERIC -> {
	            if (DateUtil.isCellDateFormatted(cell)) {
	                yield cell.getDateCellValue().toString();
	            } else {
	                yield Double.toString(cell.getNumericCellValue());
	            }
	        }
	        case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue()); 
	        case FORMULA -> cell.getCellFormula();
	        default -> "";
		};
	}

}
