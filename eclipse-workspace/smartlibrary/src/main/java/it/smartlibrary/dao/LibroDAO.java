package it.smartlibrary.dao;

import java.util.List;
import it.smartlibrary.model.Libro;

/* Interfaccia DAO per la gestione dei libri.
 * Definisce tutte le operazioni fondamentali per accedere e manipolare
 * i dati dei libri nel database. Le implementazioni (es. JDBC) si occupano
 * dei dettagli tecnici, mentre il resto dell'applicazione usa solo questa
 * interfaccia per rimanere indipendente dal tipo di database.
 */

public interface LibroDAO {
	
	//Inserisce un nuovo libro nel database
	void save(Libro libro) throws Exception;
	
	//Aggiorna i dati di un libro esistente
	void update(Libro libro) throws Exception;
	
	//Elimina un libro tramite il suo ID
	void delete(String id) throws Exception;
	
	//Cerca un libro tramite ID.
	Libro findById(String id) throws Exception;
	
	//Restituisce la lista completa dei libri.
	List<Libro> findAll() throws Exception;
}
