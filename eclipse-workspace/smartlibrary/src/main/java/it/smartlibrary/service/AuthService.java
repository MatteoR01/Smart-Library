package it.smartlibrary.service;

import it.smartlibrary.dao.UtenteDAO;
import it.smartlibrary.dao.impl.DAOFactory;
import it.smartlibrary.model.Utente;
import it.smartlibrary.util.LogService;

import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
	
	// DAO per accedere ai dati degli utenti
	private final UtenteDAO utenteDAO;
	
	// Utente attualmente autenticato nell'applicazione
	private static Utente utenteCorrente;

	public AuthService() throws Exception {
		// Inizializza il DAO concreto per gli utenti
		this.utenteDAO = DAOFactory.getUtenteDAO();
	}

	public Utente login(String email, String password) throws Exception {
		
		//Cerca l’utente tramite email
		Utente utente = utenteDAO.findByEmail(email);
		
		LogService.getLogger().info("Tentativo login: " + email);

		if (utente == null) {
		    LogService.getLogger().warning("Login fallito: utente non trovato (" + email + ")");
		    return null;
		}

		if (!BCrypt.checkpw(password, utente.getPasswordHash())) {
		    LogService.getLogger().warning("Login fallito: password errata (" + email + ")");
		    return null;
		}

		LogService.getLogger().info("Login riuscito: " + utente.getId());

		//Verifica la password confrontando l’hash salvato con quella inserita
		if (BCrypt.checkpw(password, utente.getPasswordHash())) {
			
			//Login riuscito, salva l’utente come corrente
			utenteCorrente = utente;
			return utente;
		}
		// Password errata -> login fallito
		return null;
	}

	//Termina la sessione dell’utente corrente
	public void logout() { utenteCorrente = null; }

	//Restituisce l’utente attualmente autenticato
	public Utente getCurrentUser() { return utenteCorrente; }
}
