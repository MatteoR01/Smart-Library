package it.smartlibrary.ui;

import it.smartlibrary.service.StatisticheService;
import it.smartlibrary.util.ChartUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

/**
 * Pannello che mostra i grafici delle statistiche:
 * - Libri più prestati (bar chart)
 * - Prestiti per categoria (pie chart)
 * Permette anche l'esportazione come immagine PNG.
 */
public class StatistichePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage graficoLibri;
    private BufferedImage graficoCategorie;

    public StatistichePanel() {
        setLayout(new BorderLayout());

        JPanel center = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                //Posizione verticale iniziale
                int y = 10;

                //Disegna il grafico dei libri più prestati
                if (graficoLibri != null) {
                    g.drawImage(graficoLibri, 10, y, null);
                    y += graficoLibri.getHeight() + 20;
                }

                //Disegna il grafico dei prestiti per categoria
                if (graficoCategorie != null) {
                    g.drawImage(graficoCategorie, 10, y, null);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int h = 0;
                if (graficoLibri != null) h += graficoLibri.getHeight() + 20;
                if (graficoCategorie != null) h += graficoCategorie.getHeight() + 20;
                if (h == 0) h = 400;
                return new Dimension(850, h);
            }
        };

        JScrollPane scroll = new JScrollPane(center);
        add(scroll, BorderLayout.CENTER);

        JButton btnAggiorna = new JButton("Aggiorna grafici");
        JButton btnEsporta = new JButton("Esporta come immagine");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnAggiorna);
        bottom.add(btnEsporta);

        add(bottom, BorderLayout.SOUTH);

        btnAggiorna.addActionListener(e -> caricaStatistiche(center));
        btnEsporta.addActionListener(e -> esportaImmagine());

        //Caricamento iniziale
        caricaStatistiche(center);
    }

    //Carica i dati dal database tramite StatisticheService e genera i grafici usando ChartUtils.
    private void caricaStatistiche(JComponent repaintTarget) {
    	
        try {
            StatisticheService service = new StatisticheService();

            Map<String, Integer> libri = service.getLibriPiuPrestati();
            Map<String, Integer> categorie = service.getPrestitiPerCategoria();

            //Genera i grafici Java2D
            graficoLibri = ChartUtils.drawBarChart(libri, "Libri più prestati");
            graficoCategorie = ChartUtils.drawPieChart(categorie, "Prestiti per categoria");

            //Aggiorna la UI
            repaintTarget.revalidate();
            repaintTarget.repaint();

        } 
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore caricamento statistiche: " + ex.getMessage());
        }
    }

    //Esporta entrambi i grafici in un'unica immagine PNG
    private void esportaImmagine() {
    	
        if (graficoLibri == null && graficoCategorie == null) {
            JOptionPane.showMessageDialog(this, "Nessun grafico da esportare.");
            return;
        }

        try {
        	//Calcola dimensioni dell'immagine combinata
            int width = 900;
            int height = 0;

            if (graficoLibri != null) height += graficoLibri.getHeight() + 20;
            if (graficoCategorie != null) height += graficoCategorie.getHeight() + 20;
            if (height == 0) height = 400;
            
            //Crea immagine finale
            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = combined.createGraphics();

            int y = 10;
            if (graficoLibri != null) {
                g.drawImage(graficoLibri, 10, y, null);
                y += graficoLibri.getHeight() + 20;
            }
            if (graficoCategorie != null) {
                g.drawImage(graficoCategorie, 10, y, null);
            }
            g.dispose();

            //Salva su file
            File f = new File("statistiche_prestiti.png");
            ChartUtils.saveAsPng(combined, f);

            JOptionPane.showMessageDialog(this, "Immagine esportata: " + f.getAbsolutePath());

        } 
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore esportazione: " + ex.getMessage());
        }
    }
}
