package it.smartlibrary.ui;

import it.smartlibrary.dao.impl.DAOFactory;
import it.smartlibrary.model.Utente;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UtenteManagementDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JTable table;
	private UtenteTableModel model;

	public UtenteManagementDialog(Frame owner) {
		super(owner, "Gestione Utenti", true);

		setLayout(new BorderLayout(10, 10));
		setPreferredSize(new Dimension(800, 500));

		// Carica utenti dal DB
		try {
			List<Utente> utenti = DAOFactory.getUtenteDAO().findAll();
			model = new UtenteTableModel(utenti);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Errore caricamento utenti: " + e.getMessage());
			model = new UtenteTableModel(null);
		}

		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);

		add(new JScrollPane(table), BorderLayout.CENTER);

		// Pannello bottoni
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton btnAdd = new JButton("Aggiungi");
		JButton btnEdit = new JButton("Modifica");
		JButton btnDelete = new JButton("Elimina");
		JButton btnRefresh = new JButton("Ricarica");

		buttons.add(btnAdd);
		buttons.add(btnEdit);
		buttons.add(btnDelete);
		buttons.add(btnRefresh);

		add(buttons, BorderLayout.SOUTH);

		// Azioni
		btnAdd.addActionListener(e -> doAdd());
		btnEdit.addActionListener(e -> doEdit());
		btnDelete.addActionListener(e -> doDelete());
		btnRefresh.addActionListener(e -> reload());

		pack();
		setLocationRelativeTo(owner);
	}

	// Aggiungi utente
	private void doAdd() {
		ModificaUtenteDialog dlg = new ModificaUtenteDialog(this, null);
		dlg.setVisible(true);

		if (dlg.isSaved()) {
			reload();
			JOptionPane.showMessageDialog(this, "Utente creato con successo");
		}
	}

	// Modifica utente
	private void doEdit() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Seleziona un utente");
			return;
		}

		Utente u = model.getUtenteAt(table.convertRowIndexToModel(row));

		ModificaUtenteDialog dlg = new ModificaUtenteDialog(this, u);
		dlg.setVisible(true);

		if (dlg.isSaved()) {
			reload();
			JOptionPane.showMessageDialog(this, "Modifica salvata con successo");
		}
	}

	// Elimina utente
	private void doDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Seleziona un utente");
			return;
		}

		Utente u = model.getUtenteAt(table.convertRowIndexToModel(row));

		int confirm = JOptionPane.showConfirmDialog(
				this,
				"Eliminare l'utente " + u.getEmail() + "?",
				"Conferma",
				JOptionPane.YES_NO_OPTION
				);

		if (confirm == JOptionPane.YES_OPTION) {
			try {
				DAOFactory.getUtenteDAO().delete(u.getId());
				reload();
				JOptionPane.showMessageDialog(this, "Utente eliminato");
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Errore eliminazione: " + ex.getMessage());
			}
		}
	}

	// Ricarica tabella
	private void reload() {
		try {
			model.setData(DAOFactory.getUtenteDAO().findAll());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Errore ricaricamento: " + e.getMessage());
		}
	}
}
