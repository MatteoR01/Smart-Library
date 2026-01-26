package it.smartlibrary.model;

import java.sql.Timestamp;

public class Prestito {
	private String idPrestito;
	private String idLibro;
	private String idUtente;
	private Timestamp dataPrestito;
	private Timestamp dataScadenza;
	private Timestamp dataRestituzione;
	private String stato;
	
	public Prestito() {}

	public String getIdPrestito() {
		return idPrestito;
	}

	public void setIdPrestito(String idPrestito) {
		this.idPrestito = idPrestito;
	}

	public String getIdLibro() {
		return idLibro;
	}

	public void setIdLibro(String idLibro) {
		this.idLibro = idLibro;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public Timestamp getDataPrestito() {
		return dataPrestito;
	}

	public void setDataPrestito(Timestamp dataPrestito) {
		this.dataPrestito = dataPrestito;
	}

	public Timestamp getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Timestamp dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public Timestamp getDataRestituzione() {
		return dataRestituzione;
	}

	public void setDataRestituzione(Timestamp dataRestituzione) {
		this.dataRestituzione = dataRestituzione;
	}

	//Stato del prestito
	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}
}
