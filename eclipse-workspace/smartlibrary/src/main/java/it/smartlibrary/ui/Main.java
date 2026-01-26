package it.smartlibrary.ui;

import java.io.File;

import javax.swing.SwingUtilities;

import it.smartlibrary.model.Utente;
import it.smartlibrary.service.AuthService;
import it.smartlibrary.util.DBConnection;
import it.smartlibrary.util.DatabaseInit;
import it.smartlibrary.util.LogService;

public class Main {
	public static void main(String[] args) {
		
		LogService.getLogger().info("Logger avviato");
		System.out.println("Working dir: " + new File(".").getAbsolutePath());
		System.out.println("Log file exists? " + new File("logs/smartlibrary.log").exists());

		
		try { 
			DBConnection.getInstance(); //Forza apertura DB 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
			System.exit(1); 
			}
		
		//Inizializza DB
		DatabaseInit.initIfNeeded();
				
		LogService.getLogger().info("Autenticazione avviata");

		
		//Avvia GUI
		SwingUtilities.invokeLater(() -> {
			try {
				AuthService auth = new AuthService();
				
				//Mostra dialog di login
				LoginDialog loginDialog = new LoginDialog(null, auth);
				loginDialog.setVisible(true);
				
				//Recupera utente loggato
				Utente loggato = loginDialog.getUtenteLoggato();
				if(loggato == null) {
					//Nessun login: quindi termina oppure apri in sola lettura
					System.out.println("Nessun utente autenticato. L'app verrà chiusa.");
					System.exit(0);
				}
				 
				MainFrame mainFrame = new MainFrame(loggato, auth);
				
				mainFrame.addWindowListener(new java.awt.event.WindowAdapter() { 
					@Override 
					public void windowClosing(java.awt.event.WindowEvent e) { 
						System.out.println("MainFrame: chiudo H2..."); 
						DBConnection.shutdown(); } 
				});
				
				mainFrame.setVisible(true);
			}
			catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		});
	}
} 
