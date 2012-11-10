package pl.jakd.TramSim;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * klasa reprezentująca przystanek w symulacji
 * 
 * @author jacek
 */
public class TramStop {
	private String name; // nazwa
	private Point2D.Double point; // pozycja na planszy
	private LinkedList<Passenger> passengers; // pasażerowie na przystanku
	private LinkedList<Tram> timtabl; // rozkład jazdy
	private StopWatcher watcher; // "zarządca" przystanku
	private LinkedList<Route> routes; // gdzie można dojechać z tego przystanku

	/**
	 * tworzy nową instancję przystanku
	 * 
	 * @param name
	 *            nazwa przystanku
	 * @param p
	 *            położenie na mapie
	 */
	public TramStop(String name, Point2D.Double p) {
		this.name = name;
		this.point = p;
		watcher = new StopWatcher(this);
		passengers = new LinkedList<Passenger>();
		timtabl = new LinkedList<Tram>();
	}

	/**
	 * ustala trasy przystanku, tj. trasy z danego przystanku do innych
	 * sąsiadujących
	 * 
	 * @param routes
	 */
	public void setRoutes(LinkedList<Route> routes) {
		this.routes = routes;
	}

	/**
	 * tramwaj ogłasza za pomocą tej metody, że właśnie podjechał na przystanek
	 * 
	 * @param t
	 *            tramwaj, który podjechał
	 */
	public void arrived(Tram t) {
		watcher.arrived(t);
	}

	/**
	 * tramwaj ogłasza odjazd
	 * 
	 * @param t
	 *            tramwaj, który ogłasza odjazd
	 */
	public void leave(Tram t) {
		watcher.leave(t);
	}

	/**
	 * zwraca trasy danego przystanku, czyli gdzie można z niego dojechać
	 * 
	 * @return trasy danego przystanku
	 */
	public LinkedList<Route> getRoutes() {
		return routes;
	}

	/**
	 * zwraca położenie przystanku
	 * 
	 * @return położenie przystanku
	 */
	public Point2D.Double getPoint() {
		return point;
	}

	/**
	 * ustala rozkład jazdy, czyli tramwaj, który przejeżdża przez dany
	 * przystanek
	 * 
	 * @param t
	 */
	public void setTimeTable(LinkedList<Tram> t) {
		this.timtabl = t;
	}

	/**
	 * dodaje tramwaj do rozkładu
	 * 
	 * @param t
	 *            tramwaj który dodajemy do przystanku
	 */
	public void addTram(Tram t) {
		timtabl.add(t);
	}

	/**
	 * zwraca zarządcę przystanku
	 * 
	 * @return zarządca przystanku
	 */
	public StopWatcher getWatcher() {
		return watcher;
	}

	/**
	 * dodaje pasażera do przystanku
	 * 
	 * @param p
	 *            nowy oczekujący pasażer
	 */
	public void addPassenger(Passenger p) {
		watcher.addPassenger(p);
		// passengers.add(p);
	}

	/**
	 * zwraca nazwę przystanku
	 * 
	 * @return nazwa przystanku
	 */
	public String getName() {
		return name;
	}

	/**
	 * zwraca rozkład jazdy przystanku
	 * 
	 * @return rozkład jazdy przystanku
	 */
	public LinkedList<Tram> getTimeTable() {
		return timtabl;
	}

	/**
	 * zwraca listę pasażerów oczekujących na przystanku
	 * 
	 * @return lista pasażerów oczekujących na przystanku
	 */
	public LinkedList<Passenger> getPassengers() {
		return passengers;
	}

	/**
	 * zwraca trasę prowadzącą z tego przystanku do przystanku docelowego.
	 * jeżeli przystanek dest nie jest sąsiadem tego przystanku, czyli nie
	 * istnieje bezpośrednia trasa do niego, wówczas zwraca null
	 * 
	 * @param dest
	 *            przystanek do którego chcemy dojechać
	 * @return trasa do przystanku dest z this, lub null, jeżeli nie znaleziono
	 */
	public Route getRoute(TramStop dest) {
		Route tmp = new Route(this, dest, null);
		for (Route r : routes) {
			if (tmp.equals(r))
				return r;
		}
		return null; // nie powinno wystąpić
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TramStop)
			if (((TramStop) o).name.equals(this.name))
				return true;
		if (o instanceof String)
			if (o.equals(name))
				return true;
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
