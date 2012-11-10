/**
 * ficzery:
 * 1. oczekiwanie tramwaju na przystanku uzależnione od ilości in/out pasażerów
 * 2. prędkość tramwaju zależna od trasy
 * 3. kolejka tramwajów na przystanku
 * 4. pojemność tramwajów (zależna od tramwaju)
 * 5. pasażerowie wsiadają tylko jeżeli tramwaj jedzie na ich przystanek (ale w dobrą stronę a nie w ogóle jedzie)
 * 6. trasa pomiędzy przystankami może się składać z kilku odcinków
 * 
 * 7. TODO rozkład czasowy. czyli czekają na wyjazd jeżeli mają czas, jeżeli opóźnienie, jadą natychmiast
 * 8. przesiadki (max 2)
 * 9. TODO generacja pasażerów w zależności od pory dnia itp.
 * 10. framework ułatwiający dodawanie linii, tras i przystanków
 * ---
 * 11. TODO prędkość tramwaju zależna od dnia / pory dnia
 * 12. TODO skrzyżowania //częściowo już zrobione.
 * --- 
 * 13. TODO bardziej realistyczne zachowanie tramwaju na trasie (przyspieszanie, zwalnianie, może jakiś odstęp dwóch)
 * 14. TODO awarie / zmiany linii
 * 15. licznik symulacji
 * 16. jednostki symulacji
 **/

package pl.jakd.TramSim;

import java.util.Calendar;
import java.util.Date;

import javax.swing.*;

/**
 * Symulacja krakowskiej sieci tramwajowej TODO jakiś fajny opis
 * 
 * @author jacek
 */

@SuppressWarnings("serial")
public class TramSim extends JFrame {
	/**
	 * licznik symulacji
	 */
	public static long iter = 0; // licznik symulacji
	/**
	 * kalendarz, z którego pobieramy i ustalamy datę w trakcie symulacji
	 */
	public static Calendar c = Calendar.getInstance(); // kalendarz
	private City city; // główny obiekt

	/**
	 * tworzy obiekt nowego miasta i uruchamia symulację
	 */
	public TramSim() {
		city = new City();

		c.setTime(new Date());

		// inicjalizacja okna
		this.setTitle("TramSim");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIgnoreRepaint(true);
		// inicjalizacja planszy
		this.add(city);

		this.pack();
		this.setVisible(true);

		city.createBufferStrategy(2);

		city.start();
	}

	/**
	 * wejście do programu
	 * 
	 * @param args
	 *            ignorowany
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TramSim();
			}
		});
	}
}
