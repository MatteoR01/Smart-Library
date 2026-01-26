package it.smartlibrary.dao;

import it.smartlibrary.dao.impl.LibroDAOJdbcImpl;
import it.smartlibrary.model.Libro;
import it.smartlibrary.util.DatabaseInit;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LibroDAOTest {
	@BeforeAll
	public void setup() throws Exception {
		System.setProperty("smartlibrary.testdb", "true");
		//DBConnection.resetForTests();
		DatabaseInit.initIfNeeded();
	}

	@AfterAll
	public void teardown() throws Exception {
		// nothing: in-memory DB will be discarded when JVM ends
	}

	@Test
	public void testFindAllAndSave() throws Exception {
		LibroDAOJdbcImpl dao = new LibroDAOJdbcImpl();
		List<Libro> list = dao.findAll();
		assertNotNull(list);
		int before = list.size();

		Libro l = new Libro("T1","Titolo Test","Autore Test","0000","Test",2023,1,1,"desc");
		dao.save(l);

		List<Libro> after = dao.findAll();
		assertEquals(before + 1, after.size());
		// cleanup
		dao.delete("T1");
	}
}
