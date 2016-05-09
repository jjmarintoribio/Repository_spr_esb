package ib.visa.esb.bth.gen;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigurableService;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class GetFilesCargosCE {

	private static boolean SWLECTURASC = false;
	private static String SFTPHOST;
	private static int SFTPPORT;
	private static String SFTPUSER;
	private static String SFTPPASS;
	private static String EXT;
	private static String STRICHOSTKEYKEYCHECKING;
	private static String LOCALDIR;

	public static String getFiles() {

		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {

			// -------------------------------

			if (!SWLECTURASC) {
				BrokerProxy b = BrokerProxy.getLocalInstance();

				while (!b.hasBeenPopulatedByBroker()) {
					Thread.sleep(100);
				}
				
				ConfigurableService csSftpIn = b.getConfigurableService("UserDefined", "sftpCEIn");
			
				SFTPHOST = csSftpIn.getProperties().getProperty("host");
				SFTPPORT = Integer.parseInt(csSftpIn.getProperties().getProperty("port"));
			    SFTPUSER = csSftpIn.getProperties().getProperty("user");
				SFTPPASS = csSftpIn.getProperties().getProperty("psw");  // TODO obtenerlo del encriptado en BD
				EXT = csSftpIn.getProperties().getProperty("extension");
				STRICHOSTKEYKEYCHECKING = csSftpIn.getProperties().getProperty("strictHostKeyChecking");
				LOCALDIR = csSftpIn.getProperties().getProperty("localDirectory");				

				SWLECTURASC = true;
			}

			// -----------------------------

			StringBuffer strFiles = new StringBuffer();
			String nomNewFile = null;

			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", STRICHOSTKEYKEYCHECKING);
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;

			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> lisDir = channelSftp.ls("*"); // lee todos los directorios del root del SFTP
			ChannelSftp.LsEntry item = null;
			int cantDir = lisDir.size();
			int cantFiles = 0;
			String nameDir = null;
			String nameFile = null;
			byte[] buffer = null;
			BufferedInputStream bis = null;
			File newFile = null;
			File newFileRenamed = null;
			OutputStream os = null;
			BufferedOutputStream bos = null;

			SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddhhmmss");
			Date dateModify = null;

			for (int i = 0; i < cantDir; i++) {

				item = lisDir.get(i);

				nameDir = item.getFilename();

				if (nameDir.length() == 9) { // solo lee las carpetas que tengan 9 digitos de nombre

					channelSftp.cd("/" + nameDir + "/IN");

					@SuppressWarnings("unchecked")
					Vector<ChannelSftp.LsEntry> lisFiles = channelSftp.ls("*" + EXT);

					cantFiles = lisFiles.size();

					for (int x = 0; x < cantFiles; x++) {

						item = lisFiles.get(x);
						nameFile = item.getFilename();

						if (nameFile.endsWith(EXT)) {

							buffer = new byte[1024];
							bis = new BufferedInputStream(channelSftp.get(nameFile));

							// Date date = originalFormat.parse(item.getAttrs().getMtimeString());
							dateModify = new Date(item.getAttrs().getMTime() * 1000L);
							nomNewFile = LOCALDIR + nameDir + "_" + formatDate.format(dateModify) + "_" + nameFile;
							newFile = new File(nomNewFile + ".part");
							os = new FileOutputStream(newFile);
							bos = new BufferedOutputStream(os);
							int readCount;
							while ((readCount = bis.read(buffer)) > 0) {
								bos.write(buffer, 0, readCount);
							}

							strFiles.append(nameDir).append("/IN/").append(nameFile).append("|");

							// renombrado del archivo transferido
							newFileRenamed = new File(nomNewFile);

							newFile.renameTo(newFileRenamed);
							// borrado del archivo del sftp que fue transferido
							channelSftp.rm(nameFile);
							// cerrado de objetos
							bis.close();
							bos.close();

						}
					}

				}

			}

			if (strFiles.length() > 0) {
				return SFTPHOST + ":" + strFiles.toString(); // devuelve la IP del SFTP con los archisos que transfirio
			} else {
				return ""; // not found files
			}

		} catch (Exception e) {

			return "ERROR: " + e.getMessage();

		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
			} else {
				channelSftp = null;
			}
			if (channel != null) {
				channel.disconnect();
			} else {
				channel = null;
			}
			if (session != null) {
				session.disconnect();
			} else {
				session = null;
			}

		}

	}

}
