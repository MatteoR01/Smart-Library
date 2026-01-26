package it.smartlibrary.ui;

import it.smartlibrary.model.Utente;
import it.smartlibrary.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final AuthService authService;
	private Utente utenteLoggato;
	private final JTextField campoEmail;
	private final JPasswordField campoPassword;
	private final JButton tastoAccedi;
	private final JButton tastoAnnulla;
	private final JButton tastoRegistrati;

	public LoginDialog(Frame owner, AuthService authService) {
		//Dialog modale con titolo
		super(owner, "Login", true);
		this.authService = authService;

		//Inizializzazione componenti
		campoEmail = new JTextField();
		campoPassword = new JPasswordField();
		tastoAccedi = new JButton("Accedi");
		tastoAnnulla = new JButton("Annulla");
		tastoRegistrati = new JButton("Registrati");

		//Layout con GridBag per controllo dimensioni e spaziatura
		JPanel content = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 14, 6, 14);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		//Campo email
		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
		content.add(new JLabel("Email:"), gbc);
		gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
		campoEmail.setColumns(30);
		content.add(campoEmail, gbc);

		//Campo password
		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
		content.add(new JLabel("Password:"), gbc);
		gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
		campoPassword.setColumns(30);
		content.add(campoPassword, gbc);

		//Pannello pulsanti
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		btnPanel.add(tastoRegistrati);
		btnPanel.add(tastoAccedi);
		btnPanel.add(tastoAnnulla);

		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(12, 14, 12, 14);
		content.add(btnPanel, gbc);

		getContentPane().add(content);

		//Dimensioni e comportamento
		setPreferredSize(new Dimension(520, 200));
		setMinimumSize(new Dimension(420, 180));
		setResizable(true);
		pack();
		setLocationRelativeTo(owner);

		//Azioni dei pulsanti
		
		//Apre il dialog di registrazione
		tastoRegistrati.addActionListener(e -> { 
			RegisterDialog reg = new RegisterDialog((Frame) getOwner()); 
			reg.setVisible(true); 
		});

		//Effettua login
		tastoAccedi.addActionListener(e -> doLogin());
		
		//Annulla login
		tastoAnnulla.addActionListener(e -> {
			utenteLoggato = null;
			setVisible(false);
		});

		//Invio con Enter dalla password
		campoPassword.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
			}
		});

		//Focus iniziale sul campo email
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override public void windowOpened(java.awt.event.WindowEvent e) {
				campoEmail.requestFocusInWindow();
			}
		});
	}
	
	//Esegue la procedura di login - valida i campi, chiama AuthService, gestisce errori e messaggi
	private void doLogin() {
		String email = campoEmail.getText() == null ? "" : campoEmail.getText().trim().toLowerCase();
		String password = campoPassword.getPassword() == null ? "" : new String(campoPassword.getPassword()).trim();

		if (email.isBlank() || password.isBlank()) {
			JOptionPane.showMessageDialog(this, "Inserisci email e password", "Attenzione", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {

			//Tentativo di login
			Utente utente = authService.login(email, password);
			System.out.println("DEBUG login -> u = " + utente + " email=" + (utente==null? "null": utente.getEmail()) + " tipo=" + (utente==null? "null": utente.getTipo()));

			if (utente != null) {
				//Login riuscito
				this.utenteLoggato = utente;
				JOptionPane.showMessageDialog(this, "Login effettuato: " + utente.getNome(), "Benvenuto", JOptionPane.INFORMATION_MESSAGE);
				setVisible(false);
			} 
			else {
				//Credenziali errate
				JOptionPane.showMessageDialog(this, "Email o password errati", "Errore", JOptionPane.ERROR_MESSAGE);
				campoPassword.setText("");
				campoPassword.requestFocusInWindow();
			}
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Errore durante il login: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
		}
	}

	//Restituisce l'utente loggato, null se login fallito.

	public Utente getUtenteLoggato() {
		return utenteLoggato;
	}
}
