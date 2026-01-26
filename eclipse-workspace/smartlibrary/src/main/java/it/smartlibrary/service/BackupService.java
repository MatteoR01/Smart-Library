package it.smartlibrary.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.smartlibrary.util.LogService;

//Backup e restore del database H2 usando il comando SQL BACKUP, che permette di creare copie consistenti anche mentre il DB è aperto
public class BackupService {

	private static final String BACKUP_DIR = "./backup/";

	public BackupService() {
		File dir = new File(BACKUP_DIR);
		if (!dir.exists()) dir.mkdirs();
	}

	//Crea un backup usando il comando SQL nativo di H2
	public File backup() throws Exception {
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File backupFile = new File(BACKUP_DIR + "backup_" + timestamp + ".zip");

		String sql = "BACKUP TO '" + backupFile.getAbsolutePath().replace("\\", "/") + "'";
		
		try (Connection conn = DriverManager.getConnection("jdbc:h2:./data/smartlibrary", "sa", "");
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.execute();
		}
		
		LogService.getLogger().info("Backup eseguito in: " + backupFile);
		
		return backupFile;
	}

	//Restore non può essere fatto mentre il db è aperto
	public void restore(File backupZip) throws Exception {
		LogService.getLogger().warning("Restore eseguito da: " + backupZip);
		throw new UnsupportedOperationException(
				"Il restore richiede la chiusura dell'applicazione. " +
						"Chiudi SmartLibrary e usa il comando H2: java -cp h2.jar org.h2.tools.Restore"
				);
	}

	public File[] listBackups() {
		File dir = new File(BACKUP_DIR);
		return dir.listFiles((d, name) -> name.endsWith(".zip"));
	}
}
