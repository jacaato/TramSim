package pl.jakd.TramSim;

import java.util.*;

/**
 * służy do zarządzania pasażerami i tramwajem po zatrzymaniu się tramwaju
 * założenie: pasażer jest zawsze przydzielany w pierwszej kolejności do
 * tramwajów tych, które jadą docelowo, dopiero później przesiadki
 * 
 * @author jacek
 * 
 */
public class StopWatcher {
	private final static double WAITING = 0.1; // czas oczekiwania na każdego
	// pasażera
	private final static double WAITING_CONST = 2; // stały czas zatrzymania
	private TramStop stop; // przystanek którym zarządzam
	private Tram currentTram; // akutalny tramwaj na przystanku
	private LinkedList<Tram> waitingTrams = new LinkedList<Tram>();
	private double waitingTime = WAITING_CONST;
	private HashMap<TramStop, TramStop> polaczenie = new HashMap<TramStop, TramStop>(); // czyli
																						// w
																						// który
																						// tramwaj
																						// należy
																						// wsiąść
																						// i
																						// gdzie
																						// pojechać,
																						// jeżeli
																						// chcemy
																						// dojechać
																						// z
																						// przesiadką.
																						// mapuje
																						// dest
																						// na
																						// intercharge

	/**
	 * nowa instancja zarządcy
	 * 
	 * @param stop
	 *            zarządzany przystanek
	 */
	public StopWatcher(TramStop stop) {
		this.stop = stop;
	}

	/**
	 * przydziela pasażerów do tramwaju i wysiada wysiadających :P TODO
	 * przesiadki! TODO niech sprawdzają tylko następne przystanki tramwaju, a
	 * nie poprzednie!
	 * */
	private void manage() {
		// najpierw wysiadający
		LinkedList<Passenger> pas = currentTram.getPassengers();
		// pasażerowie są tacy sami jeżeli jadą w dane miejsce
		Passenger p = new Passenger(null, stop);
		
		
		LinkedList<Passenger> tmp = new LinkedList<Passenger>();
		for(Passenger pasInTram : pas){
			System.out.print(stop + " " + currentTram + " "+ pasInTram);
			if(pas.contains(p)){
				tmp.add(pasInTram);
			}
			else if(pasInTram.getIntercharge()!=null && pasInTram.getIntercharge().equals(stop)){
				System.out.print(" removed");
				//przesiadkowych dodajemy do przystanku
				this.addPassenger(pasInTram);
				tmp.add(pasInTram);
				waitingTime++;
			}
			System.out.println("");
		}
		pas.removeAll(tmp);
		tmp=null;
		// potem wsiadający
		pas = stop.getPassengers();
		outer: for (Passenger passenger : pas) { // dla każdego pasażera na
			// przystanku
			for (TramStop s : currentTram.getRemainingStops()) { // dla każdego
				// pozostałego
				// przystanku
				// na trasie
				if (passenger.getDest().equals(s)
						|| (passenger.getIntercharge() != null && passenger
								.getIntercharge().equals(s))) { // jeżeli nie
																// masz
																// przesiadki,
																// lub jeżeli
																// masz
																// przesiadkę
					if (currentTram.getPassengers().size() < currentTram
							.getCapacity()) {
						currentTram.addPassenger(passenger);
						waitingTime += WAITING;
					} else
						break outer; // nie ma miejsca, więc jedziemy!
				}
			}

		}
		// usuwamy ich z przystanku
		int i = currentTram.getCapacity();
		for (Passenger passenger : currentTram.getPassengers()) {
			if (i > 0) { // niech nie usuwa tych, co nie pojechali
				pas.remove(passenger);
				i--;
			} else {
				return;
			}
		}
		currentTram.setWaitingTime(waitingTime); // ustawia czas postoju
		// tramwaju w jednostkach
		// symulacji
		waitingTime = WAITING_CONST;
	}

