package it.smartlibrary.ui;

import it.smartlibrary.model.Utente;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UtenteTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private final String[] colonne = {
			"ID", "Nome", "Cognome", "Email", "Telefono", "Tipo", "Data iscrizione"
	};

	private List<Utente> utenti = new ArrayList<>();

	public UtenteTableModel(List<Utente> utenti) {
		if (utenti != null) {
			this.utenti = utenti;
		}
	}

	public void setData(List<Utente> utenti) {
		this.utenti = utenti != null ? utenti : new ArrayList<>();
		fireTableDataChanged();
	}

	public Utente getUtenteAt(int row) {
		if (row < 0 || row >= utenti.size()) return null;
		return utenti.get(row);
	}

	@Override
	public int getRowCount() {
		return utenti.size();
	}

	@Override
	public int getColumnCount() {
		return colonne.length;
	}

	@Override
	public String getColumnName(int column) {
		return colonne[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Utente u = utenti.get(rowIndex);

		return switch (columnIndex) {
		case 0 -> u.getId();
		case 1 -> u.getNome();
		case 2 -> u.getCognome();
		case 3 -> u.getEmail();
		case 4 -> u.getTelefono();
		case 5 -> u.getTipo();
		case 6 -> u.getDataIscrizione();
		default -> "";
		};
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// Per ora nessuna cella è editabile direttamente
		return false;
	}
}
