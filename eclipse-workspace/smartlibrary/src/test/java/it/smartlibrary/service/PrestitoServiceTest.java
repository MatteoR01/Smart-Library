package it.smartlibrary.service;

import it.smartlibrary.dao.impl.LibroDAOJdbcImpl;
import it.smartlibrary.dao.impl.UtenteDAOJdbcImpl;
import it.smartlibrary.model.Libro;
import it.smartlibrary.model.Utente;
import it.smartlibrary.util.DatabaseInit;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrestitoServiceTest {
	@BeforeAll
	public void setup() throws Exception {
		System.setProperty("smartlibrary.testdb", "true");
	//	DBConnection.resetForTests();
		DatabaseInit.initIfNeeded();
	}

	@Test
	public void testPrestaERestituisci() throws Exception {
		// prepara utente e libro
		UtenteDAOJdbcImpl udao = new UtenteDAOJdbcImpl();
		LibroDAOJdbcImpl ldao = new LibroDAOJdbcImpl();

		Utente u = new Utente();
		u.setId("u1"); u.setNome("Test"); u.setCognome("User"); u.setEmail("u1@example.com"); u.setPasswordHash("pwd"); u.setTipo("USER");
		udao.save(u);

		Libro l = new Libro("b1","TestBook","Author","1111","Cat",2021,2,2,"d");
		ldao.save(l);

		PrestitoService ps = new PrestitoService();
		ps.prestaLibro("b1", "u1", 7);

		Libro after = ldao.findById("b1");
		assertEquals(1, after.getCopieDisponibili());

		// trova prestiti utente e restituisci
		List<?> list = ps.findByUser("u1");
		assertFalse(list.isEmpty());
		String idPrestito = ((it.smartlibrary.model.Prestito)list.get(0)).getIdPrestito();

		ps.restituisciLibro(idPrestito);

		Libro after2 = ldao.findById("b1");
		assertEquals(2, after2.getCopieDisponibili());

		// cleanup
		ps = null;
	}

	@Test
	public void testPrestitoDoppio() throws Exception { 

		// Inizializza DB con libri e utenti di default 
		DatabaseInit.initIfNeeded(); 
		PrestitoService service = new PrestitoService(); 

		// Primo prestito: deve funzionare 
		service.prestaLibro("L1", "user1", 30); 

		// Secondo prestito dello stesso libro allo stesso utente: deve fallire 
		Exception ex = assertThrows(Exception.class, () -> { 
			service.prestaLibro("L1", "user1", 30); 
		}); 
		assertTrue(ex.getMessage().contains("già") || ex.getMessage().contains("prestito")); 
	}
}
