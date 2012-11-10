package pl.jakd.TramSim;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Klasa reprezentuje odcinek i skrzyżowania na trasie jeżeli odcinek będzie
 * bardzo którki, można potraktować go jak skrzyżowanie (dla ułatwienia)
 * 
 * @author jacek
 */

public class LinkingRoute {
	private Point2D.Double p1;
	private Point2D.Double p2;
	private double speed;
	private boolean isCrossing;

	/**
	 * tworzy odcinek trasy pomiędzy dwoma przystankami. reprezentuje również
	 * skrzyżowania.
	 * 
	 * @param p1
	 *            punkt początkowy
	 * @param p2
	 *            punkt końcowy
	 * @param speed
	 *            szybkość na danym odcinku
	 * @param isCrossing
	 *            czy jest skrzyżowaniem
	 */
	public LinkingRoute(Point2D.Double p1, Point2D.Double p2, double speed,
			boolean isCrossing) {
		this.p1 = p1;
		this.p2 = p2;
		this.speed = speed;
		this.isCrossing = isCrossing;
	}

	/**
	 * zwraca prędkość na danej trasie
	 * 
	 * @return prędkość na trasie
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * zwraca punkty początkowy i końcowy reprezentujący ten odcinek
	 * 
	 * @return tablica punktów
	 */
	public Point2D.Double[] getPoints() {
		return new Point2D.Double[] { p1, p2 };
	}

	/**
	 * sprawdza, czy podany odcinek jest skrzyżowaniem
	 * 
	 * @return true, jeżeli jest skrzyżowaniem, false w przeciwnym razie
	 */
	public boolean isCrossing() {
		return isCrossing;
	}

	/**
	 * TODO obsługa skrzyżowania
	 */
	public void manageCrossing() {
		if (isCrossing) {

		}
	}

	public Line2D.Double getLine() {
		return new Line2D.Double(p1, p2);
	}

	/**
	 * sprawdza czy dana trasa zawiera podany punkt
	 * 
	 * @param p
	 *            punkt którego obecność prawdzamy
	 * @return true- jeżeli punkt jest, false- jeżeli punktu nie ma
	 */
	public boolean containsPoint(Point2D.Double p) {
		if (p.equals(p1) || p.equals(p2))
			return true;
		return false;
	}

	/**
	 * są jednakowe jeżeli prowadzą z t1 do t2. zakładamy że istnieje tylko
	 * jedna bezpośrednia trasa z A do B
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof LinkingRoute) {
			if (((LinkingRoute) o).p1.equals(this.p1)
					&& ((LinkingRoute) o).p2.equals(this.p2)) {
				// System.out.println(p1 + " " + p2 + " " + o + " OK");
				return true;
			} else if (((LinkingRoute) o).p1.equals(this.p2)
					&& ((LinkingRoute) o).p2.equals(this.p1)) {
				// System.out.println(p1 + " " + p2 + " " + o + " OK");
				return true;
			}
		}
		// System.out.println(p1 + " " + p2 + " " + o + " :(");
		return false;
	}

	@Override
	public String toString() {
		return "" + p1 + " " + p2;
	}

}
