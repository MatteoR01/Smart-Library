package it.smartlibrary.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseInit {

	/*	Inizializza il database solo se necessario:
     * 	crea le tabelle se non esistono
     * 	inserisce dati di esempio se le tabelle sono vuote
     */
	public static void initIfNeeded() {
		try {
			//Ottiene la connessione unica dell’applicazione
			Connection conn = DBConnection.getInstance().getConnection();

			//Creazionje tabelle se non esistono
			try (Statement st = conn.createStatement()) {
				//Tabella Libro
				st.execute("CREATE TABLE IF NOT EXISTS Libro ("
						+ "id VARCHAR(50) PRIMARY KEY,"
						+ "titolo VARCHAR(255),"
						+ "autore VARCHAR(255),"
						+ "isbn VARCHAR(50),"
						+ "categoria VARCHAR(100),"
						+ "anno INT,"
						+ "copieTotali INT,"
						+ "copieDisponibili INT,"
						+ "descrizione CLOB"
						+ ")");
				//Tabella Utente
				st.execute("CREATE TABLE IF NOT EXISTS Utente ("
						+ "id VARCHAR(50) PRIMARY KEY,"
						+ "nome VARCHAR(100),"
						+ "cognome VARCHAR(100),"
						+ "email VARCHAR(150),"
						+ "telefono VARCHAR(50),"
						+ "tipo VARCHAR(20),"
						+ "passwordHash VARCHAR(255),"
						+ "dataIscrizione TIMESTAMP"
						+ ")");
				//Tabella Prestito
				st.execute("CREATE TABLE IF NOT EXISTS Prestito ("
						+ "idPrestito VARCHAR(50) PRIMARY KEY,"
						+ "idLibro VARCHAR(50),"
						+ "idUtente VARCHAR(50),"
						+ "dataPrestito TIMESTAMP,"
						+ "dataScadenza TIMESTAMP,"
						+ "dataRestituzione TIMESTAMP,"
						+ "stato VARCHAR(20)"
						+ ")");
			}

			//Popola libri se vuoto
			try (Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Libro")) {
				if (rs.next() && rs.getInt(1) == 0) {
					String insertSql = "INSERT INTO Libro(id,titolo,autore,isbn,categoria,anno,copieTotali,copieDisponibili,descrizione) VALUES(?,?,?,?,?,?,?,?,?)";
					try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
						//Libro 1
						ps.setString(1, "L1");
						ps.setString(2, "Il Nome della Rosa");
						ps.setString(3, "Umberto Eco");
						ps.setString(4, "9788806170000");
						ps.setString(5, "Narrativa");
						ps.setInt(6, 1980);
						ps.setInt(7, 3);
						ps.setInt(8, 3);
						ps.setString(9, "Classico");
						ps.executeUpdate();
						//Libro 2	
						ps.setString(1, "L2");
						ps.setString(2, "Java Programming");
						ps.setString(3, "Some Author");
						ps.setString(4, "9781234567890");
						ps.setString(5, "Tecnico");
						ps.setInt(6, 2020);
						ps.setInt(7, 2);
						ps.setInt(8, 2);
						ps.setString(9, "Manuale Java");
						ps.executeUpdate();
					}
				}
			}

			//Popola utente admin e un utente standard se la tabella Utente è vuota
			try (Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Utente")) {
				if (rs.next() && rs.getInt(1) == 0) {
					String insertUser = "INSERT INTO Utente(id,nome,cognome,email,telefono,tipo,passwordHash,dataIscrizione) VALUES(?,?,?,?,?,?,?,CURRENT_TIMESTAMP())";
					try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
						//Admin
						ps.setString(1, "admin");
						ps.setString(2, "Admin");
						ps.setString(3, "Admin");
						ps.setString(4, "admin@example.it");
						ps.setString(5, "0000000000");
						ps.setString(6, "ADMIN");
						String adminHash = BCrypt.hashpw("changeme", BCrypt.gensalt());
						ps.setString(7, adminHash);
						ps.executeUpdate();

						//Utente standard di default
						ps.setString(1, "user1");
						ps.setString(2, "Mario");
						ps.setString(3, "Rossi");
						ps.setString(4, "user@example.it");
						ps.setString(5, "333111222");
						ps.setString(6, "USER");
						String userHash = BCrypt.hashpw("userpass", BCrypt.gensalt());
						ps.setString(7, userHash);
						ps.executeUpdate();
					}
				}
			}

		} catch (Exception e) {
			// Inizializzazione fallita, stampa stack trace ma non blocca l’app
			e.printStackTrace();
		}
	}
}
