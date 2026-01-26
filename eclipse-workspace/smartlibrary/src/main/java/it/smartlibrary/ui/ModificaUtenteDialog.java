package it.smartlibrary.ui;

import it.smartlibrary.dao.impl.DAOFactory;
import it.smartlibrary.model.Utente;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class ModificaUtenteDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JTextField campoNome = new JTextField();
	private final JTextField campoCognome = new JTextField();
	private final JTextField campoEmail = new JTextField();
	private final JTextField campoTelefono = new JTextField();
	private final JComboBox<String> campoTipo = new JComboBox<>(new String[]{"USER", "ADMIN"});
	private final JPasswordField campoPassword = new JPasswordField();
	private final JPasswordField campoConferma = new JPasswordField();

	private boolean saved = false;
	private Utente utente;

	//Regex email robusta
	private static final Pattern EMAIL_REGEX =
			Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	public ModificaUtenteDialog(Window owner, Utente utenteEsistente) {
		super(owner, utenteEsistente == null ? "Nuovo Utente" : "Modifica Utente", ModalityType.APPLICATION_MODAL);

		this.utente = utenteEsistente;

		setLayout(new BorderLayout(10, 10));
		setPreferredSize(new Dimension(500, 450));

		JPanel form = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 12, 8, 12);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;

		addRow(form, gbc, "Nome", campoNome);
		addRow(form, gbc, "Cognome", campoCognome);
		addRow(form, gbc, "Email", campoEmail);
		addRow(form, gbc, "Telefono (opzionale)", campoTelefono);
		addRow(form, gbc, "Tipo Utente", campoTipo);

		// Password solo se nuovo utente
		if (utenteEsistente == null) {
			addRow(form, gbc, "Password", campoPassword);
			addRow(form, gbc, "Conferma Password", campoConferma);
		}

		add(form, BorderLayout.CENTER);

		// Bottoni
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnSalva = new JButton("Salva");
		JButton btnAnnulla = new JButton("Annulla");
		btnPanel.add(btnAnnulla);
		btnPanel.add(btnSalva);
		add(btnPanel, BorderLayout.SOUTH);

		btnAnnulla.addActionListener(e -> setVisible(false));
		btnSalva.addActionListener(e -> doSave());

		// Se modifica, precompila i campi
		if (utenteEsistente != null) {
			campoNome.setText(utenteEsistente.getNome());
			campoCognome.setText(utenteEsistente.getCognome());
			campoEmail.setText(utenteEsistente.getEmail());
			campoTelefono.setText(utenteEsistente.getTelefono());
			campoTipo.setSelectedItem(utenteEsistente.getTipo());
		}

		pack();
		setLocationRelativeTo(owner);
	}

	private void addRow(JPanel p, GridBagConstraints gbc, String label, JComponent field) {
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		p.add(new JLabel(label + ":"), gbc);

		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		if (field instanceof JTextField) {
			((JTextField) field).setColumns(25);
		}

		p.add(field, gbc);
		gbc.gridy++;
	}

	private void doSave() {
		try {
			String nome = campoNome.getText().trim();
			String cognome = campoCognome.getText().trim();
			String email = campoEmail.getText().trim().toLowerCase();
			String telefono = campoTelefono.getText().trim();
			String tipo = campoTipo.getSelectedItem().toString();

			if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty()) {
				showError("Compila tutti i campi obbligatori");
				return;
			}

			// Validazione email
			if (!EMAIL_REGEX.matcher(email).matches()) {
				showError("Email non valida");
				return;
			}

			// Telefono opzionale
			if (telefono.isEmpty()) telefono = null;

			// Se nuovo utente → controlla password
			String passwordHash = null;
			if (utente == null) {
				String pwd = new String(campoPassword.getPassword()).trim();
				String conf = new String(campoConferma.getPassword()).trim();

				if (pwd.isEmpty()) {
					showError("La password è obbligatoria");
					return;
				}
				if (!pwd.equals(conf)) {
					showError("Le password non coincidono");
					return;
				}

				passwordHash = BCrypt.hashpw(pwd, BCrypt.gensalt());
			}

			// Se nuovo utente → crea oggetto
			if (utente == null) {
				utente = new Utente();
				utente.setId(java.util.UUID.randomUUID().toString());
				utente.setDataIscrizione(LocalDate.now());
			}

			// Aggiorna campi
			utente.setNome(nome);
			utente.setCognome(cognome);
			utente.setEmail(email);
			utente.setTelefono(telefono);
			utente.setTipo(tipo);

			if (passwordHash != null) {
				utente.setPasswordHash(passwordHash);
			}

			// Salvataggio
			if (DAOFactory.getUtenteDAO().findById(utente.getId()) == null) {
				DAOFactory.getUtenteDAO().save(utente);
			} else {
				DAOFactory.getUtenteDAO().update(utente);
			}

			saved = true;
			setVisible(false);

		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Errore salvataggio: " + ex.getMessage());
		}
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Errore", JOptionPane.ERROR_MESSAGE);
	}

	public boolean isSaved() {
		return saved;
	}
}
