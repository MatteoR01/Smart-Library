package it.smartlibrary.util;

import java.io.File;
import java.util.logging.*;

public class LogService {

	private static final Logger logger = Logger.getLogger("SmartLibraryLogger");

	static {
		try {
			logger.setUseParentHandlers(false);

			String baseDir = new File(System.getProperty("user.dir")).getCanonicalPath();
			File logDir = new File(baseDir, "logs");
			logDir.mkdirs();

			File logFile = new File(logDir, "app.log");

			Handler fh = new FileHandler(logFile.getAbsolutePath(), true);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);

			logger.setLevel(Level.INFO);

			System.out.println("CREATO FILE LOG: " + logFile.getAbsolutePath());
			System.out.println("ESISTE? " + logFile.exists());

		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Logger getLogger() {
		return logger;
	}
}
