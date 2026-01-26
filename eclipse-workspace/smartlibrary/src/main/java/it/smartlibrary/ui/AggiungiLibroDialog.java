package it.smartlibrary.ui;

import it.smartlibrary.model.Libro;
import it.smartlibrary.dao.impl.DAOFactory;

import javax.swing.*;
import java.awt.*;

public class AggiungiLibroDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	//Campi del form per inserire i dati del nuovo libro
	private final JTextField campoId;
	private final JTextField campoTitolo;
	private final JTextField campoAutore;
	private final JTextField campoIsbn;
	private final JTextField campoCategoria;
	private final JTextField campoAnno;
	private final JTextField campoCopieTotali;
	private final JTextField campoCopieDisponibili;
	private final JTextArea campoDescrizione;
	
	//Pulsanti di azione
	private final JButton tastoSalva;
	private final JButton tastoAnnulla;

	public AggiungiLibroDialog(Frame owner) {
		//Dialog modale con titolo
		super(owner, "Aggiungi nuovo libro", true);

		//Inizializzazione campi
		campoId = new JTextField();
		campoTitolo = new JTextField();
		campoAutore = new JTextField();
		campoIsbn = new JTextField();
		campoCategoria = new JTextField();
		campoAnno = new JTextField();
		campoCopieTotali = new JTextField();
		campoCopieDisponibili = new JTextField();
		campoDescrizione = new JTextArea(4, 30);
		tastoSalva = new JButton("Salva");
		tastoAnnulla = new JButton("Annulla");

		//Layout principale con GridBagLayout
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6,8,6,8);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0; gbc.gridy = 0;

		//Aggiunge tutte le righe del form
		addRow(p, gbc, "ID (opzionale)", campoId, "Se lasci vuoto viene generato automaticamente (UUID)");
		addRow(p, gbc, "Titolo", campoTitolo, "Obbligatorio. Esempio: Il Nome della Rosa");
		addRow(p, gbc, "Autore", campoAutore, "Obbligatorio. Esempio: Umberto Eco");
		addRow(p, gbc, "ISBN", campoIsbn, "Formato numerico o con trattini. Esempio: 9788806170000");
		addRow(p, gbc, "Categoria", campoCategoria, "Esempio: Narrativa, Tecnico, Storia");
		addRow(p, gbc, "Anno pubblicazione", campoAnno, "Numero intero 4 cifre. Esempio: 1980");
		addRow(p, gbc, "Copie totali", campoCopieTotali, "Intero >= 0");
		addRow(p, gbc, "Copie disponibili", campoCopieDisponibili, "Intero >= 0 e <= copie totali");
		addTextAreaRow(p, gbc, "Descrizione", campoDescrizione, "Breve descrizione (opzionale)");

		//Pannello pulsanti
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPanel.add(tastoAnnulla);
		btnPanel.add(tastoSalva);
		gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
		p.add(btnPanel, gbc);

		//Aggiunge il pannello al dialog
		getContentPane().add(new JScrollPane(p));
		
		//Dimensioni e posizionamento
		setPreferredSize(new Dimension(640, 520));
		pack();
		setLocationRelativeTo(owner);

		//Azioni pulsanti
		tastoAnnulla.addActionListener(e -> setVisible(false));
		tastoSalva.addActionListener(e -> onSave());
	}

	//Aggiunge una riga composta da etichetta, campo di input e suggerimento.
	private void addRow(JPanel p, GridBagConstraints gbc, String label, JComponent field, String hint) {
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//Etichetta
		gbc.gridx = 0;
		p.add(new JLabel(label + ":"), gbc);
		// Campo di input
		gbc.gridx = 1;
		p.add(field, gbc);
		// Riga successiva
		gbc.gridy++;
		// Suggerimento
		gbc.gridx = 0; gbc.gridwidth = 2;
		JLabel hintLabel = new JLabel("<html><i style='color:gray;'>" + hint + "</i></html>");
		p.add(hintLabel, gbc);
		gbc.gridy++;
	}

	
    //Variante per campi descrizione.
	private void addTextAreaRow(JPanel p, GridBagConstraints gbc, String label, JTextArea area, String hint) {
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		p.add(new JLabel(label + ":"), gbc);
		// TextArea con scroll
		gbc.gridx = 1;
		p.add(new JScrollPane(area), gbc);
		gbc.gridy++;
		gbc.gridx = 0; gbc.gridwidth = 2;
		JLabel hintLabel = new JLabel("<html><i style='color:gray;'>" + hint + "</i></html>");
		p.add(hintLabel, gbc);
		gbc.gridy++;
	}

	//Logica di salvataggio del nuovo libro.
	private void onSave() {
		try {
			//Validazioni campi obbligatori
			String titolo = campoTitolo.getText().trim();
			String autore = campoAutore.getText().trim();
			if (titolo.isEmpty()) { showError("Il campo Titolo è obbligatorio"); return; }
			if (autore.isEmpty()) { showError("Il campo Autore è obbligatorio"); return; }

			//Parsing numeri con messaggi di errore dedicati
			int anno = parseIntOrFail(campoAnno.getText().trim(), "Anno pubblicazione deve essere un numero intero (es. 1980)");
			int copieTot = parseIntOrFail(campoCopieTotali.getText().trim(), "Copie totali deve essere un numero intero >= 0");
			int copieDisp = parseIntOrFail(campoCopieDisponibili.getText().trim(), "Copie disponibili deve essere un numero intero >= 0");
			if (copieTot < 0 || copieDisp < 0) { showError("I valori copie devono essere >= 0"); return; }
			if (copieDisp > copieTot) { showError("Copie disponibili non può essere maggiore di copie totali"); return; }

			//Costruzione oggetto Libro
			Libro l = new Libro();
			String id = campoId.getText().trim();
			if (id.isEmpty()) id = java.util.UUID.randomUUID().toString();
			l.setId(id);
			l.setTitolo(titolo);
			l.setAutore(autore);
			l.setIsbn(campoIsbn.getText().trim());
			l.setCategoria(campoCategoria.getText().trim());
			l.setAnno(anno);
			l.setCopieTotali(copieTot);
			l.setCopieDisponibili(copieDisp);
			l.setDescrizione(campoDescrizione.getText().trim());

			//Salva via DAO
			DAOFactory.getLibroDAO().save(l);

			JOptionPane.showMessageDialog(this, "Libro salvato con successo", "OK", JOptionPane.INFORMATION_MESSAGE);
			setVisible(false);
		} 
		catch (NumberFormatException nfe) {
			//Messaggio già gestito in parseIntOrFail
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			showError("Errore salvataggio: " + ex.getMessage());
		}
	}

	//Converte una stringa in intero, mostrando un messaggio di errore personalizzato.
	private int parseIntOrFail(String s, String errMsg) {
		try {
			return Integer.parseInt(s);
		} 
		catch (NumberFormatException e) {
			showError(errMsg);
			throw e;
		}
	}
	
	//Mostra un messaggio di errore standard.
	private void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Errore", JOptionPane.ERROR_MESSAGE);
	}
}
