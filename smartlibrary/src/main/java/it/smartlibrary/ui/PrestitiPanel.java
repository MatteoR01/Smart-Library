package it.smartlibrary.ui;

import it.smartlibrary.model.Libro;
import it.smartlibrary.model.Prestito;
import it.smartlibrary.model.Utente;
import it.smartlibrary.service.PrestitoService;
import it.smartlibrary.dao.impl.DAOFactory;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PrestitiPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JTable table;
	private final LibroTableModel model;
	private final JTextField campoGiorni;
	private final JButton tastoPresta;
	private final JButton tastoRestituisci;
	private final JButton tastoEstendi;
	private final Utente utenteCorrente;
	private final PrestitoService prestitoService;

	//Lunghezza abbreviata degli ID mostrati nella lista
	private static final int SHORT_ID_LEN = 6;
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	public PrestitiPanel(Utente utenteCorrente, PrestitoService prestitoService) {
		this.utenteCorrente = utenteCorrente;
		this.prestitoService = prestitoService;

		setLayout(new BorderLayout(8, 8));

		//Tabella libri
		model = new LibroTableModel(java.util.Collections.emptyList());
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(table), BorderLayout.CENTER);

		//Pannello sud: prestito e restituzione
		JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
		south.add(new JLabel("Giorni di prestito:"));
		campoGiorni = new JTextField("14", 4);
		south.add(campoGiorni);

		tastoPresta = new JButton("Prendi in prestito");
		tastoPresta.setEnabled(utenteCorrente != null && prestitoService != null);
		south.add(tastoPresta);

		tastoRestituisci = new JButton("Restituisci un prestito");
		tastoRestituisci.setEnabled(utenteCorrente != null && prestitoService != null);
		south.add(tastoRestituisci);
		
		tastoEstendi = new JButton("Estendi prestito");
		if ("ADMIN".equalsIgnoreCase(utenteCorrente.getTipo())) {
		    south.add(tastoEstendi);
		}


		add(south, BorderLayout.SOUTH);

		//Pannello nord: info utente
		JLabel infoUtente = new JLabel("Utente: " + (utenteCorrente != null ? utenteCorrente.getNome() + " (" + utenteCorrente.getId() + ")" : "Ospite"));
		add(infoUtente, BorderLayout.NORTH);

		//Carica i libri all'avvio
		loadLibri();

		//Azioni dei pulsanti
		tastoPresta.addActionListener(e -> doPresta());
		tastoRestituisci.addActionListener(e -> doRestituisci());
		tastoEstendi.addActionListener(e -> doEstendi());
	}

    //Gestisce la logica del prestito di un libro
	private void doPresta() {
		int row = table.getSelectedRow();
		// Nessun libro selezionato
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Seleziona un libro dalla tabella", "Attenzione", JOptionPane.WARNING_MESSAGE);
			return;
		}
		Libro l = model.getLibroAt(row);
		int giorni = parseIntSafe(campoGiorni.getText(), 14);	// fallback 14 giorni
		tastoPresta.setEnabled(false);
		
		//Esegue il prestito in background per non bloccare la GUI
		new javax.swing.SwingWorker<Void, Void>() {
			@Override protected Void doInBackground() throws Exception {
				prestitoService.prestaLibro(l.getId(), utenteCorrente.getId(), giorni);
				return null;
			}
			@Override protected void done() {
				tastoPresta.setEnabled(true);
				try {
					get();	// verifica eventuali eccezioni
					JOptionPane.showMessageDialog(PrestitiPanel.this, "Prestito registrato con successo");
					loadLibri();	// aggiorna disponibilità
				} 
				catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(PrestitiPanel.this, "Errore prestito: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
				}
			}
		}.execute();
	}

	//Gestisce la restituzione di un prestito attivo.
	private void doRestituisci() {
		try {
			// Recupera i prestiti attivi dell'utente
			List<Prestito> attivi = prestitoService.findByUser(utenteCorrente.getId())
					.stream().filter(p -> p.getDataRestituzione() == null).toList();
			if (attivi.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Nessun prestito attivo per l'utente", "Info", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			// Costruisce la lista da mostrare nel dialog
			String[] items = new String[attivi.size()];
			for (int i = 0; i < attivi.size(); i++) {
				Prestito p = attivi.get(i);
				// Recupera il titolo del libro
				String titolo;
				String idLibro = p.getIdLibro();
				try {
					Libro libro = DAOFactory.getLibroDAO().findById(idLibro);
					titolo = (libro != null && libro.getTitolo() != null && !libro.getTitolo().isBlank()) ? libro.getTitolo() : idLibro;
				} 
				catch (Exception ex) {
					titolo = idLibro;
				}

				String shortId = idLibro == null ? "n/a" : abbreviateId(idLibro, SHORT_ID_LEN);

				// Formatta la data di scadenza
				String scadenza = "N/A";
				if (p.getDataScadenza() != null) {
					scadenza = DATE_FMT.format(p.getDataScadenza().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				}

				items[i] = String.format("%s (scadenza: %s) [id: %s]", titolo, scadenza, shortId);
			}

			// Dialog di selezione prestito da restituire
			String sel = (String) JOptionPane.showInputDialog(this, "Seleziona prestito da restituire", "Restituisci", JOptionPane.PLAIN_MESSAGE, null, items, items[0]);
			if (sel == null) return;

			// Trova l'indice selezionato
			int selectedIndex = -1;
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(sel)) {
					selectedIndex = i;
					break;
				}
			}
			if (selectedIndex < 0) return;

			final String idPrestitoFinal = attivi.get(selectedIndex).getIdPrestito();

			tastoRestituisci.setEnabled(false);
			// Restituzione in background
			new javax.swing.SwingWorker<Void, Void>() {
				@Override protected Void doInBackground() throws Exception {
					prestitoService.restituisciLibro(idPrestitoFinal);
					return null;
				}
				@Override
				protected void done() {
				    try {
				        get(); // prova a ottenere il risultato
				        loadLibri();
				    } catch (ExecutionException ex) {
				        Throwable cause = ex.getCause();

				        JOptionPane.showMessageDialog(
				            PrestitiPanel.this,
				            cause.getMessage(),
				            "Errore prestito",
				            JOptionPane.ERROR_MESSAGE
				        );

				    } catch (Exception ex) {
				        ex.printStackTrace();
				        JOptionPane.showMessageDialog(
				            PrestitiPanel.this,
				            "Errore inatteso: " + ex.getMessage(),
				            "Errore",
				            JOptionPane.ERROR_MESSAGE
				        );
				    }
				}

			}.execute();
		} 
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Errore caricamento prestiti: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	//Estende il tempo di un prestito
	private void doEstendi() {
	    try {
	        // Solo admin
	        if (!"ADMIN".equalsIgnoreCase(utenteCorrente.getTipo())) {
	            JOptionPane.showMessageDialog(this, "Solo un amministratore può estendere un prestito");
	            return;
	        }

	        // Recupera prestiti attivi
	        List<Prestito> attivi = prestitoService.findByUser(utenteCorrente.getId())
	                .stream().filter(p -> p.getDataRestituzione() == null).toList();

	        if (attivi.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Nessun prestito attivo da estendere");
	            return;
	        }

	        // Lista da mostrare
	        String[] items = new String[attivi.size()];
	        for (int i = 0; i < attivi.size(); i++) {
	            Prestito p = attivi.get(i);
	            Libro libro = DAOFactory.getLibroDAO().findById(p.getIdLibro());
	            String titolo = libro != null ? libro.getTitolo() : p.getIdLibro();
	            items[i] = titolo + " (scadenza: " + p.getDataScadenza() + ")";
	        }

	        String sel = (String) JOptionPane.showInputDialog(
	                this,
	                "Seleziona prestito da estendere",
	                "Estendi prestito",
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                items,
	                items[0]
	        );

	        if (sel == null) return;

	        int index = -1;
	        for (int i = 0; i < items.length; i++) {
	            if (items[i].equals(sel)) {
	                index = i;
	                break;
	            }
	        }

	        Prestito p = attivi.get(index);

	        String input = JOptionPane.showInputDialog(this, "Giorni di estensione:", "7");
	        if (input == null) return;

	        int giorni = Integer.parseInt(input);

	        prestitoService.estendiPrestito(p.getIdPrestito(), giorni, utenteCorrente);

	        JOptionPane.showMessageDialog(this, "Prestito esteso con successo");
	        loadLibri();

	    } 
	    catch (Exception ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Errore estensione: " + ex.getMessage());
	    }
	}


	//Abbrevia un ID per mostrarlo in modo più compatto.
	private String abbreviateId(String id, int len) {
		if (id == null) return "n/a";
		if (id.length() <= len) return id;
		return id.substring(id.length() - len);
	}

    //Carica tutti i libri dal database e aggiorna la tabella.
	private void loadLibri() {
		new javax.swing.SwingWorker<List<Libro>, Void>() {
			@Override
			protected List<Libro> doInBackground() throws Exception {
			    long start = System.currentTimeMillis();

			    List<Libro> libri = DAOFactory.getLibroDAO().findAll();

			    long end = System.currentTimeMillis();
			    System.out.println("Caricamento catalogo: " + (end - start) + " ms");

			    return libri;
			}

			@Override protected void done() {
				try { model.setData(get()); } catch (Exception e) { e.printStackTrace(); }
			}
		}.execute();
	}

    //Converte una stringa in intero, con valore di fallback in caso di errore.
	private int parseIntSafe(String s, int fallback) {
		try { return Integer.parseInt(s.trim()); } catch (Exception e) { return fallback; }
	}
}
