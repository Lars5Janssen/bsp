package Mensa;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Mensa.Kasse.java: Bietet eine Zugangsmethode ("enter") an mit anschliessendem
 * Bezahlen. Exklusiver Zugang durch Mutex! Die Anzahl wartender Studenten kann
 * inkrementiert, dekrementiert und abgefragt werden. Fuer die Sortierung wird
 * das Comparable<Mensa.Kasse>-Interface implementiert, dass die aktuelle
 * Studi-Warteschlangenlaenge als Kriterium verwendet.
 */
public class Kasse implements Comparable<Kasse> {
	private int kassenNummer;
	private int anzahlStudenten;

	public ReentrantLock getKassenMutex() {
		return kassenMutex;
	}

	private ReentrantLock kassenMutex = new ReentrantLock(true); // Faire Warteschlange vor dieser Mensa.Kasse!
	//private ReentrantLock StudentenQueueMutex = new ReentrantLock(true); // Faire Warteschlange vor dieser Mensa.Kasse!

	public Kasse(int num) {
		kassenNummer = num;
	}

	/*
	 * Mensa.Student ruft die Methode ENTER auf --> Mutex garantiert den exklusiven
	 * Zugriff und verwaltet die Warteschlange!
	 */
	public void enter() throws InterruptedException {
		try {
			kassenMutex.lock();
			System.out.printf("Int: %d | Mutex: %d\n", anzahlStudenten, kassenMutex.getQueueLength());
			// Zahlvorgang abwarten
			bezahlen();
		} finally {
			// Mensa.Kasse verlassen
			kassenMutex.unlock();
		}
	}

	// Studenten im Kassenbereich benutzen diese Methode, um zu bezahlen
	private void bezahlen() throws InterruptedException {
		int sleepTime = (int) (100 * Math.random());
		Thread.sleep(sleepTime);
	}

	/**
	 * @return the kassenNummer
	 */
	public int getKassenNummer() {
		return kassenNummer;
	}

	/**
	 * @return the kassenName (Ascii-Code von 'A': 65)
	 */
	public String getKassenName() {
		return Character.toString((char) (kassenNummer + 65));
	}
	
	/**
	 * @return the anzahlStudenten
	 */
	public int getAnzahlStudenten() {
		return anzahlStudenten;
	}

	public void inkrAnzahlStudenten() {
		anzahlStudenten++;
		System.out.printf("Anazhl Studenten: %d\n", anzahlStudenten);
	}

	public void dekrAnzahlStudenten() {
		anzahlStudenten--;
		System.out.printf("Mensa.Kasse: %s Anazhl Studenten: %d\n",kassenNummer, anzahlStudenten);
	}
	@Override
	public int compareTo(Kasse kasse2) {
		/* Sortiere Kassen nach Warteschlangenlaenge aufsteigend */
		return this.getAnzahlStudenten() - kasse2.getAnzahlStudenten();
	}
}
