package pl.jakd.TramSim;

import java.util.ArrayList;

/**
 * Klasa reprezentująca trasę łączącą dwa przystanki
 * 
 * @author jacek
 */
public class Route {
    private TramStop t1; // przystanki, które łączy
    private TramStop t2;
    private ArrayList<LinkingRoute> checkpoints;

    /**
     * tworzy trasę łączącą dwa przystanki
     * 
     * @param t1
     *            przystanek początkowy
     * @param t2
     *            przystanek końcowy
     * @param checkpoints
     *            zbiór odcinków, na które składa się dana trasa
     */
    public Route(TramStop t1, TramStop t2, ArrayList<LinkingRoute> checkpoints) {
	this.t1 = t1;
	this.t2 = t2;
	this.checkpoints = checkpoints;
    }

    /**
     * są jednakowe jeżeli prowadzą z t1 do t2. zakładamy że istnieje tylko
     * jedna bezpośrednia trasa z A do B porównanie z przystankiem zwróci true
     * jeżeli jest jednym z przystanków t1, bądź t2
     */
    @Override
    public boolean equals(Object o) {
	if (o instanceof Route) {
	    if (((Route) o).t1.equals(this.t1)
		    && ((Route) o).t2.equals(this.t2)) {
		// System.out.println(t1 + " " + t2 + " " + o + " OK");
		return true;
	    } else if (((Route) o).t1.equals(this.t2)
		    && ((Route) o).t2.equals(this.t1)) {
		// System.out.println(t1 + " " + t2 + " " + o + " OK");
		return true;
	    }
	}
	if (o instanceof TramStop) {
	    if (o.equals(t1))
		return true;
	    if (o.equals(t2))
		return true;
	}
	// System.out.println(t1 + " " + t2 + " " + o + " :(");
	return false;
    }

    /**
     * zwraca przystanek początkowy i końcowy tej trasy
     * 
     * @return tablica przystanków
     */
    public TramStop[] getStops() {
	return new TramStop[] { t1, t2 };
    }

    /**
     * zwraca odcinki reprezentujące tę trasę
     * 
     * @return lista odcinków
     */
    public ArrayList<LinkingRoute> getTrack() {
	return checkpoints;
    }

    @Override
    public String toString() {
	return "Trasa: " + t1 + "-" + t2;
    }
}
