package it.smartlibrary.ui;

import it.smartlibrary.model.Libro;
import it.smartlibrary.model.Utente;
import it.smartlibrary.service.AuthService;
import it.smartlibrary.service.PrestitoService;
import it.smartlibrary.dao.impl.DAOFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	//Servizi principali
	private Utente utenteCorrente;
	private final AuthService authService;
	private final PrestitoService prestitoService;

	//Toolbar e controlli admin
	private final JToolBar toolbar = new JToolBar();
	private final JButton tastoAggiungi = new JButton("Aggiungi libro");

	private JLabel userLabel;

	//Tabella libri
	private LibroTableModel model;
	private JTable table;

	//Flag per evitare update DB durante la ricerca
	private boolean suppressTableEvents = false;

	//Componenti ricerca
	private JTextField campoRicerca;
	private JCheckBox checkRegex;

	public MainFrame(Utente utenteCorrente, AuthService authService) {
		this.utenteCorrente = utenteCorrente;
		this.authService = authService;

		PrestitoService tmp = null;
		try {
			tmp = new PrestitoService();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
					"Attenzione: impossibile inizializzare il servizio prestiti: " + e.getMessage(),
					"Errore", JOptionPane.ERROR_MESSAGE);
		}
		this.prestitoService = tmp;

		setTitle("Smart Library - Utente: " + (utenteCorrente != null ? utenteCorrente.getNome() : "ospite"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("MainFrame: chiudo H2...");
				it.smartlibrary.util.DBConnection.shutdown();
			}
		});

		initUI();
		pack();
		setLocationRelativeTo(null);
	}

	private void initUI() {

		getContentPane().setLayout(new BorderLayout(8, 8));

		boolean isAdmin = utenteCorrente != null &&
				"ADMIN".equalsIgnoreCase(utenteCorrente.getTipo() == null ? "" : utenteCorrente.getTipo().trim());

		//Toolbar 
		tastoAggiungi.setVisible(isAdmin);
		tastoAggiungi.setEnabled(isAdmin);
		tastoAggiungi.addActionListener(e -> {
			AggiungiLibroDialog dlg = new AggiungiLibroDialog(this);
			dlg.setVisible(true);
			reloadLibri();
		});
		toolbar.add(tastoAggiungi);

		//Top bar
		JPanel top = new JPanel(new BorderLayout());

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
		left.add(toolbar);

		JButton tastoRegistrati = new JButton("Registrati");
		JButton tastoPrestiti = new JButton("Prestiti");
		JButton tastoLogout = new JButton("Logout");
		JButton tastoGestioneUtenti = new JButton("Gestione Utenti");
		JButton tastoStatistiche = new JButton("Statistiche");
		JButton tastoBackup = new JButton("Backup");
		JButton tastoLog = new JButton("Log");

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
		actions.add(tastoRegistrati);
		actions.add(tastoPrestiti);
		actions.add(tastoLogout);
		actions.add(tastoGestioneUtenti);
		actions.add(tastoStatistiche);
		actions.add(tastoBackup);
		actions.add(tastoLog);


		left.add(actions);

		tastoRegistrati.setVisible(utenteCorrente == null);
		tastoRegistrati.addActionListener(e -> new RegisterDialog(this).setVisible(true));

		tastoGestioneUtenti.setVisible(isAdmin);
		tastoGestioneUtenti.addActionListener(e -> new UtenteManagementDialog(this).setVisible(true));

		tastoLog.setVisible(isAdmin);

		tastoStatistiche.addActionListener(e -> {
			JDialog dlg = new JDialog(this, "Statistiche prestiti", true);
			dlg.getContentPane().add(new StatistichePanel());
			dlg.pack();
			dlg.setLocationRelativeTo(this);
			dlg.setVisible(true);
		});

		tastoBackup.addActionListener(e -> {
			BackupDialog dlg = new BackupDialog(this);
			dlg.setVisible(true);
		});

		tastoLog.addActionListener(e -> {
			JDialog dlg = new JDialog(this, "Log di sistema", true);
			dlg.getContentPane().add(new LogViewerPanel());
			dlg.setSize(600, 400);
			dlg.setLocationRelativeTo(this);
			dlg.setVisible(true);
		});

		JLabel titolo = new JLabel("Benvenuto in Smart Library - Prototipo");
		titolo.setHorizontalAlignment(SwingConstants.CENTER);

		userLabel = new JLabel(utenteCorrente != null ?
				utenteCorrente.getNome() + " (" + utenteCorrente.getTipo() + ")" : "Ospite");

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		right.add(userLabel);

		top.add(left, BorderLayout.WEST);
		top.add(titolo, BorderLayout.CENTER);
		top.add(right, BorderLayout.EAST);

		getContentPane().add(top, BorderLayout.NORTH);

		//Pannello ricerca
		campoRicerca = new JTextField(20);

		String[] campi = { "Titolo", "Autore", "ISBN", "Categoria" };
		JComboBox<String> comboCampo = new JComboBox<>(campi);

		checkRegex = new JCheckBox("Regex");

		JButton tastoCerca = new JButton("Cerca");
		JButton tastoReset = new JButton("Reset");

		JPanel searchPanel = new JPanel();
		searchPanel.add(new JLabel("Cerca per:"));
		searchPanel.add(comboCampo);
		searchPanel.add(campoRicerca);
		searchPanel.add(checkRegex);
		searchPanel.add(tastoCerca);
		searchPanel.add(tastoReset);

		searchPanel.add(tastoReset);

		//Pannello ricerca sotto la top bar
		getContentPane().add(searchPanel, BorderLayout.SOUTH);

		//Tabella libri
		model = new LibroTableModel(java.util.Collections.emptyList());
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		//Menu popup 
		JPopupMenu popup = new JPopupMenu();
		JMenuItem editItem = new JMenuItem("Modifica");
		JMenuItem deleteItem = new JMenuItem("Elimina");
		popup.add(editItem);
		popup.add(deleteItem);

		editItem.setEnabled(isAdmin);
		deleteItem.setEnabled(isAdmin);

		editItem.addActionListener(ae -> {
			int row = table.getSelectedRow();
			if (row < 0) return;

			Libro l = model.getLibroAt(row);
			openEditDialog(l, row, model);
		});


		table.addMouseListener(new MouseAdapter() {

			@Override 
			public void mousePressed(MouseEvent e) { maybeShowPopup(e); }

			@Override
			public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }

			@Override 
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
					int row = table.rowAtPoint(e.getPoint());
					if (row >= 0)
						openEditDialog(model.getLibroAt(row), row, model);
				}
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int row = table.rowAtPoint(e.getPoint());
					if (row >= 0) table.setRowSelectionInterval(row, row);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		//Azione elimina
		deleteItem.addActionListener(ae -> {
			int row = table.getSelectedRow();
			if (row < 0) return;

			Libro l = model.getLibroAt(row);
			int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
					"Eliminare il libro " + l.getTitolo() + "?",
					"Conferma", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				new SwingWorker<Void, Void>() {
					@Override protected Void doInBackground() throws Exception {
						DAOFactory.getLibroDAO().delete(l.getId());
						return null;
					}
					@Override protected void done() {
						try { get(); reloadLibri(); }
						catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(MainFrame.this,
									"Errore eliminazione: " + ex.getMessage());
						}
					}
				}.execute();
			}
		});

		//Salvataggio automatico celle
		table.getModel().addTableModelListener(e -> {
			if (suppressTableEvents) return; // evita update durante ricerca

			if (e.getType() == TableModelEvent.UPDATE) {
				int row = e.getFirstRow();
				if (row >= 0) {
					Libro updated = model.getLibroAt(row);

					new SwingWorker<Void, Void>() {
						@Override protected Void doInBackground() throws Exception {
							DAOFactory.getLibroDAO().update(updated);
							return null;
						}
						@Override protected void done() {
							try { get(); }
							catch (Exception ex) {
								ex.printStackTrace();
								JOptionPane.showMessageDialog(MainFrame.this,
										"Errore salvataggio: " + ex.getMessage());
							}
						}
					}.execute();
				}
			}
		});

		//Prestiti
		tastoPrestiti.setEnabled(utenteCorrente != null && prestitoService != null);
		tastoPrestiti.addActionListener(e -> {
			JDialog dlg = new JDialog(this, "Gestione Prestiti", true);
			dlg.getContentPane().add(new PrestitiPanel(utenteCorrente, prestitoService));
			dlg.pack();
			dlg.setLocationRelativeTo(this);
			dlg.setVisible(true);
			reloadLibri();
		});

		//Logout
		tastoLogout.addActionListener(e -> {
			if (authService != null) authService.logout();
			dispose();
		});

		//Ricerca libri (regex)
		tastoCerca.addActionListener(e -> doSearchLibri(comboCampo));
		tastoReset.addActionListener(e -> {
			campoRicerca.setText("");
			checkRegex.setSelected(false);
			reloadLibri();
		});

		//Caricamento iniziale
		reloadLibri();
	}

	//Ricerca semplice + regex
	private void doSearchLibri(JComboBox<String> comboCampo) {
		String query = campoRicerca.getText().trim();
		boolean useRegex = checkRegex.isSelected();

		if (query.isEmpty()) {
			reloadLibri();
			return;
		}

		try {
			List<Libro> tutti = DAOFactory.getLibroDAO().findAll();
			List<Libro> filtrati = new ArrayList<>();

			String campo = comboCampo.getSelectedItem().toString();

			//Regex case-insensitive
			String regex = useRegex ? query : ".*" + query.toLowerCase() + ".*";

			for (Libro l : tutti) {

				String valore = switch (campo) {
				case "Titolo" -> l.getTitolo();
				case "Autore" -> l.getAutore();
				case "ISBN" -> l.getIsbn();
				case "Categoria" -> l.getCategoria();
				default -> "";
				};

				if (valore == null) valore = "";
				valore = valore.toLowerCase();

				if (valore.matches(regex)) {
					filtrati.add(l);
				}
			}

			suppressTableEvents = true;
			model.setData(filtrati);
			suppressTableEvents = false;

		} 
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Errore ricerca: " + ex.getMessage());
		}
	}

	//Ricarica libri dal db
	private void reloadLibri() {
		new SwingWorker<List<Libro>, Void>() {
			@Override protected List<Libro> doInBackground() throws Exception {
				return DAOFactory.getLibroDAO().findAll();
			}
			@Override protected void done() {
				try { model.setData(get()); }
				catch (Exception e) { e.printStackTrace(); }
			}
		}.execute();
	}

	//Login
	public void showLoginDialog() {
		LoginDialog dlg = new LoginDialog(this, authService);
		dlg.setVisible(true);

		Utente u = dlg.getUtenteLoggato();
		if (u != null) onUserLoggedIn(u);
	}

	//Aggiorna UI dopo login
	public void onUserLoggedIn(Utente u) {
		SwingUtilities.invokeLater(() -> {
			this.utenteCorrente = u;
			boolean isAdmin = "ADMIN".equalsIgnoreCase(u.getTipo());

			tastoAggiungi.setVisible(isAdmin);
			tastoAggiungi.setEnabled(isAdmin);

			userLabel.setText(u.getNome() + " (" + u.getTipo() + ")");

			revalidate();
			repaint();
		});
	}

	//Modifica libro
	private void openEditDialog(Libro libro, int row, LibroTableModel model) {
		Libro copy = new Libro(
				libro.getId(), libro.getTitolo(), libro.getAutore(), libro.getIsbn(),
				libro.getCategoria(), libro.getAnno(), libro.getCopieTotali(),
				libro.getCopieDisponibili(), libro.getDescrizione()
				);

		ModificaLibroDialog dlg = new ModificaLibroDialog(this, copy);
		dlg.setVisible(true);

		if (dlg.isSaved()) {
			Libro updated = dlg.getLibro();

			new SwingWorker<Void, Void>() {

				@Override 
				protected Void doInBackground() throws Exception {
					DAOFactory.getLibroDAO().update(updated);
					return null;
				}

				@Override
				protected void done() {
					try { 
						get(); 
						JOptionPane.showMessageDialog(
								MainFrame.this,
								"Modifica salvata con successo!",
								"Salvataggio completato",
								JOptionPane.INFORMATION_MESSAGE
								);
						reloadLibri(); 
					}
					catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(
								MainFrame.this,
								"Errore salvataggio: " + ex.getMessage(),
								"Errore",
								JOptionPane.ERROR_MESSAGE
								);
					}
				}
			}.execute();
		}
	}
}
