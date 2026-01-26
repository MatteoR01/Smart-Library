package it.smartlibrary.dao.impl;

import java.sql.SQLException;

import it.smartlibrary.dao.LibroDAO;
import it.smartlibrary.dao.PrestitoDAO;
import it.smartlibrary.dao.UtenteDAO;

public class DAOFactory {
	
	/* Restituisce l'implementazione JDBC del DAO dei libri.
     * La factory centralizza la creazione dei DAO, così il resto
     * dell'applicazione non deve conoscere le classi concrete.
     */
	public static LibroDAO getLibroDAO() throws SQLException { 
		return new LibroDAOJdbcImpl(); 
	} 
	
	/* Restituisce l'implementazione JDBC del DAO degli utenti.
     * Usato da AuthService, UtenteService e dalla GUI.
     */	
	public static UtenteDAO getUtenteDAO() throws SQLException {
	    return new UtenteDAOJdbcImpl();
	}
	
	/* Restituisce l'implementazione JDBC del DAO dei prestiti.
     * Usato da PrestitoService per gestire prestiti, restituzioni
     * e controlli di disponibilità.
     */
	public static PrestitoDAO getPrestitoDAO() throws SQLException { 
		return new PrestitoDAOJdbcImpl(); 
	}
}
