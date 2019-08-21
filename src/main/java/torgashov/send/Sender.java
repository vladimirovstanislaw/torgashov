package torgashov.send;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class Sender {
	private String from;
	private String fileName;

	public void setData(String from, String fileName) {
		this.from = from;
		this.fileName = fileName;
	}

	public void send() {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession("sftp_user_w", "sftp.vianor.ru", 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword("ErAGAn35eN");

			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;
			String unixFrom = from.replace("\\", "/");
			System.out.println("from = " + unixFrom);
			System.out.println("fileName = " + fileName);

			System.out.println("Full filename = " + unixFrom + "/" + fileName);
			sftpChannel.put(unixFrom + "/" + fileName, "/SFTP_W/" + fileName);

			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}

	}
}
