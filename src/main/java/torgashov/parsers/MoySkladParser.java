package torgashov.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import torgashov.rows.MoySkladRow;

public class MoySkladParser {
	private String filenameFrom = null;
	private HashMap<String, MoySkladRow> asIsMap;

	public MoySkladParser() {
		super();
		asIsMap = new HashMap<String, MoySkladRow>();
	}

	public void setFilenameFrom(String fileName) {
		this.filenameFrom = fileName;
	}

	public HashMap<String, MoySkladRow> Parse() throws IOException {
		File myFile = new File(filenameFrom);

		FileInputStream fis = new FileInputStream(myFile);

		// Finds the workbook instance for XLSX file
		HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);

		// Return first sheet from the XLSX workbook
		HSSFSheet mySheet = myWorkBook.getSheetAt(0);

		// Get iterator to all the rows in current sheet
		Iterator<Row> rowIterator = mySheet.iterator();
		int countAllRows = 0;
		// Traversing over each row of XLSX file

		// Traversing over each row of XLSX file
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			//System.out.println(row.getCell(0) + "; " + row.getCell(1) + "; " + row.getCell(2));

			if (row == null) {
				continue;
			}
			if (row.getCell(1) == null) {
				continue;
			}
			if (row.getCell(1).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(1).toString().equals("")) {
				continue;
			}
			if (row.getCell(2) == null) {
				continue;
			}
			if (row.getCell(2).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(2).toString().equals("")) {
				continue;
			}
			if (row.getCell(3) == null) {
				continue;
			}
			if (row.getCell(3).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(3).toString().equals("")) {
				continue;
			}

			MoySkladRow tmpRow = new MoySkladRow();
			tmpRow.setId(row.getCell(1).toString());
			tmpRow.setPrice(row.getCell(2).toString());
			tmpRow.setLeftOver(row.getCell(3).toString());

			asIsMap.put(tmpRow.getId(), tmpRow);
			countAllRows++;

		}

		System.out.println("The number of entrys rows = " + countAllRows);
		myWorkBook.close();

		return asIsMap;
	}

}
