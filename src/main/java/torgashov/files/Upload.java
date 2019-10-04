package torgashov.files;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import torgashov.rows.MoySkladRow;

public class Upload {
	private static final String n = "\r\n";
	private static final String semilicon = ";";
	private int dayToDelivery;

	String fileNameOne;
	String fileNameTwo;

	private String finalData;
	private final int DEFAULT_dayToDelivery = 1;

	private HashMap<String, MoySkladRow> allDataMap;

	public Upload() {
		allDataMap = null;
		dayToDelivery = DEFAULT_dayToDelivery;
	}

	public Upload(int dayToDelivery, String fileNameOne, String fileNameTwo, HashMap<String, MoySkladRow> allDataMap) {
		super();
		this.dayToDelivery = dayToDelivery;
		this.fileNameOne = fileNameOne;
		this.fileNameTwo = fileNameTwo;
		this.allDataMap = allDataMap;
	}

	public int getDayToDelivery() {
		return dayToDelivery;
	}

	public void setDayToDelivery(int dayToDelivery) {
		this.dayToDelivery = dayToDelivery;
	}

	public String getFileNameOne() {
		return fileNameOne;
	}

	public void setFileNameOne(String fileNameOne) {
		this.fileNameOne = fileNameOne;
	}

	public String getFileNameTwo() {
		return fileNameTwo;
	}

	public void setFileNameTwo(String fileNameTwo) {
		this.fileNameTwo = fileNameTwo;
	}

	public HashMap<String, MoySkladRow> getAllDataMap() {
		return allDataMap;
	}

	public void setAllDataMap(HashMap<String, MoySkladRow> allDataMap) {
		this.allDataMap = allDataMap;
	}

	public void writeFiles() throws IOException {
		if (allDataMap != null) {

			allDataMap.forEach((k, v) -> finalData += k + semilicon + "" + semilicon + "" + semilicon + "" + semilicon
					+ v.getPrice() + semilicon + v.getLeftOver() + semilicon + dayToDelivery + n);
			FileOutputStream outputStreamOne = new FileOutputStream(fileNameOne);
			FileOutputStream outputStreamTwo = new FileOutputStream(fileNameTwo);

			byte[] strToBytes = finalData.getBytes();

			outputStreamOne.write(strToBytes);

			outputStreamTwo.write(strToBytes);

			outputStreamTwo.close();
			outputStreamOne.close();
		}
	}

}
