import java.util.*;

/**
 * Student.java: Waehlt die Kassen mit der kuerzesten Warteschlange und stellt
 * sich dort an. Nach dem Bezahlen isst er fuer eine Zufallszeit und beginnt von
 * vorne.
 */
public class Student extends Thread {
	private Mensa meineMensa;

	public Student(String name, Mensa meineMensa) {
		this.setName(name);
		this.meineMensa = meineMensa;
	}

	public void run() {
		Kasse besteKasse;

		try {
			while (!isInterrupted()) {
				/*
				 * Waehle die Kasse mit der kuerzesten Warteschlange --> Sortiere absteigend
				 */
				Collections.sort(meineMensa.kassenliste);
				besteKasse = meineMensa.kassenliste.getFirst();
				System.err.print(this.getName() + " waehlt Kasse " + besteKasse.getKassenName());
				meineMensa.showScore();
				
				// Warteschlangenzaehler erhoehen
				besteKasse.inkrAnzahlStudenten();

				// An Kasse anstellen 
				besteKasse.enter();

				// Kasse verlassen --> Warteschlangenzaehler erniedrigen
            System.err.println(this.getName() + " verlaesst Kasse " + besteKasse.getKassenName());            
				besteKasse.dekrAnzahlStudenten();

				// Fuer unbestimmte Zeit essen
				essen();
			}
		} catch (InterruptedException e) {
		}
		System.err.println("Student " + this.getName() + " beendet seine Teilnahme");
	}

	// Studenten benutzen diese Methode, um zu essen oder sich zu vergnuegen
	public void essen() throws InterruptedException {
		int sleepTime = (int) (100 * Math.random());
		Thread.sleep(sleepTime);

	}
}
