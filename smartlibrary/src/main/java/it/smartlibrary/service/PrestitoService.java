package it.smartlibrary.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import it.smartlibrary.dao.PrestitoDAO;
import it.smartlibrary.dao.impl.DAOFactory;
import it.smartlibrary.dao.impl.PrestitoDAOJdbcImpl;
import it.smartlibrary.model.Libro;
import it.smartlibrary.model.Prestito;
import it.smartlibrary.model.Utente;
import it.smartlibrary.util.DBConnection;
import it.smartlibrary.util.LogService;

public class PrestitoService {

	//DAO che gestisce l’accesso ai dati dei prestiti
	private final PrestitoDAO prestitoDAO;

	public PrestitoService() throws SQLException {
		//Inizializza il DAO concreto tramite la sua implementazione JDBC
		this.prestitoDAO = new PrestitoDAOJdbcImpl();
	}

	public void prestaLibro(String idLibro, String idUtente, int giorni) throws Exception{
		//Ottiene la connessione condivisa per gestire la transazione
		Connection conn = DBConnection.getInstance().getConnection();
		try {
			//Avvia una transazione manuale
			conn.setAutoCommit(false);

			//Controlla disponibilità
			Libro libro = DAOFactory.getLibroDAO().findById(idLibro);
			if(libro == null) throw new IllegalAccessException("Libro non trovato");
			if(libro.getCopieDisponibili() <= 0) throw new IllegalStateException("Nessuna copa del libro disponibile");

			//Controlla se l'utente ha già un prestito attivo per questo libro
			List<Prestito> prestitiUtente = prestitoDAO.findByUser(idUtente);
			for (Prestito p : prestitiUtente) {
				boolean stessoLibro = p.getIdLibro().equals(idLibro);
				boolean attivo = p.getDataRestituzione() == null; // non restituito

				if (stessoLibro && attivo) {
					throw new IllegalStateException("L'utente ha già questo libro in prestito");
				}
			}

			//Crea prestito
			Prestito prestito = new Prestito();
			prestito.setIdPrestito(UUID.randomUUID().toString());
			prestito.setIdLibro(idLibro);
			prestito.setIdUtente(idUtente);
			prestito.setDataPrestito(Timestamp.from(Instant.now()));
			prestito.setDataScadenza(Timestamp.from(Instant.now().plusSeconds(60L*60*24*giorni)));
			prestito.setDataRestituzione(null);
			prestito.setStato("ATTIVO");
			//Salva il prestito nel DB
			prestitoDAO.save(prestito);

			//Aggiorna copie disponibili
			libro.setCopieDisponibili(libro.getCopieDisponibili()-1);
			DAOFactory.getLibroDAO().update(libro);
			//Conferma la transazione
			conn.commit();

			LogService.getLogger().info("Prestito registrato: " + prestito.getIdPrestito());
		}
		catch(Exception e) {
			//In caso di errore annulla tutte le operazioni
			conn.rollback();
			throw e;
		}
		finally {
			//Ripristina la modalità automatica
			conn.setAutoCommit(true);
		}
	}

	public void restituisciLibro(String idPrestito) throws Exception {
		//Ottiene la connessione per gestire la transazione
		Connection conn = DBConnection.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);		

			//Restituisci il presitto direttamente per id
			Prestito prestito = prestitoDAO.findById(idPrestito);
			if(prestito == null) throw new IllegalArgumentException("Prestito non trovato: " +idPrestito);
			if(prestito.getDataRestituzione() != null) throw new IllegalStateException("Prestito già restituito: "+idPrestito);

			prestito.setDataRestituzione(Timestamp.from(Instant.now())); 
			prestito.setStato("CHIUSO"); 
			prestitoDAO.update(prestito);

			//aggiorna copie disponibili
			Libro libro = DAOFactory.getLibroDAO().findById(prestito.getIdLibro());
			if(libro != null) {
				libro.setCopieDisponibili(libro.getCopieDisponibili()+1);
				DAOFactory.getLibroDAO().update(libro);
			}
			conn.commit();

			LogService.getLogger().info("Restituzione prestito: " + prestito.getIdPrestito());
		}
		catch(Exception e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
		}
	}

	public void estendiPrestito(String idPrestito, int giorniEstensione, Utente richiedente) throws Exception {

		//Solo admin può estendere
		if (!"ADMIN".equalsIgnoreCase(richiedente.getTipo())) {
			throw new Exception("Solo un amministratore può estendere un prestito");
		}

		Prestito p = prestitoDAO.findById(idPrestito);

		if (p == null) {
			throw new Exception("Prestito non trovato");
		}

		if (p.getDataRestituzione() != null) {
			throw new Exception("Il prestito è già stato restituito");
		}

		//Calcola nuova scadenza
		// Timestamp → LocalDateTime
		LocalDateTime scadenzaAttuale = p.getDataScadenza().toLocalDateTime();

		// Aggiungi giorni
		LocalDateTime nuovaScadenzaDT = scadenzaAttuale.plusDays(giorniEstensione);

		// LocalDateTime → Timestamp
		Timestamp nuovaScadenza = Timestamp.valueOf(nuovaScadenzaDT);

		// Aggiorna il prestito
		p.setDataScadenza(nuovaScadenza);
		prestitoDAO.update(p);


		prestitoDAO.update(p);
	}


	public List<Prestito> findByUser(String userId) throws Exception {
		//Restituisce i prestiti dell’utente, oppure una lista vuota se null
		List<Prestito> list = prestitoDAO.findByUser(userId);
		return list == null ? java.util.Collections.emptyList() : list;
	}
}










































