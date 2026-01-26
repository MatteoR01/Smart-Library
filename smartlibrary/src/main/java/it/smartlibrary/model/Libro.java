package it.smartlibrary.model;

public class Libro {
	private String id;
	private String titolo;
	private String autore;
	private String isbn;
	private String categoria;
	private int anno;
	private int copieTotali;
	private int copieDisponibili;
	private String descrizione;
	
	public Libro() {}
	
	public Libro(String id, String titolo, String autore, String isbn, String categoria, int anno, int copieTotali, int copieDisponibili, String descrizione) {
		this.id = id;
		this.titolo = titolo;
		this.autore = autore;
		this.isbn = isbn;
		this.categoria = categoria;
		this.anno = anno;
		this.copieTotali = copieTotali;
		this.copieDisponibili = copieDisponibili;
		this.descrizione = descrizione;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getAutore() {
		return autore;
	}

	public void setAutore(String autore) {
		this.autore = autore;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public int getAnno() {
		return anno;
	}

	public void setAnno(int anno) {
		this.anno = anno;
	}

	public int getCopieTotali() {
		return copieTotali;
	}

	public void setCopieTotali(int copieTotali) {
		this.copieTotali = copieTotali;
	}

	public int getCopieDisponibili() {
		return copieDisponibili;
	}

	public void setCopieDisponibili(int copieDisponibili) {
		this.copieDisponibili = copieDisponibili;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	//Ritorna true se almeno una copia è disponibile al prestito
	public boolean isAvailable() {
		return copieDisponibili > 0;
	}
	
	//Decrementa il numero di copie disponibili
	public void decrement() {
		if(copieDisponibili > 0)
			copieDisponibili--;
	}
	
	//Incrementa il numero di copie disponibili (usato quando un libro viene restituito)
	public void increment() {
		copieDisponibili++;
	}
}
