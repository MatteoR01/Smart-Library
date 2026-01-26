package it.smartlibrary.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.smartlibrary.dao.impl.DAOFactory;
import it.smartlibrary.model.Libro;
import it.smartlibrary.model.Prestito;


//Service dedicato al calcolo delle statistiche sui prestiti.
public class StatisticheService {

	//Libri più prestati: titolo -> numero prestiti
	public Map<String, Integer> getLibriPiuPrestati() throws Exception {
		Map<String, Integer> result = new HashMap<>();

		List<Prestito> prestiti = DAOFactory.getPrestitoDAO().findAll();

		for (Prestito p : prestiti) {
			Libro l = DAOFactory.getLibroDAO().findById(p.getIdLibro());
			if (l == null) continue;

			String titolo = l.getTitolo() != null ? l.getTitolo() : "Senza titolo";
			result.merge(titolo, 1, Integer::sum);
		}

		return result;
	}

	//Prestiti per categoria: categoria -> numero prestiti
	public Map<String, Integer> getPrestitiPerCategoria() throws Exception {
		Map<String, Integer> result = new HashMap<>();

		List<Prestito> prestiti = DAOFactory.getPrestitoDAO().findAll();

		for (Prestito p : prestiti) {
			Libro l = DAOFactory.getLibroDAO().findById(p.getIdLibro());
			if (l == null) continue;

			String categoria = l.getCategoria() != null ? l.getCategoria() : "Sconosciuta";
			result.merge(categoria, 1, Integer::sum);
		}

		return result;
	}
}
