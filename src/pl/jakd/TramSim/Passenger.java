package pl.jakd.TramSim;

/**
 * reprezentacja pasażera
 * 
 * @author jacek
 * 
 */
public class Passenger {
	private TramStop start; // przystanek początkowy
	private TramStop dest; // przystanek końcowy
	private TramStop intercharge; // następny przystanek przesiadkowy
	private int tramId; // numer tramwaju którym chcę jechać
	/**
	 * tworzy nowego pasażera
	 * 
	 * @param s
	 *            przystanek na którym się znajduję
	 * @param d
	 *            przystanek na który jadę
	 */
	public Passenger(TramStop s, TramStop d) {
		this.start = s;
		this.dest = d;
		intercharge = null;
		tramId = -1; // nieistniejący
	}

	/**
	 * zwraca przystanek na którym stoi pasażer
	 * 
	 * @return przystanek na któym stoi
	 */
	public TramStop getStart() {
		return start;
	}

	/**
	 * zwraca przystanek docelowy pasażera
	 * 
	 * @return przystanek docelowy pasażera
	 */
	public TramStop getDest() {
		return dest;
	}
	/**
	 * zwraca numer tramwaju którym chce jechać pasażer
	 * @return numer tramwaju
	 */
	public int getTramId(){
		return tramId;
	}
	/**
	 * ustawia numer tramwaju, którym chce jechać pasażer
	 * @param tramId numer tramwaju, którym chce jechać pasażer
	 */
	public void setTramId(int tramId){
		this.tramId = tramId;
	}
	/**
	 * zwraca przystanek przesiadkowy dla pasażera
	 * @return przystanek przesiadkowy
	 */
	public TramStop getIntercharge(){
		return intercharge;
	}
	/**
	 * ustawia kolejny przystanek przesiadkowy na trasie
	 * @param intercharge przystanek przesiadkowy
	 */
	public void setIntercharge(TramStop intercharge){
		this.intercharge = intercharge;
	}
	
	
	// założenie: pasażerowie są tacy sami, jeżeli jadą w to samo miejsce
	@Override
	public boolean equals(Object o) {
		if (o instanceof Passenger)
			if (((Passenger) o).dest.equals(this.dest))
				return true;
		return false;
	}
	@Override
	public String toString(){
		return "Passenger source: "+start+" dest: " + dest + " intercharge: "+intercharge ;
	}
}
