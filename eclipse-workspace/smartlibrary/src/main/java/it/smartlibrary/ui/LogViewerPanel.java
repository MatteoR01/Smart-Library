package it.smartlibrary.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class LogViewerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public LogViewerPanel() {
		setLayout(new BorderLayout());

		JTextArea area = new JTextArea();
		area.setEditable(false);

		Path logPath = Path.of(System.getProperty("user.dir"), "logs", "app.log");
		
		try {
			File logFile = logPath.toFile();

			if (!logFile.exists()) {
				area.setText("Il file di log non esiste ancora.\nPercorso: " + logPath);
			} 
			else {
				String content = Files.readString(logPath);

				if (content.isBlank()) {
					area.setText("Il file di log è vuoto.\nPercorso: " + logPath);
				} 
				else {
					area.setText(content);
				}
			}

		} 
		catch (Exception e) {
			area.setText("Impossibile leggere il file di log:\n" + logPath + "\n\n" + e.getMessage());
		}

		add(new JScrollPane(area), BorderLayout.CENTER);
	}
}
