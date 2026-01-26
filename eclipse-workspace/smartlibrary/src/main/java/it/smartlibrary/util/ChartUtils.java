package it.smartlibrary.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import javax.imageio.ImageIO;

//Metodi di utilità per disegnare grafici con Java2D e salvarli come immagini PNG. 
public class ChartUtils {

	//Disegna un semplice bar chart (titolo -> valore)
	public static BufferedImage drawBarChart(Map<String, Integer> data, String title) {
		int width = 800;
		int height = 400;

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 18));
		g.drawString(title, 20, 30);

		if (data == null || data.isEmpty()) {
			g.drawString("Nessun dato disponibile", 20, 80);
			g.dispose();
			return img;
		}

		int max = data.values().stream().max(Integer::compareTo).orElse(1);
		int n = data.size();
		int barWidth = Math.max(40, (width - 100) / n);

		int x = 50;
		int baseY = height - 60;

		for (Map.Entry<String, Integer> e : data.entrySet()) {
			int value = e.getValue();
			int barHeight = (int) ((value / (double) max) * 250);

			g.setColor(new Color(70, 130, 180));
			g.fillRect(x, baseY - barHeight, barWidth - 10, barHeight);

			g.setColor(Color.BLACK);
			g.drawRect(x, baseY - barHeight, barWidth - 10, barHeight);
			g.drawString(String.valueOf(value), x, baseY - barHeight - 5);

			String label = e.getKey();
			if (label.length() > 10) label = label.substring(0, 10) + "...";
			g.drawString(label, x, baseY + 15);

			x += barWidth;
		}

		g.dispose();
		return img;
	}

	//Disegna un semplice pie chart
	public static BufferedImage drawPieChart(Map<String, Integer> data, String title) {
		int width = 800;
		int height = 400;

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 18));
		g.drawString(title, 20, 30);

		if (data == null || data.isEmpty()) {
			g.drawString("Nessun dato disponibile", 20, 80);
			g.dispose();
			return img;
		}

		int total = data.values().stream().mapToInt(Integer::intValue).sum();
		int startAngle = 0;

		Color[] colors = {
				Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
				Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW
		};

		int i = 0;
		int cx = 50;
		int cy = 70;
		int size = 250;

		for (Map.Entry<String, Integer> e : data.entrySet()) {
			int value = e.getValue();
			int angle = (int) Math.round(360.0 * value / total);

			g.setColor(colors[i % colors.length]);
			g.fillArc(cx, cy, size, size, startAngle, angle);

			g.setColor(Color.BLACK);
			String label = e.getKey() + " (" + value + ")";
			g.drawString(label, 330, 80 + i * 20);

			startAngle += angle;
			i++;
		}

		g.dispose();
		return img;
	}

	//Salva un BufferedImage come PNG
	public static void saveAsPng(BufferedImage img, File file) throws Exception {
		ImageIO.write(img, "png", file);
	}
}
