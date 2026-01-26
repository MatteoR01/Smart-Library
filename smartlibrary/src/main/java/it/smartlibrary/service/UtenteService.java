package it.smartlibrary.service;

import it.smartlibrary.dao.UtenteDAO;
import it.smartlibrary.dao.impl.DAOFactory;
import it.smartlibrary.model.Utente;

import java.sql.SQLException;
import java.util.List;

public class UtenteService {

	//Riferimento al DAO che gestisce l’accesso ai dati degli utenti
	private final UtenteDAO dao;

	public UtenteService() {
		try {
			//Ottiene l’implementazione concreta del DAO tramite la factory
			this.dao = DAOFactory.getUtenteDAO();
		} 
		catch (SQLException e) {
			//Se il DAO non può essere inizializzato l’applicazione non può proseguire
			throw new RuntimeException("Errore inizializzazione UtenteDAO", e);
		}
	}

	//Restituisce la lista completa degli utenti
	public List<Utente> findAll() throws Exception {
		return dao.findAll();
	}

	//Cerca un utente tramite email
	public Utente findByEmail(String email) throws Exception {
		return dao.findByEmail(email);
	}

	//Elimina un utente tramite ID
	public void deleteById(String id) throws Exception {
		dao.delete(id);
	}

	//Elimina un utente cercandolo prima tramite email
	public void deleteByEmail(String email) throws Exception {
		Utente utente = dao.findByEmail(email);
		//Se l’utente esiste viene eliminato tramite il suo ID
		if (utente != null) {
			dao.delete(utente.getId());
		}
	}
}
