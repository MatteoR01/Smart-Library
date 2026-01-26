package it.smartlibrary.dao.impl;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.smartlibrary.dao.LibroDAO;
import it.smartlibrary.model.Libro;
import it.smartlibrary.util.DBConnection;
import it.smartlibrary.util.LogService;

public class LibroDAOJdbcImpl implements LibroDAO {

	//Connessione al database ottenuta tramite la classe centralizzata DBConnection
	private Connection conn;

	public LibroDAOJdbcImpl() throws SQLException {
		//Recupera la connessione condivisa al DB
		conn = DBConnection.getInstance().getConnection();
	}

	@Override
	public List <Libro> findAll() throws SQLException {
		//Restituisce tutti i libri presenti nella tabella Libro
		List<Libro> list = new ArrayList<>();
		String sql = "SELECT * FROM Libro";
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			//Scorre tutte le righe del ResultSet e crea oggetti Libro
			while(rs.next()) {
				Libro libro = new Libro(
						rs.getString("id"),
						rs.getString("titolo"),
						rs.getString("autore"),
						rs.getString("isbn"),
						rs.getString("categoria"),
						rs.getInt("anno"),
						rs.getInt("copieTotali"),
						rs.getInt("copieDisponibili"),
						rs.getString("descrizione")
						);
				list.add(libro);
			}
		}
		return list;
	}

	@Override public Libro findById(String id) throws SQLException {
		//Cerca un libro tramite ID
		String sql = "SELECT * FROM Libro WHERE id = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			try(ResultSet rs = ps.executeQuery()) {
				//Se il libro esiste, costruisce e restituisce l'oggetto Libro
				if(rs.next()) {
					return new Libro(
							rs.getString("id"),
							rs.getString("titolo"),
							rs.getString("autore"),
							rs.getString("isbn"),
							rs.getString("categoria"),
							rs.getInt("anno"),
							rs.getInt("copieTotali"),
							rs.getInt("copieDisponibili"),
							rs.getString("descrizione")
							);
				}
				else {
					//Se non trovato, restituisce null
					return null;
				}
			}
		}
	}

	@Override
	public void save(Libro libro) throws SQLException {
		//Inserisce un nuovo libro nella tabella Libro
		String sql = "INSERT INTO Libro(id, titolo, autore, isbn, categoria, anno, copieTotali," +
				"copieDisponibili, descrizione) VALUES(?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			//Mappa i campi dell'oggetto Libro nei parametri SQL
			ps.setString(1, libro.getId());
			ps.setString(2, libro.getTitolo());
			ps.setString(3, libro.getAutore());
			ps.setString(4, libro.getIsbn());
			ps.setString(5, libro.getCategoria());
			ps.setInt(6, libro.getAnno());
			ps.setInt(7, libro.getCopieTotali());
			ps.setInt(8, libro.getCopieDisponibili());
			ps.setString(9, libro.getDescrizione());
			//Esegue l'inserimento
			ps.executeUpdate();

			LogService.getLogger().info("Creato libro: " + libro.getId() + " - " + libro.getTitolo());
		}
	}

	@Override public void update(Libro libro) throws SQLException {
		//Aggiorna i dati di un libro esistente
		String sql = "UPDATE Libro SET titolo = ?, autore = ?, isbn = ?, categoria = ?, anno = ?, copieTotali = ?, copieDisponibili = ?, descrizione = ? WHERE id = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, libro.getTitolo());
			ps.setString(2, libro.getAutore());
			ps.setString(3, libro.getIsbn());
			ps.setString(4, libro.getCategoria());
			ps.setInt(5, libro.getAnno());
			ps.setInt(6, libro.getCopieTotali());
			ps.setInt(7, libro.getCopieDisponibili());
			ps.setString(8, libro.getDescrizione());
			ps.setString(9, libro.getId());
			ps.executeUpdate();

			LogService.getLogger().info("Aggiornato libro: " + libro.getId());
		}
	}

	@Override public void delete(String id) throws SQLException {
		//Elimina un libro tramite il suo ID
		String sql = "DELETE FROM Libro WHERE id = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			ps.executeUpdate();

			LogService.getLogger().warning("Eliminato libro: " + id);
		}
	}

}
