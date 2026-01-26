package it.smartlibrary.dao;

import java.sql.SQLException;
import java.util.List;

import it.smartlibrary.model.Utente;

/* Interfaccia DAO per la gestione degli utenti. 
 * Definisce tutte le operazioni CRUD (Create, Read, Update, Delete)
 * che qualunque implementazione JDBC o di altro tipo deve fornire.
 * Il Service (UtenteService) utilizza questa interfaccia per accedere
 * ai dati senza conoscere i dettagli del database.
 */

public interface UtenteDAO {

	//Restituisce la lista completa degli utenti. Usato nella GUI per popolare le tabelle
	List<Utente> findAll() throws SQLException;
	//Restituisce un utente dato il suo ID
	Utente findById(String id) throws SQLException;
	
	//Restituisce un utente cercandolo tramite email
	Utente findByEmail(String email) throws SQLException;
	
	//Salva un nuovo utente nel database
	void save(Utente utente) throws SQLException;
	
	//Aggiorna i dati di un utente esistente
	void update(Utente utente) throws SQLException;
	
	//Elimina un utente dal database tramite ID
	void delete(String id) throws SQLException;
}
