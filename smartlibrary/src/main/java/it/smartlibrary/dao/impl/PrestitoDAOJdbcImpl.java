package it.smartlibrary.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.smartlibrary.dao.PrestitoDAO;
import it.smartlibrary.model.Prestito;
import it.smartlibrary.util.DBConnection;

public class PrestitoDAOJdbcImpl implements PrestitoDAO{

	private final Connection conn;

	public PrestitoDAOJdbcImpl() throws SQLException {
		try {	
			//Ottiene la connessione al database tramite la classe centralizzata DBConnection
			this.conn = DBConnection.getInstance().getConnection();
		}
		catch(Exception e) {
			//Se qualcosa va storto nella connessione, solleva un errore
			throw new RuntimeException("Impossibile connettersi al DB", e);
		}
	}

	@Override
	public void save(Prestito prestito) throws SQLException {
		//Inserisce un nuovo prestito nel database
		String sql = "INSERT INTO Prestito(idPrestito,idLibro,idUtente,dataPrestito,dataScadenza,dataRestituzione,stato) VALUES(?,?,?,?,?,?,?)";
		try(PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, prestito.getIdPrestito());
			ps.setString(2, prestito.getIdLibro());
			ps.setString(3, prestito.getIdUtente());
			ps.setTimestamp(4, prestito.getDataPrestito());
			ps.setTimestamp(5, prestito.getDataScadenza());
			ps.setTimestamp(6, prestito.getDataRestituzione());
			ps.setString(7, prestito.getStato());
			ps.executeUpdate();	
		}
	}

	@Override
	public void update(Prestito prestito) throws SQLException {
		String sql = "UPDATE Prestito SET dataPrestito=?, dataScadenza=?, dataRestituzione=?, stato=? WHERE idPrestito=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			//dataPrestito
			ps.setTimestamp(1, prestito.getDataPrestito());

			//dataScadenza
			ps.setTimestamp(2, prestito.getDataScadenza());

			//dataRestituzione
			ps.setTimestamp(3, prestito.getDataRestituzione());

			//stato
			ps.setString(4, prestito.getStato());

			//id prestito
			ps.setString(5, prestito.getIdPrestito());

			ps.executeUpdate();
		}
	}


	@Override
	public void delete(String id) throws SQLException {
		//Elimina un prestito tramite il suo ID
		String sql = "DELETE FROM Prestito WHERE idPrestito = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql))	{
			ps.setString(1, id);
			ps.executeUpdate();
		}
	}

	@Override
	public List<Prestito> findByUser(String userId) throws SQLException {
		//Restituisce tutti i prestiti associati a un determinato utente
		String sql = "SELECT * FROM Prestito WHERE idUtente = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, userId);
			try(ResultSet rs = ps.executeQuery()) {
				List<Prestito> list = new ArrayList<>();
				while(rs.next()) {
					list.add(mapRow(rs));
				}
				return list;
			}
		}
	}

	@Override 
	public Prestito findById(String id) throws SQLException {
	    String sql = "SELECT * FROM Prestito WHERE idPrestito=?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, id);
	        ResultSet rs = ps.executeQuery();
	        return rs.next() ? mapRow(rs) : null;
	    }
	}


	@Override
	public List<Prestito> findActiveByBook(String libroId) throws SQLException {
		//Restituisce tutti i prestiti ATTIVI relativi a un determinato libro.
		String sql = "SELECT * FROM Prestito WHERE idLibro = ? AND dataRestituzione IS NULL";		//-->// Un prestito è considerato attivo quando dataRestituzione è NULL.
		try(PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, libroId);
			try(ResultSet rs = ps.executeQuery()) {
				List<Prestito> list = new ArrayList<>();
				//Converte ogni riga del ResultSet in un oggetto Prestito
				while(rs.next()) {
					list.add(mapRow(rs));
				}
				return list;
			}
		}
	}

	@Override
	public List<Prestito> findAll() throws SQLException {
		String sql = "SELECT * FROM Prestito";
		List<Prestito> list = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapRow(rs));
			}
		}

		return list;
	}

	//Metodo che costruisce un oggetto Prestito a partire da una riga del ResultSet
	private Prestito mapRow(ResultSet rs) throws SQLException{
		Prestito prestito = new Prestito();
		prestito.setIdPrestito(rs.getString("idPrestito"));
		prestito.setIdLibro(rs.getString("idLibro"));
		prestito.setIdUtente(rs.getString("idUtente"));
		prestito.setDataPrestito(rs.getTimestamp("dataPrestito"));
		prestito.setDataScadenza(rs.getTimestamp("dataScadenza"));
		prestito.setDataRestituzione(rs.getTimestamp("dataRestituzione"));
		prestito.setStato(rs.getString("stato"));
		return prestito;
	}
}
