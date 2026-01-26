package it.smartlibrary.test;

import org.junit.jupiter.api.BeforeEach;

public abstract class TestBase {

    @BeforeEach
    public void resetDatabase() throws Exception {
        // Usa il DB in-memory per i test
        System.setProperty("smartlibrary.testdb", "true");

        // Reset della connessione
     //   DBConnection.resetForTests();
    }
}
