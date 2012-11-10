package pl.jakd.TramSim.util;

/**
 * Generator
 * 
 * @author jacek
 */
public interface Generator<T> {
	/**
	 * generuje kolejną wartość/obiekt
	 * 
	 * @return kolejna wartość/obiekt T
	 */
	public T next();
}
