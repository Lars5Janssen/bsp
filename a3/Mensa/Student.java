package Mensa;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Mensa.Student.java: Waehlt die Kassen mit der kuerzesten Warteschlange und stellt
 * sich dort an. Nach dem Bezahlen isst er fuer eine Zufallszeit und beginnt von
 * vorne.
 */
public class Student extends Thread {
	private final ReentrantLock sortMutex;
	private Mensa meineMensa;

	public Student(String name, Mensa meineMensa, ReentrantLock sortMutex) {
		this.setName(name);
		this.meineMensa = meineMensa;
		this.sortMutex = sortMutex;
	}

	public void run() {
		Kasse besteKasse;

		try {
			while (!isInterrupted()) {
				/*
				 * Waehle die Mensa.Kasse mit der kuerzesten Warteschlange --> Sortiere absteigend
				 */
				sortMutex.lock(); // Kritischer Abschnitt 1 start
				Collections.sort(meineMensa.kassenliste);
				besteKasse = meineMensa.kassenliste.getFirst();
				System.out.printf("Beste Mensa.Kasse: %s%n", besteKasse.getKassenName());
				System.err.print(this.getName() + " waehlt Mensa.Kasse " + besteKasse.getKassenName() + "\n");
				meineMensa.showScore();

				// Warteschlangenzaehler erhoehen
				besteKasse.inkrAnzahlStudenten();
				sortMutex.unlock(); // Kritischer Abschnitt 1 stop

				// An Mensa.Kasse anstellen
				besteKasse.enter();

				// Mensa.Kasse verlassen --> Warteschlangenzaehler erniedrigen
            System.err.println(this.getName() + " verlaesst Mensa.Kasse " + besteKasse.getKassenName());
				sortMutex.lock(); // Kritischer Abschnitt 2 start
				besteKasse.dekrAnzahlStudenten();
				sortMutex.unlock(); // Kritischer Abschnitt 2 stop
				// Fuer unbestimmte Zeit essen
				essen();
			}
		} catch (InterruptedException e) {
		}
		System.err.println("Mensa.Student " + this.getName() + " beendet seine Teilnahme");
	}

	// Studenten benutzen diese Methode, um zu essen oder sich zu vergnuegen
	public void essen() throws InterruptedException {
		int sleepTime = (int) (100 * Math.random());
		Thread.sleep(sleepTime);

	}
}
