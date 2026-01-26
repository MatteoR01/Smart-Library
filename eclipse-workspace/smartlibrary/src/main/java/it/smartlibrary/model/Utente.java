package it.smartlibrary.model;

import java.time.LocalDate;

public class Utente {
	private String id;
	private String nome;
	private String cognome;
	private String email;
	private String telefono;
	private String tipo;
	private String passwordHash;
	private LocalDate dataIscrizione;

	
	public Utente() {
	}

	public Utente(String id, String nome, String cognome, String email, String telefono, String tipo, String passwordHash, LocalDate dataIscrizione) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.email = email;
		this.telefono = telefono;
		this.tipo = tipo;
		this.passwordHash = passwordHash;
		this.dataIscrizione = dataIscrizione;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
	    this.email = (email == null ? null : email.trim().toLowerCase());
	}


	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	//Tipo di utente ("ADMIN" o "USER")
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public LocalDate getDataIscrizione() {
		return dataIscrizione;
	}

	public void setDataIscrizione(LocalDate dataIscrizione) {
		this.dataIscrizione = dataIscrizione;
	}
}
