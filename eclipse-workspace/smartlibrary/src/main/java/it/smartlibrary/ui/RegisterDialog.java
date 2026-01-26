package it.smartlibrary.ui;

import it.smartlibrary.model.Utente;
import it.smartlibrary.dao.impl.DAOFactory;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	//Campi del form di registrazione
	private final JTextField campoNome = new JTextField();
	private final JTextField campoCognome = new JTextField();
	private final JTextField campoEmail = new JTextField();
	private final JTextField campoTelefono = new JTextField();
	private final JPasswordField campoPassword = new JPasswordField();
	private final JPasswordField campoConferma = new JPasswordField();
	
	//Pulsanti azione
	private final JButton tastoRegistra = new JButton("Registrati");
	private final JButton tastoAnnulla = new JButton("Annulla");

	public RegisterDialog(Frame owner) {
		
		//Dialog modale con titolo
		super(owner, "Registrazione nuovo utente", true);
		setLayout(new BorderLayout());

		//Form di input
		JPanel form = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 12, 8, 12);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0; gbc.gridy = 0;

		//Aggiunge le righe del form
		addRow(form, gbc, "Nome", campoNome);
		addRow(form, gbc, "Cognome", campoCognome);
		addRow(form, gbc, "Email", campoEmail);
		addRow(form, gbc, "Telefono (opzionale)", campoTelefono);
		addRow(form, gbc, "Password", campoPassword);
		addRow(form, gbc, "Conferma password", campoConferma);

		//Pannello bottoni
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPanel.add(tastoAnnulla);
		btnPanel.add(tastoRegistra);

		add(form, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
		
		//Permette di premere "Invio" per registrarsi
		getRootPane().setDefaultButton(tastoRegistra);
		
		//Dimensioni e posizione
		setPreferredSize(new Dimension(500, 400));
		pack();
		setLocationRelativeTo(owner);

		// AZIONI DEI BOTTONI
		
		//Chiude il dialog senza registrare
		tastoAnnulla.addActionListener(e -> setVisible(false));
		
		//Avvia la procedura di registrazione
		tastoRegistra.addActionListener(e -> doRegister());
	}

    //Aggiunge una riga al form: etichetta + campo di input.
	private void addRow(JPanel p, GridBagConstraints gbc, String label, JComponent field) {

		//Etichetta (colonna 0)
		gbc.gridx = 0; 
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		p.add(new JLabel(label + ":"), gbc);

		//Campo (colonna 1) - occupa lo spazio rimanente
		gbc.gridx = 1; 
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;								//permette al campo di espandersi
		gbc.fill = GridBagConstraints.HORIZONTAL; 		//riempie orizzontalmente
		if (field instanceof JTextField) { 
			((JTextField) field).setColumns(30);		// suggerisce larghezza 
			field.setPreferredSize(new Dimension(320, 26));
		}
		p.add(field, gbc);
		gbc.gridy++;
	}

	//Logica di registrazione dell’utente
	private void doRegister() {
		try {
			//Recupera i valori dai campi
			String nome = campoNome.getText().trim();
			String cognome = campoCognome.getText().trim();
			String email = campoEmail.getText().trim().toLowerCase();
			String telefono = campoTelefono.getText().trim();
			String password = new String(campoPassword.getPassword()).trim();
			String conferma = new String(campoConferma.getPassword()).trim();

			//Validazione campi obbligatori
			if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
				showError("Compila tutti i campi obbligatori");
				return;
			}
			
			//Controllo password
			if (!password.equals(conferma)) {
				showError("Le password non coincidono");
				return;
			}
			
			if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
			    showError("Email non valida");
			    return;
			}
			
			//Controllo email già registrata
			if (DAOFactory.getUtenteDAO().findByEmail(email) != null) {
				showError("Email già registrata");
				return;
			}
			
			//Se il campo telefono è vuoto, salva null
			if (telefono.isEmpty()) { 
				telefono = null; 
			}

			//Crea il nuovo utente
			Utente nuovo = new Utente();
			nuovo.setId(java.util.UUID.randomUUID().toString());
			nuovo.setNome(nome);
			nuovo.setCognome(cognome);
			nuovo.setEmail(email);
			nuovo.setTelefono(telefono);
			nuovo.setTipo("USER");
			
			//Imposta la data di iscrizione
			nuovo.setDataIscrizione(java.time.LocalDate.now());
			
			//Hash della password con BCrypt
			nuovo.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));

			//Salva nel database
			DAOFactory.getUtenteDAO().save(nuovo);
			
			//Conferma registrazione
			JOptionPane.showMessageDialog(this, "Registrazione completata con successo", "OK", JOptionPane.INFORMATION_MESSAGE);
			setVisible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Errore registrazione: " + ex.getMessage());
		}
	}

    //Mostra un messaggio di errore.
	private void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Errore", JOptionPane.ERROR_MESSAGE);
	}
}
