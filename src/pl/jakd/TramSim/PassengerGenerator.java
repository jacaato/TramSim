package pl.jakd.TramSim;

import java.util.*;
import pl.jakd.TramSim.util.Generator;

/**
 * klasa generująca pasażerów TODO: praktycznie wszystko tutaj jest do
 * przepisania, ale to dopiero jak zdobędziemy dane
 * 
 * @author jacek
 */
public class PassengerGenerator implements Generator<Passenger> {
	private LinkedList<TramStop> stop; // lista przystanków
	private static Random rand = new Random();

	/**
	 * tworzy nową instancję generatora
	 * 
	 * @param stahp
	 *            wszystkie przystanki w mieście
	 */
	public PassengerGenerator(LinkedList<TramStop> stahp) {
		this.stop = stahp;
	}

	@Override
	public Passenger next() {
		if (rand.nextInt() % 3 != 0)
			return null;
		Passenger p;
		TramStop s;
		TramStop d;
		s = stop.get(rand.nextInt(stop.size()));
		do {
			d = stop.get(rand.nextInt(stop.size()));
		} while (d.equals(s));
		p = new Passenger(s, d);
		s.addPassenger(p);

		return p;
	}
}
