package pl.jakd.TramSim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * główny obiekt symulacji, reprezentuje całe miasto. jest odpowiedzialny za
 * symulowanie i wyświetlanie wyników
 * 
 * @author jacek
 * 
 */
@SuppressWarnings("serial")
public class City extends Canvas {
	private static final int REFRESH = 32; // odświeżanie rysowania
	private static final int SIM_PERIOD = 15; // okres symulacji
	private static final int STOP_SIZE = 10; // rozmiar przystanku (wielkość
	// maziana)
	private static final int TRAM_SIZE = 5; // rozmiar tramwaju (wielkość
	// maziana)

	private ExecutorService exec = Executors.newFixedThreadPool(2); // zarządca
	// wątków
	private Runnable printingTask; // zadanie rysujące
	private Runnable simTask; // zadanie symulacji

	private LinkedList<TramStop> tramStops = new LinkedList<TramStop>(); // przystanki
	// w
	// mieście
	private LinkedList<Tram> trams = new LinkedList<Tram>(); // tramwaje
	private LinkedList<Route> routes = new LinkedList<Route>(); // trasy
	// pomiędzy
	// przystankami
	private PassengerGenerator passengerGenerator; // genereator pasażerów

	private final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEEE dd MMMM yyyy HH:mm:ss");

	// private static Random rand = new Random();
	/**
	 * tworzy nowy obiekt miasta wczytuje dane symulacji z pliku xml
	 */
	public City() {
		// parsowanie xml'a
		String configFile = "./res/Cracow.xml";
		parseCity(configFile);

		passengerGenerator = new PassengerGenerator(tramStops);

		// rzeczy zainicjalizowane

		this.setSize(800, 600);
		this.setIgnoreRepaint(true);
		this.setVisible(true);
	}

	/**
	 * tworzy i uruchamia wątki dla symulacji i rysowania
	 */
	public void start() {
		printingTask = new Runnable() {
			@Override
			public void run() {
				while (true) {
					paintCity();
					try {
						TimeUnit.MILLISECONDS.sleep(REFRESH);
					} catch (Exception e) {
					}
				}
			}
		};
		simTask = new Runnable() {
			@Override
			public void run() {
				while (true) {
					simulate();
					try {
						TimeUnit.MILLISECONDS.sleep(SIM_PERIOD);
					} catch (Exception e) {
					}
				}
			}
		};

		exec.execute(simTask);
		exec.execute(printingTask);
		exec.shutdown();
	}

	/**
	 * strach dotknąć :|
	 * 
	 * @param configFile
	 *            plik xml z którego wczytujemy dane symulacji
	 */
	private void parseCity(String configFile) {
		boolean firstTrain = true;
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(configFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				// jeżeli jesteś elementem startowym
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					// tworzenie przystanków
					if (startElement.getName().getLocalPart()
							.equals("TramStop")) {
						String name = startElement.getAttributeByName(
								new QName("name")).getValue();
						while (eventReader.hasNext()) {
							event = eventReader.nextEvent();
							if (event.isStartElement()) {
								if (event.asStartElement().getName()
										.getLocalPart().equals("Point")) {
									double x = Double.parseDouble(event
											.asStartElement()
											.getAttributeByName(new QName("x"))
											.getValue());
									double y = Double.parseDouble(event
											.asStartElement()
											.getAttributeByName(new QName("y"))
											.getValue());
									TramStop tramStop = new TramStop(name,
											new Point2D.Double(x, y));
									tramStops.add(tramStop);
								}
							} else if (event.isEndElement()) {
								if (event.asEndElement().getName()
										.getLocalPart().equals("TramStop"))
									;
								break;
							}
						}
					}
					// jeżeli jesteś route
					if (startElement.getName().getLocalPart().equals("Route")) {
						// pobierz przystanki
						TramStop t1 = null;
						TramStop t2 = null;
						while (eventReader.hasNext()) {
							event = eventReader.nextEvent();
							if (event.isStartElement()) {
								if (event.asStartElement().getName()
										.getLocalPart().equals("Stop")) {
									for (TramStop stop : tramStops) {
										if (stop.equals(event
												.asStartElement()
												.getAttributeByName(
														new QName("name"))
												.getValue())) {
											if (t1 == null)
												t1 = stop;
											else
												t2 = stop;
											break;
										}
									}
								}
								if (event.asStartElement().getName()
										.getLocalPart().equals("LinkingRoutes")) {
									break;
								}
							}
						}
						// część dla tworzenia route (linkingroutes)
						ArrayList<LinkingRoute> lrl = new ArrayList<LinkingRoute>();
						while (eventReader.hasNext()) {
							event = eventReader.nextEvent();
							if (event.isStartElement()) {
								if (event.asStartElement().getName()
										.getLocalPart().equals("LinkingRoute")) {
									// System.out.println(event);
									int speed = Integer.parseInt(event
											.asStartElement()
											.getAttributeByName(
													new QName("speed"))
											.getValue());
									boolean isCrossing = event
											.asStartElement()
											.getAttributeByName(
													new QName("type"))
											.getValue()
											.equalsIgnoreCase("crossing");
									Point2D.Double p1 = null;
									Point2D.Double p2 = null;
									// część dla linking route
									while (eventReader.hasNext()) {
										event = eventReader.nextEvent();
										if (event.isStartElement()) {
											if (event.asStartElement()
													.getName().getLocalPart()
													.equals("Point")) {
												double x = Double
														.parseDouble(event
																.asStartElement()
																.getAttributeByName(
																		new QName(
																				"x"))
																.getValue());
												double y = Double
														.parseDouble(event
																.asStartElement()
																.getAttributeByName(
																		new QName(
																				"y"))
																.getValue());
												if (p1 == null)
													p1 = new Point2D.Double(x,
															y);
												else
													p2 = new Point2D.Double(x,
															y);
											}
										} else if (event.isEndElement()) {
											if (event.asEndElement().getName()
													.getLocalPart()
													.equals("LinkingRoute")) {
												lrl.add(new LinkingRoute(p1,
														p2, speed, isCrossing));
												break;
											}
										}
									}

								}
							} else if (event.isEndElement()) {
								if (event.asEndElement().getName()
										.getLocalPart().equals("LinkingRoutes")) {
									routes.add(new Route(t1, t2, lrl));
									break;
								}
							}
						}

						// System.out.println(""+t1+t2);
					}
					// tworzenie tramwajów
					if (startElement.getName().getLocalPart().equals("Tram")) {

						// dodawanie tras do przystanków.
						// zrobienie tego w tym miejscu gwarantuje że wszystko
						// wcześniej było już zainicjalizowane
						if (firstTrain) {
							for (TramStop ts : tramStops) {
								LinkedList<Route> tmpRoutes = new LinkedList<Route>();
								for (Route r : routes) {
									if (r.equals(ts)) {
										tmpRoutes.add(r);
									}
								}
								// System.out.println(Arrays.toString(tmpRoutes.toArray()));
								ts.setRoutes(tmpRoutes);
							}
							firstTrain = false;
						}

						int id = Integer
								.parseInt(startElement.getAttributeByName(
										new QName("id")).getValue());
						int capacity = Integer.parseInt(startElement
								.getAttributeByName(new QName("capacity"))
								.getValue());
						LinkedList<TramStop> ts = new LinkedList<TramStop>();
						while (eventReader.hasNext()) {
							event = eventReader.nextEvent();
							if (event.isStartElement()) {
								if (event.asStartElement().getName()
										.getLocalPart().equals("Stop")) {
									for (TramStop stop : tramStops) {
										if (stop.equals(event
												.asStartElement()
												.getAttributeByName(
														new QName("name"))
												.getValue())) {
											ts.add(stop);
											break;
										}
									}
								}
							} else if (event.isEndElement()) {
								if (event.asEndElement().getName()
										.getLocalPart().equals("Tram")) {
									trams.add(new Tram(ts, capacity, id));
									break;
								}
							}
						}
					}
				}
			}
			//ustalanie rozkładów dla przystanków
			for(Tram tram : trams){
				for(TramStop stop :tram.getStops()){
					stop.addTram(tram);
				}
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		
	}

