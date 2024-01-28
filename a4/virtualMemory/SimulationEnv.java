/**
 * Simulation eines Hauptspeicherverwaltungssystems auf Basis eines
 * "virtuellen Speichers" mit Demand Paging, lokaler Seitenersetzungsstrategie
 * und fester Hauptspeicherzuteilung pro Prozess.
 * 
 * Initialisierung der Simulationsumgebung, Start/Ende der Simulation und
 * Auswertung
 * 
 * @author (c) Prof. Dr.-Ing. Martin Hübner, HAW Hamburg
 */
public class SimulationEnv {

	/**
	 * Dauer der Simulation in Millisekunden
	 */
	public static int simulationTime;

	/**
	 * Anzahl an erzeugten Prozessen (1 reicht für die Auswertung der
	 * Seitenfehlerrate)
	 */
	public static final int NUM_OF_PROCESSES = 1;

	/**
	 * Main-Methode zum Start der Simulation
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		int pid; // Aktuelle Prozess-ID

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		// "Laden" des Betriebssystems
		OperatingSystem os = new OperatingSystem();
		System.out
				.println("*********** Simulation der Betriebssystem-Speicherverwaltung startet *************");

		// ------------------------- Parameter setzen
		// ------------------------------------------
		// Dauer der Simulation in ms
		simulationTime = 800;

		// max. Anzahl Seiten pro Prozess im Hauptspeicher (sonst Verdrängung eigener Seiten)
		os.setMAX_RAM_PAGES_PER_PROCESS(Integer.parseInt(args[0]));

		// CLOCK oder FIFO oder RANDOM
		os.setREPLACEMENT_ALGORITHM(OperatingSystem.ImplementedReplacementAlgorithms.FIFO);

		// Anzahl Operationen innerhalb eines lokalen Adressbereichs
		os.setDEFAULT_LOCALITY_FACTOR(Integer.parseInt(args[1]));
		System.out.println(Integer.parseInt(args[0]));
		System.out.println(Integer.parseInt(args[1]));

		// Testausgaben erwünscht? Wenn true, dann simulationTime auf max. 200 ms setzen!
		os.setTestMode(false);

		// ------------------------- Parameter setzen Ende
		// -----------------------------------------------

		// Erzeugen von unabhängigen Prozessen
		for (int i = 0; i < NUM_OF_PROCESSES; i++) {
			// 20 Seiten bei einer Seitengröße von 256 Byte = 5120 Byte benötigter RAM
			pid = os.createProcess(5120);
			if (pid < 0) {
				System.out
						.println("*********** Fehlerhafte Konfiguration: Zu wenig RAM f�r "
								+ NUM_OF_PROCESSES + " Prozesse! *************");
				break;
			}
		}
		// Laufzeit abwarten
		try {
			Thread.sleep(simulationTime);
		} catch (InterruptedException e) {
		}
		// Alle Prozesse stoppen
		os.killAll();

		System.out
				.println("*********** Simulation der Betriebssystem-Speicherverwaltung wurde nach "
						+ simulationTime + " ms beendet *************");

		// Statistische Auswertung anzeigen
		os.eventLog.showReport();
	}
}
