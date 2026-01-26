package it.smartlibrary.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import it.smartlibrary.test.TestBase;
import it.smartlibrary.util.DatabaseInit;

public class AuthServiceTest extends TestBase {

	@Test
	public void testLoginPasswordErrata() throws Exception {
		DatabaseInit.initIfNeeded();
		AuthService auth = new AuthService();

		assertNull(auth.login("admin@example.it", "sbagliata"));
	}
	
	@Test
	public void testLoginCorretto() throws Exception {
		DatabaseInit.initIfNeeded();
		AuthService auth = new AuthService();

		assertNotNull(auth.login("admin@example.it", "changeme"));
	}

}
