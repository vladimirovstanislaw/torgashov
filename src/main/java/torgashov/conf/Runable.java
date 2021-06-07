package torgashov.conf;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import com.google.api.services.gmail.Gmail.Users.Settings.GetVacation;

import torgashov.files.Upload;
import torgashov.gmail.GmailQuickstart;
import torgashov.parsers.MoySkladParser;
import torgashov.rows.MoySkladRow;
import torgashov.send.Sender;

public class Runable {

	// private static final String PathToNomenclature =
	// "C:\\vianor_stock\\Nomeclature";

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static void main(String[] args) throws InterruptedException, IOException, GeneralSecurityException {
		if (args.length != 0) {

			String path_from = args[0]; // Корневая папка
			String path_to = args[1]; // Куда кладем .csv - выгрузку и номеклатуру
			String pathToSaveFile = args[2]; // куда будем класть выгрузку provider'a
			String fileNameUploadFirst = args[3]; // имя отправляемого файла 1
			String fileNameUploadSecond = args[4]; // имя отправляемого файла 2
			String emailProvider = args[5]; // email provider
			String idEmailInSubj = args[6]; // id of email
			int dayToDelivery = Integer.valueOf(args[7]); // id of email

			// "C:\vianor_stock" "C:\vianor_stock" "C:\vianor_stock\MoySklad"
			// "Products_Vianor_420_32gg582.csv" "Products_Vianor_423_32gg582.csv"
			// "no-reply@x.moysklad.ru" "Stock" "1"

			File folderToSave = new File(pathToSaveFile);

			GmailQuickstart gmail = new GmailQuickstart(pathToSaveFile, emailProvider, idEmailInSubj);

			gmail.setTokenDirectory(path_from + "\\tokens");

			GmailQuickstart.clearFolder(folderToSave);// очищаем папку provider'a
			// GmailQuickstart.clearFolder(new File(PathToNomenclature));// очищаем папку
			// номенклатуры

			gmail.run();

			String fileName = getLastModifiedFile(folderToSave);

			MoySkladParser parser = new MoySkladParser();
			parser.setFilenameFrom(fileName);

			HashMap<String, MoySkladRow> moySkladMap = parser.Parse();

//			moySkladMap.entrySet().stream().forEach(e -> {
//				System.out.println("\"" + e.getKey() + "\"; \"" + e.getValue().getPrice() + "\"; \""
//						+ e.getValue().getLeftOver() + "\"");
//			});

			Upload upload = new Upload(dayToDelivery, path_to + "\\" + fileNameUploadFirst,
					path_to + "\\" + fileNameUploadSecond, moySkladMap);
			upload.writeFiles();

			Sender sender = new Sender();// 30 seconds timeout
			sender.setData(path_from, fileNameUploadFirst);
			sender.send();
			sender.setData(path_from, fileNameUploadSecond);
			sender.send();
		} else {
			System.out.println("Not enough args");
		}
	}

	public static String getLastModifiedFile(File folderToSave) {
		File folder = folderToSave;

		File[] matchingFiles = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xls");
			}
		});
		File lastFile = matchingFiles[0];

		System.out.println("Last modified file :" + lastFile);

		return lastFile.getPath();

	}
}
