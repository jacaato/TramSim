package pl.jakd.TramSim;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * klasa reprezentująca tramwaj w symulacji posiadamy dwie różne trasy, po
 * których się poruszamy pierwszą jest cała trasa, czyli przystanki, drugą
 * natomiast trasa pomiędzy konkretnymi przystankami.
 * 
 * @author jacek
 * 
 */
public class Tram {
	private LinkedList<TramStop> stahps; // przystanki
	private Point2D.Double position; // aktualna pozycja
	private LinkedList<Passenger> passengers; // pasażerowie w środku
	private int currentStopNumber; // numer przystanku na trasie
	private TramStop currentStop; // aktualny przystanek
	private TramStop nextStop; // następny przystanek
	private boolean direction = true; // jadę do przodu rozkładem, czy wracam?
	private boolean routeDirection; // jadę do przodu na konkretnej trasie, czy
	// wracam?
	private boolean isMoving = true; // czy poruszam się?
	private int capacity; // pojemność tramwaju
	private double waitingTime; // czas oczekiwania na przystanku
	private double speed = 0; // prędkość na danym odcinku
	private LinkingRoute currentRoute; // aktualny odcinek trasy
	private ArrayList<LinkingRoute> routes; // trasa pomiędzy przystankami
	private int currentRouteNumber; // żeby nie trzeba było za każdym razem
	// szukać
	private int id; // numer tramwaju

	/**
	 * tworzy instancję tramwaju
	 * 
	 * @param stahps
	 *            przystanki na trasie tramwaju
	 * @param capacity
	 *            pojemność
	 * @param id
	 *            numerek tramwaju
	 */
	public Tram(LinkedList<TramStop> stahps, int capacity, int id) {
		this.id = id;
		this.stahps = stahps;
		currentStop = stahps.get(0);
		nextStop = stahps.get(1);
		currentStopNumber = 1;
		this.capacity = capacity;
		position = new Point2D.Double(stahps.getFirst().getPoint().x, stahps
				.getFirst().getPoint().y);
		passengers = new LinkedList<Passenger>();
		routes = currentStop.getRoute(nextStop).getTrack();
		LinkingRoute lr = null;
		for (LinkingRoute r : routes) {
			if (r.containsPoint(currentStop.getPoint())) {
				lr = r;
				break;
			}
		}
		currentRouteNumber = routes.indexOf(lr);
		currentRoute = routes.get(currentRouteNumber);
		if (currentRouteNumber != 0) {
			routeDirection = false;
			currentRouteNumber++;
		} else {
			routeDirection = true;
		}

		speed = currentRoute.getSpeed();
	}

	/**
	 * zwraca id tramwaju
	 * 
	 * @return id tramwaju
	 */
	public int getId() {
		return id;
	}

	/**
	 * zwraca następny przystanek na trasie UWAGA!! następny przystanek jest
	 * również tym, na którym aktualnie stoimy
	 * 
	 * @return następny przystanek
	 */
	public TramStop getNextStop() {
		return nextStop;
	}

	/**
	 * zwraca aktualny przystanek na którym byliśmy UWAGA!! aktualnym
	 * przystankiem nazywamy przystanek ostatnio odwiedzony, czyli ten, z
	 * którego właśnie odjechaliśmy!!
	 * 
	 * @return aktualny przystanek
	 */
	public TramStop getCurrentStop() {
		return currentStop;
	}

	/**
	 * symulacja kroku tramwaju TODO jakiś fixed-step by się przydał i prędkość
	 * w zależności od godziny/trasy
	 */
	public void stepForward() {
		if (isMoving) { // jeżeli jedziesz to jedź
			if (waitingTime > 0) { // lub czekaj na skrzyżowaniu!
				waitingTime--;
				return;
			}
			move();
		} else { // jeżeli nie jedziesz
			waitingTime--; // to czasu postoju coraz mniej
			if (waitingTime <= 0) { // jeżeli koniec czasu, to ruszamy
				isMoving = true;
				nextStop.leave(this);
				goToNextStop(); // ustawiamy następny przystanek
			}
		}
	}

	/**
	 * ustawia następny odcinek na trasie i prędkość tramwaju
	 */
	private void setCurrentRoute() {
		// System.out.println("before "+currentRouteNumber+" " + direction+ " "
		// + routeDirection);
		if (routes.size() == 1) {
			currentRouteNumber = 0;
			routeDirection = !routeDirection;
		} else if (routeDirection == true) {
			if (currentRouteNumber == -1)
				currentRouteNumber = 0;
			else
				currentRouteNumber++;
		} else {
			//System.out.println("currentRouteNumber" + currentRouteNumber);
			if (currentRouteNumber == -1)
				currentRouteNumber = routes.size() - 1;
			else
				currentRouteNumber--;
			//System.out.println("currentRouteNumber" + currentRouteNumber);
		}
		currentRoute = routes.get(currentRouteNumber);
		speed = currentRoute.getSpeed();
		//System.out.println("after " + currentRouteNumber + " " + direction
			//	+ " " + routeDirection);
	}

