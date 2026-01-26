package it.smartlibrary.dao;

import java.sql.SQLException;
import java.util.List;

import it.smartlibrary.model.Prestito;

/* Interfaccia DAO dedicata alla gestione dei prestiti.
 * Definisce tutte le operazioni di accesso ai dati relative ai prestiti
 * (inserimento, aggiornamento, cancellazione e ricerca).
 * Il Service (PrestitoService) utilizza questa interfaccia per applicare
 * la logica di business senza conoscere i dettagli del database.
 */


public interface PrestitoDAO {

	//Salva un nuovo prestito nel database
	void save(Prestito prestito) throws SQLException;
	
	//Aggiorna un prestito esistente
	void update(Prestito prestito) throws SQLException;
	
	//Elimina un prestito tramite ID
	void delete(String id) throws SQLException;

	//Restituisce tutti i prestiti associati a un determinato utente
	List<Prestito> findByUser(String userId) throws SQLException;
	
	//Restituisce i prestiti attivi per un determinato libro.
	List<Prestito> findActiveByBook(String libroId) throws SQLException;
	
	//Cerca un prestito tramite il suo ID.
	Prestito findById(String idPrestito) throws SQLException;
	
	//Restituisce tutti i prestiti presenti nel database
	List<Prestito> findAll() throws SQLException;

}
