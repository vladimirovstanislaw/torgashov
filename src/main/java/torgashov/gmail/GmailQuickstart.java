package torgashov.gmail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

public class GmailQuickstart {

	private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "C:/vianor_stock/tokens";
	private static final List<String> list = new ArrayList<String>(
			Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY));

	private static final List<String> SCOPES = list;
	private static final String CREDENTIALS_FILE_PATH_OTHER_APP_BAD = "/client_id.json";
	private final String pathToSaveFile;
	private final String emailProvider;
	private final String idEmailInSubj;

	public GmailQuickstart(String pathToSaveFile, String emailProvider, String idEmailInSubj) {
		super();
		this.pathToSaveFile = pathToSaveFile;
		this.emailProvider = emailProvider;
		this.idEmailInSubj = idEmailInSubj;
	}

	private static Credential getCredential(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH_OTHER_APP_BAD);
		if (in == null) {
			throw new FileNotFoundException("File not found :" + CREDENTIALS_FILE_PATH_OTHER_APP_BAD);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

	}

	public void run() throws IOException, GeneralSecurityException {

		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		String user = "me";

		String queryFromProvider = "from:" + emailProvider;

		ArrayList<Message> messageList = listMessagesMatchingQuery(service, user, queryFromProvider);
		getFiles(service, user, messageList, pathToSaveFile + "\\");

	}

	public void getFiles(Gmail service, String userId, ArrayList<Message> list, String pathToSaveFiles)
			throws IOException {
		Message lastMessage = null;

		System.out.println("Input list size = " + list.size());

		ArrayList<Message> fullMessageList = new ArrayList<Message>();
		ArrayList<Message> fullMessageSubjList = new ArrayList<Message>();

		for (int i = 0; i < 2; i++) {// Можно поменять
			Message fullLastMessage_tmp = service.users().messages().get(userId, list.get(i).getId()).execute();
			fullMessageList.add(fullLastMessage_tmp);
		}
		System.out.println("fullMessageList list size = " + fullMessageList.size());

		for (Message msg : fullMessageList) {

			List<MessagePartHeader> headerList = msg.getPayload().getHeaders();

			for (MessagePartHeader header : headerList) {

				if (header.getName().equals("Subject")) {

					System.out.println("subject : " + header.getName() + "  " + header.getValue());

					if (header.getValue().contains(this.idEmailInSubj)) {
						System.out.println("We have some email with \"" + this.idEmailInSubj + "\" in subject"
								+ header.getValue());
						fullMessageSubjList.add(msg);
					}
				}
			}
		}
		System.out.println("fullMessageSubjList list size = " + fullMessageSubjList.size());
		lastMessage = fullMessageSubjList.get(0);

		Date dateLastMessage = new Date(lastMessage.getInternalDate());
		System.out.println("Date of last message: " + dateLastMessage.getDate() + "." + dateLastMessage.getMonth());

		if (lastMessage == null) {
			System.out.println("lastMessage is null message.");
		}
		String lastMessageId = lastMessage.getId();

		Message fullLastMessage = service.users().messages().get(userId, lastMessageId).execute();

		List<MessagePart> parts = fullLastMessage.getPayload().getParts();
		for (MessagePart part : parts) {
			if (part.getFilename() != null && part.getFilename().length() > 0) {
				String filename = part.getFilename();
				String attId = part.getBody().getAttachmentId();

				MessagePartBody attachPart = service.users().messages().attachments().get(userId, lastMessageId, attId)
						.execute();

				Base64 base64Url = new Base64(true);
				byte[] fileByteArray = base64Url.decodeBase64(attachPart.getData());
				FileOutputStream fileOutFile = new FileOutputStream(pathToSaveFiles + filename);
				fileOutFile.write(fileByteArray);
				fileOutFile.close();
			}
		}

	}

	public static ArrayList<Message> listMessagesMatchingQuery(Gmail service, String userId, String query)
			throws IOException {
		ListMessagesResponse response = service.users().messages().list(userId).setMaxResults((long) 20).setQ(query)
				.execute();

		ArrayList<Message> messages = new ArrayList<Message>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
			} else {
				break;
			}
		}

		return messages;
	}

	public static void clearFolder(File folder) {

		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					clearFolder(f);
				} else {
					f.delete();
				}
			}
		}

	}

}
