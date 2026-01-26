package it.smartlibrary.ui;

import it.smartlibrary.model.Libro;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class LibroTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	//Nomi delle colonne visualizzate nella JTable
	private final String[] columns = {"ID","Titolo","Autore","ISBN","Categoria","Anno","Disponibili"};
	private List<Libro> data;

	public LibroTableModel(List<Libro> data) { this.data = data; }

	public void setData(List<Libro> data) { this.data = data; fireTableDataChanged(); }

	@Override public int getRowCount() { 
		//Numero di righe = numero di libri
		return data == null ? 0 : data.size(); 
	}

	@Override 
	public int getColumnCount() { 
		//Numero di colonne = lunghezza dell'array columns
		return columns.length; 
	}

	@Override 
	public String getColumnName(int col) {
		//Restituisce il nome della colonna
		return columns[col]; 
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		//Recupera il libro della riga richiesta
		Libro libro = data.get(rowIndex);
		//Restituisce il valore da mostrare nella cella
		switch (columnIndex) {
		case 0: return libro.getId();
		case 1: return libro.getTitolo();
		case 2: return libro.getAutore();
		case 3: return libro.getIsbn();
		case 4: return libro.getCategoria();
		case 5: return libro.getAnno();
		case 6: return libro.getCopieDisponibili();
		default: return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//ID non editabile
		return columnIndex != 0;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		//Controlli di sicurezza
		if (data == null || rowIndex < 0 || rowIndex >= data.size()) return;
		Libro libro = data.get(rowIndex);
		//Aggiorna il campo corretto del libro
		switch (columnIndex) {
		case 1: libro.setTitolo(String.valueOf(aValue)); break;
		case 2: libro.setAutore(String.valueOf(aValue)); break;
		case 3: libro.setIsbn(String.valueOf(aValue)); break;
		case 4: libro.setCategoria(String.valueOf(aValue)); break;
		case 5: libro.setAnno(parseIntSafe(aValue, libro.getAnno())); break;
		case 6: libro.setCopieDisponibili(parseIntSafe(aValue, libro.getCopieDisponibili())); break;
		}
		//Notifica la JTable che la cella è stata aggiornata
		fireTableCellUpdated(rowIndex, columnIndex);
	}	

	//Converte un valore in intero, con fallback in caso di errore
	private int parseIntSafe(Object v, int fallback) {
		try { return Integer.parseInt(String.valueOf(v).trim()); } catch (Exception e) { return fallback; }
	}

	//Restituisce il libro corrispondente alla riga selezionata
	public Libro getLibroAt(int row) { return data == null ? null : data.get(row); }
}