	/**
	 * przemieszczenie do kolejnego przystanku uwaga, trasa MUSI kończyć się
	 * dokładnie w tym samym punkcie, co przystanek
	 */
	private void goToNextStop() {
		// jeżeli jedziesz do przodu i nie skończyła ci się trasa
		if (direction == true && currentStopNumber + 1 < stahps.size()) {
			currentStopNumber++;
			currentStop = nextStop;
			nextStop = stahps.get(currentStopNumber);
		}
		// jeżeli jedziesz w tył i nie skończyła ci się trasa
		else if (direction == false && currentStopNumber - 1 >= 0) {
			currentStopNumber--;
			currentStop = nextStop;
			nextStop = stahps.get(currentStopNumber);
		}
		// jeżeli jedziesz do przodu i skończyła ci się trasa
		else if (direction == true && currentStopNumber + 1 >= stahps.size()) {
			direction = false;
			currentStopNumber--;
			currentStop = nextStop;
			nextStop = stahps.get(currentStopNumber);
		}
		// jeżeli jedziesz do tyłu i skończyła ci się trasa
		else if (direction == false && currentStopNumber - 1 < 0) {
			direction = true;
			currentStopNumber++;
			currentStop = nextStop;
			nextStop = stahps.get(currentStopNumber);
		}
		//System.out.println(currentStop);
		routes = currentStop.getRoute(nextStop).getTrack(); // ustawienie nowej
		// trasy;
		// sprawdzenie kierunku ruchu po trasie
		LinkingRoute lr = null;
		for (LinkingRoute r : routes) {
			if (r.containsPoint(currentStop.getPoint())
					&& !r.equals(currentStop)) {
				lr = r;
				break;
			}
		} // TODO tu jest błąd
			// jeżeli jest pierwszy to jedziesz prosto, jeżeli nie, to do tyłu
		routeDirection = routes.indexOf(lr) != 0 ? false : true;
		currentRoute = lr;
		speed=currentRoute.getSpeed();
		//System.out.println("routeDirection " + routeDirection);
	}

	/**
	 * metoda przemieszczania się tramwaju
	 */
	private void move() {
		// System.out.println(currentRouteNumber);
		// TODO albo tutaj.... jest bład
		int dir = routeDirection ? 1 : 0; // strona w którą jedziemy ()
		Point2D.Double nextTrack = currentRoute.getPoints()[dir];
		double r = speed; // długość wektora przemieszczenia dla każdego kroku
		double d = position.distance(nextTrack);

		if (d < r) { // to znaczy że dojechałem
			if (nextTrack.equals(nextStop.getPoint())) { // jeżeli to przystanek
				isMoving = false;
				nextStop.arrived(this); // ogłaszamy przystankowi dojazd
				currentRouteNumber = -1; // ustawiamy żeby wiedzieć, że zmieniła
				// się trasa.
				return;
			}
			setCurrentRoute(); // ustaw kolejny punkt docelowy i prędkość
			if (currentRoute.isCrossing())
				currentRoute.manageCrossing();
			return;
		}

		double vx = (nextTrack.x - position.x) / d;
		double vy = (nextTrack.y - position.y) / d;

		position.x += vx * r;
		position.y += vy * r;
	}

	public LinkedList<TramStop> getStops() {
		return stahps;
	}

	/**
	 * zwraca aktualną pozycję tramwaju
	 * 
	 * @return pozycja tramwaju
	 */
	public Point2D getPosition() {
		return position;
	}

	/**
	 * pobiera listę pasażerów, którzy jadą tramwajem
	 * 
	 * @return lista pasażerów
	 */
	public LinkedList<Passenger> getPassengers() {
		return passengers;
	}

	/**
	 * dodaje pasażera do tramwaju
	 * 
	 * @param p
	 *            pasażer, którego chcemy dodać
	 */
	public void addPassenger(Passenger p) {
		passengers.add(p);
	}

	/**
	 * zwraca pojemność tramwaju
	 * 
	 * @return pojemność tramwaju
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * ustawia czas postoju tramwaju na przystanku/skrzyżowaniu
	 * 
	 * @param time
	 *            czas oczekiwania
	 */
	public void setWaitingTime(double time) {
		waitingTime = time;
	}

	/**
	 * zwraca listę pozostałych przystanków na trasie
	 * 
	 * @return lista pozostałych przystanków
	 */
	public List<TramStop> getRemainingStops() {
		List<TramStop> tmp = new LinkedList<TramStop>();
		int index = stahps.indexOf(nextStop); // w momencie zatrzymania jest to
		// dalej następny przystanek!
		if (direction == true && currentStopNumber + 1 < stahps.size()) {
			tmp = stahps.subList(index, stahps.size());
		}
		// jeżeli jedziesz w tył i nie skończyła ci się trasa
		else if (direction == false && currentStopNumber - 1 >= 0) {
			tmp = stahps.subList(0, index);
		}
		// jeżeli jedziesz do przodu i skończyła ci się trasa
		else {
			tmp = stahps;
		}
		return tmp;
	}
	@Override
	public String toString(){
		return String.valueOf(id);
	}
}
