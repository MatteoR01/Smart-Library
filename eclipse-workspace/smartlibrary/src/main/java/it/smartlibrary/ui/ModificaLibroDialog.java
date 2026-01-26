package it.smartlibrary.ui;

import it.smartlibrary.model.Libro;
import javax.swing.*;
import java.awt.*;

public class ModificaLibroDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	//Indica se l’utente ha premuto "Salva"
	private boolean saved = false;
	private Libro libro;
	//Campi di input del form
	private JTextField titolo, autore, isbn, categoria, anno, disponibili;

	public ModificaLibroDialog(Frame owner, Libro libro) {
		//Dialog modale con titolo
		super(owner, "Modifica Libro", true);
		this.libro = libro;
		init();			// Inizializza la UI
		pack();
		setLocationRelativeTo(owner);
	}

	private void init() {
		//Pannello principale con layout a griglia 2 colonne
		JPanel p = new JPanel(new GridLayout(0,2,6,6));
		//Ogni riga: etichetta + campo precompilato con i dati del libro
		p.add(new JLabel("Titolo:")); titolo = new JTextField(libro.getTitolo()); p.add(titolo);
		p.add(new JLabel("Autore:")); autore = new JTextField(libro.getAutore()); p.add(autore);
		p.add(new JLabel("ISBN:")); isbn = new JTextField(libro.getIsbn()); p.add(isbn);
		p.add(new JLabel("Categoria:")); categoria = new JTextField(libro.getCategoria()); p.add(categoria);
		p.add(new JLabel("Anno:")); anno = new JTextField(String.valueOf(libro.getAnno())); p.add(anno);
		p.add(new JLabel("Disponibili:")); disponibili = new JTextField(String.valueOf(libro.getCopieDisponibili())); p.add(disponibili);

		//Pulsanti Salva / Annulla
		JButton save = new JButton("Salva");
		JButton cancel = new JButton("Annulla");
		JPanel btn = new JPanel(); btn.add(save); btn.add(cancel);

		//Azione del pulsante Salva
		save.addActionListener(e -> {
			//Aggiorna i campi del libro con i valori inseriti
			libro.setTitolo(titolo.getText());
			libro.setAutore(autore.getText());
			libro.setIsbn(isbn.getText());
			libro.setCategoria(categoria.getText());
			libro.setAnno(parseIntSafe(anno.getText(), libro.getAnno()));
			libro.setCopieDisponibili(parseIntSafe(disponibili.getText(), libro.getCopieDisponibili()));
			saved = true;			//modifiche confermate
			setVisible(false);		//chiude il dialog
		});
		
		//Azione del pulsante Annulla
		cancel.addActionListener(e -> { saved = false; setVisible(false); });

		//Layout generale del dialog
		getContentPane().setLayout(new BorderLayout(8,8));
		getContentPane().add(p, BorderLayout.CENTER);
		getContentPane().add(btn, BorderLayout.SOUTH);
	}

	//Converte una stringa in intero, con valore di fallback in caso di errore
	private int parseIntSafe(String s, int fallback) {
		try { return Integer.parseInt(s.trim()); } catch (Exception e) { return fallback; }
	}

	//Indica se l’utente ha premuto "Salva"
	public boolean isSaved() { return saved; }
	//Restituisce il libro modificato
	public Libro getLibro() { return libro; }
}