	/**
	 * jeżeli nie znajdzie pasażerowi bezpośredniego połączenia, wyszukuje mu
	 * najlepszy przystanek przesiadkowy i ustawia mu go
	 * 
	 * @param p
	 */
	public void addPassenger(Passenger p) {
		boolean znalazlDroge=false;
		p.setIntercharge(null);
		if (stop.getTimeTable() == null)
			return;
		for (Tram tram : stop.getTimeTable()) {
			if (tram.getStops().contains(p.getDest())) {
				p.setIntercharge(null); // nie ma przesiadki
				stop.getPassengers().add(p);
				return;
			}
		}
		// jeżeli nie znalazł bezpośredniego połączenia
		// TODO brzydkie, brzydsze, najbrzydsze (niepotrzebne skreślić)
		// UWAGA: MAX 2 PRZESIADKI UWZGLĘDNIAM
		if (polaczenie.get(p.getDest())!=null) {
			/*System.out.println("  znalazłem przesiadkę w mapie! src " + stop
					+ " dest " + p.getDest() + " intercharge "
					+ polaczenie.get(p.getDest()));*/
			znalazlDroge=true;
			p.setIntercharge(polaczenie.get(p.getDest()));
		} else {
			// dla każdego tramwaju przejeżdżającego przez ten przystanek
			outer: for (Tram tram : stop.getTimeTable()) {
				// dla każdego przystanku na trasie tego tramwaju
				for (TramStop tramStop : tram.getStops()) {
					// sprawdź czy istnieje bezpośrednie połączenie
					for (Tram tram2 : tramStop.getTimeTable()) {
						for (TramStop tramStop2 : tram2.getStops()) {
							if (tramStop2.equals(p.getDest())) {
								if(tramStop.equals(stop) || tramStop.equals(p.getStart())) // żeby się nie zapętliło
									continue;
								// ustaw pasażerowi najbliższy przystanek na
								// który ma jechać
								p.setIntercharge(tramStop);
								// dodaj do mapy połączenie
								polaczenie.put(p.getDest(), tramStop);
								/*System.out
										.println("1 znalazłem przesiadkę BEZ mapy! src "
												+ stop
												+ " dest "
												+ p.getDest()
												+ " intercharge "
												+ polaczenie.get(p.getDest()));*/
								znalazlDroge=true;
								break outer;
							}
						}
					}
				}
				// dla każdego rpzystanku na trasie tego tramwaju (2
				// przesiadki!!)
				for (TramStop tramStop : tram.getStops()) {
					// dla każdego tramwaju na tym przystanku
					for (Tram tram2 : tramStop.getTimeTable()) {
						for (TramStop tramStop2 : tram2.getStops()) {
							for (Tram tram3 : tramStop2.getTimeTable()) {
								for (TramStop tramStop4 : tram3.getStops()) {
									if (tramStop4.equals(p.getDest())) {
										if(tramStop.equals(stop) || tramStop.equals(p.getStart()))
											continue;
										// ustaw pasażerowi najbliższy
										// przystanek na który ma jechać
										p.setIntercharge(tramStop);
										// dodaj do mapy połączenie
										polaczenie.put(p.getDest(), tramStop);
										/*System.out
												.println("2 znalazłem przesiadkę BEZ mapy! src "
														+ stop
														+ " dest "
														+ p.getDest()
														+ " intercharge "
														+ polaczenie.get(p
																.getDest()));*/
										znalazlDroge=true;
										break outer;
									}
								}
							}
						}
					}

				}
			}
		}
		if(znalazlDroge){
			stop.getPassengers().add(p);
		}
		else{
			System.err.println("Pasażer nie znalazł drogi: " + p);
		}
	}

	/**
	 * obsługa przyjazdu tramwaju
	 * 
	 * @param t
	 *            tramwaj który przyjeżdża
	 */
	public void arrived(Tram t) {
		waitingTrams.add(t); // dodaj do kolejki
		t.setWaitingTime(Integer.MAX_VALUE); // ustaw nieokreślony czas
		// oczekiwania
		if (waitingTrams.peekFirst().equals(t)) { // jeżeli nikt nie czeka, to
			// obsłuż (adresy)
			currentTram = waitingTrams.peekFirst();
			manage();
		}
	}

	/**
	 * obsługa odjazdu tramwaju
	 * 
	 * @param t
	 *            tramwaj który odjeżdża
	 */
	public void leave(Tram t) {
		waitingTrams.removeFirst(); // usuń tramwaj z kolejki oczekujących
		currentTram = waitingTrams.peekFirst(); // pobierz kolejny tramwaj
		if (currentTram != null) // jeżeli jest kogo, to obsłuż
			manage();
	}
}
