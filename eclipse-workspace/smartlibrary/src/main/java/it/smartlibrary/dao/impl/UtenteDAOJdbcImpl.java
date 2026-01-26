package it.smartlibrary.dao.impl;

import it.smartlibrary.dao.UtenteDAO;
import it.smartlibrary.model.Utente;
import it.smartlibrary.util.DBConnection;
import it.smartlibrary.util.LogService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAOJdbcImpl implements UtenteDAO {

	private final Connection conn;

	public UtenteDAOJdbcImpl() throws SQLException {
		this.conn = DBConnection.getInstance().getConnection();
	}

	@Override
	public List<Utente> findAll() throws SQLException {
		List<Utente> list = new ArrayList<>();
		String sql = "SELECT * FROM Utente";

		try (Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				list.add(mapRow(rs));
			}
		}
		return list;
	}

	@Override
	public Utente findById(String id) throws SQLException {
		String sql = "SELECT * FROM Utente WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapRow(rs) : null;
			}
		}
	}

	@Override
	public Utente findByEmail(String email) throws SQLException {
		String sql = "SELECT * FROM Utente WHERE email = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email.toLowerCase());

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapRow(rs) : null;
			}
		}
	}

	@Override
	public void save(Utente u) throws SQLException {
		String sql = """
				    INSERT INTO Utente(id, nome, cognome, email, telefono, tipo, passwordHash, dataIscrizione)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, u.getId());
			ps.setString(2, u.getNome());
			ps.setString(3, u.getCognome());
			ps.setString(4, u.getEmail());
			ps.setString(5, u.getTelefono());
			ps.setString(6, u.getTipo());
			ps.setString(7, u.getPasswordHash());
			ps.setDate(8, Date.valueOf(u.getDataIscrizione()));

			ps.executeUpdate();
			LogService.getLogger().info("Creato utente: " + u.getEmail());
		}
	}

	@Override
	public void update(Utente u) throws SQLException {
		String sql = "UPDATE Utente SET nome=?, cognome=?, email=?, telefono=?, tipo=?, passwordHash=? WHERE id=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, u.getNome());
			ps.setString(2, u.getCognome());
			ps.setString(3, u.getEmail());
			ps.setString(4, u.getTelefono());
			ps.setString(5, u.getTipo());
			ps.setString(6, u.getPasswordHash());
			ps.setString(7, u.getId());

			ps.executeUpdate();
			LogService.getLogger().info("Aggiornato utente: " + u.getEmail());
		}
	}

	@Override
	public void delete(String id) throws SQLException {
		String sql = "DELETE FROM Utente WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			ps.executeUpdate();

			LogService.getLogger().warning("Eliminato utente: " + id);
		}
	}

	private Utente mapRow(ResultSet rs) throws SQLException {
		Utente u = new Utente();

		u.setId(rs.getString("id"));
		u.setNome(rs.getString("nome"));
		u.setCognome(rs.getString("cognome"));
		u.setEmail(rs.getString("email"));
		u.setTelefono(rs.getString("telefono"));
		u.setTipo(rs.getString("tipo"));
		u.setPasswordHash(rs.getString("passwordHash"));

		Date d = rs.getDate("dataIscrizione");
		u.setDataIscrizione(d != null ? d.toLocalDate() : LocalDate.now());

		return u;
	}
}