	private BufferStrategy buffer;
	private Graphics2D graphics;
	private Point2D.Double point;
	private Point2D pointTram;
	private long startTime=System.currentTimeMillis();
	/**
	 * maluje miasto, używany przez wątek rysujący
	 */
	private synchronized void paintCity() {
		long fps = 1000/(System.currentTimeMillis()-startTime);
		startTime = System.currentTimeMillis();
		try {
			if (buffer == null) {
				buffer = this.getBufferStrategy();
			}
			graphics = (Graphics2D) buffer.getDrawGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, getWidth(), getHeight());

			graphics.setColor(Color.BLACK);
			// wypisujemy nr iteracji + czas
			graphics.drawString(
					"iteration " + TramSim.iter + "    itersPerSecond: " + itersPerSecond + "    fps: "+ fps +  "    czas: "
							+ sdf.format(TramSim.c.getTime()), 1, 10);
			// rysujemy przystanki i ilość pasażerów
			for (TramStop t : tramStops) {
				point = t.getPoint();
				graphics.fillOval((int) point.x, (int) point.y, STOP_SIZE,
						STOP_SIZE);
				graphics.drawString(
						t.getName() + " "
								+ Integer.toString(t.getPassengers().size()),
						(int) point.x + STOP_SIZE + 1, (int) point.y
								+ STOP_SIZE + 1);
			}
			// rysujemy trasy
			graphics.setColor(Color.BLUE);
			for (Route n : routes) {
				for (LinkingRoute lr : n.getTrack()) {
					graphics.draw(lr.getLine());
				}
			}
			// rysujemy tramwaj i liczbę pasażerów w środku
			graphics.setColor(new Color(255, 0, 255));
			for (Tram t : trams) {
				pointTram = t.getPosition();
				graphics.fillOval((int) pointTram.getX(),
						(int) pointTram.getY(), TRAM_SIZE, TRAM_SIZE);
				graphics.drawString(
						t.getId() + " p:"
								+ Integer.toString(t.getPassengers().size()),
						(int) pointTram.getX() + TRAM_SIZE + 1,
						(int) pointTram.getY() + TRAM_SIZE + 1);

			}

			buffer.show();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (graphics != null)
				graphics.dispose();
		}
	}
	
	private long simTime = System.currentTimeMillis();
	private long itersPerSecond = 0;
	/**
	 * symuluje miasto. używane przez wątek sumulacji
	 */
	private synchronized void simulate() {
		itersPerSecond = 1000/(System.currentTimeMillis()-simTime);
		simTime = System.currentTimeMillis();
		
		TramSim.iter++; // zwiększamy licznik iteracji;
		TramSim.c.add(Calendar.SECOND, 5);// zwiększamy czas
		// dochodzą pasażerowie
		passengerGenerator.next();
		// wsiadają/wysiadają
		// połączone z jechaniem tramwaju (gdy tramwaj osiągnie przystanek
		// powiadamia o tym)
		// tramwaj jedzie
		for (Tram t : trams) {
			t.stepForward();
		}
	}
}
