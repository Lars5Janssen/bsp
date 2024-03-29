import java.util.*;

/**
 * PageTable.java
 * 
 * Eine Seitentabelle eines Prozesses, implementiert als ArrayList von
 * PageTableEntry-Elementen (pte)
 * 
 */
public class PageTable {
	/**
	 * Die Seitentabelle als ArrayList von Seitentabelleneinträgen
	 * (PageTableEntry). Die Seitentabelle darf nicht sortiert werden!
	 */
	private ArrayList<PageTableEntry> pageTable;

	/**
	 * Liste aller Seiten, die sich im RAM befinden
	 */
	private LinkedList<PageTableEntry> pteRAMlist;

	/**
	 * Uhrzeiger für Clock-Algorithmus
	 */
	private int pteRAMlistIndex;

	/**
	 * Zeiger auf das Betriebssystem-Objekt
	 */
	private OperatingSystem os;

	/**
	 * Prozess-ID des eigenen Prozesses
	 */
	private int pid;

	/**
	 * Konstruktor
	 */
	public PageTable(OperatingSystem currentOS, int myPID) {
		os = currentOS;
		pid = myPID;
		// Die Seitentabelle erzeugen
		pageTable = new ArrayList<PageTableEntry>();
		// Die Liste auf RAM-Seiteneinträge erzeugen
		pteRAMlist = new LinkedList<PageTableEntry>();
		pteRAMlistIndex = 0;
	}

	/**
	 * Rückgabe: Seitentabelleneintrag pte (PageTableEntry) für die übergebene
	 * virtuelle Seitennummer (VPN = Virtual Page Number) oder null, falls Seite
	 * nicht existiert
	 */
	public PageTableEntry getPte(int vpn) {
		PageTableEntry returnValue = null; // Default
		if (vpn >= 0 && vpn < pageTable.size()) {
			returnValue = pageTable.get(vpn);
		}
		return returnValue;
	}

	/**
	 * Einen Eintrag (PageTableEntry) an die Seitentabelle anhängen. Die
	 * Seitentabelle darf nicht sortiert werden!
	 */
	public void addEntry(PageTableEntry pte) {
		pageTable.add(pte);
	}

	/**
	 * Rückgabe: Aktuelle Größe der Seitentabelle.
	 */
	public int getSize() {
		return pageTable.size();
	}

	/**
	 * Pte in pteRAMlist eintragen, wenn sich die Zahl der RAM-Seiten des
	 * Prozesses erhöht hat.
	 */
	public void pteRAMlistInsert(PageTableEntry pte) {
		pteRAMlist.add(pte);
	}

	/**
	 * Eine Seite, die sich im RAM befindet, anhand der pteRAMlist zur Ersetzung
	 * auswählen und zurückgeben
	 */
	public PageTableEntry selectNextRAMpteAndReplace(PageTableEntry newPte) {
		PageTableEntry returnValue = null;
		if (os.getReplacementAlgorithm() == OperatingSystem.ImplementedReplacementAlgorithms.CLOCK) {
			returnValue = clockAlgorithm(newPte);
		} else if (os.getReplacementAlgorithm() == OperatingSystem.ImplementedReplacementAlgorithms.FIFO) {
			returnValue = fifoAlgorithm(newPte);
		} else {
			returnValue = randomAlgorithm(newPte);
		}
		return returnValue;
	}

	/**
	 * FIFO-Algorithmus: Auswahl = Listenkopf (1. Element) Anschließend
	 * Listenkopf löschen, neue Seite (newPte) an Liste anhängen
	 */
	private PageTableEntry fifoAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte; // Auswahl

		pte = (PageTableEntry) pteRAMlist.getFirst();
		os.testOut("Prozess " + pid + ": FIFO-Algorithmus hat pte ausgewählt: "
				+ pte.virtPageNum);
		pteRAMlist.removeFirst();
		pteRAMlist.add(newPte);
		return pte;
	}

	/**
	 * CLOCK-Algorithmus (Second-Chance): Nächstes Listenelement, ausgehend vom
	 * aktuellen Index, mit Referenced-Bit = 0 (false) auswählen Sonst R-Bit auf
	 * 0 setzen und nächstes Element in der pteRAMlist untersuchen. Anschließend
	 * die ausgewählte Seite durch die neue Seite (newPte) am selben Listenplatz
	 * ersetzen
	 */
	private PageTableEntry clockAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte; // Aktuell untersuchter Seitentabelleneintrag

		// Immer ab altem "Uhrzeigerstand" weitersuchen
		pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);

		// Suche den nächsten Eintrag mit referenced == false (R-Bit = 0)
		while (pte.referenced) {
			// Seite wurde referenziert, also nicht auswählen, sondern R-Bit zurücksetzen
			os.testOut("Prozess " + pid + ": CLOCK-Algorithmus! --- pte.vpn: "
					+ pte.virtPageNum + " ref: " + pte.referenced);
			pte.referenced = false;
			incrementPteRAMlistIndex();
			pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);
		}

		// Seite ausgewählt! (--> pteRAMlistIndex)
		// Alte Seite gegen neue in pteRAMlist austauschen
		pteRAMlist.remove(pteRAMlistIndex);
		pteRAMlist.add(pteRAMlistIndex, newPte);
		// Index auf Nachfolger setzen
		incrementPteRAMlistIndex();
		os.testOut("Prozess " + pid
				+ ": CLOCK-Algorithmus hat pte ausgewählt: " + pte.virtPageNum
				+ "  Neuer pteRAMlistIndex ist " + pteRAMlistIndex);

		return pte;
	}

	/**
	 * RANDOM-Algorithmus: Zufällige Auswahl
	 */
	private PageTableEntry randomAlgorithm(PageTableEntry newPte) {
		Random rand = new Random();
		int i = rand.nextInt(0, pteRAMlist.size());
		PageTableEntry pageToReplace = pteRAMlist.get(i);

		// Suche den nächsten Eintrag mit referenced == false (R-Bit = 0)
		while (pageToReplace.referenced) {
			// Seite wurde referenziert, also nicht auswählen, sondern R-Bit zurücksetzen
			os.testOut("Prozess " + pid + ": RANDOM-Algorithmus! --- pte.vpn: "
					+ pageToReplace.virtPageNum + " ref: " + pageToReplace.referenced);
			pageToReplace.referenced = false;
			i = rand.nextInt(0, pteRAMlist.size());
			pageToReplace = pteRAMlist.get(i);
		}

		pteRAMlist.remove(pageToReplace);
		pteRAMlist.add(i, newPte);

		return pageToReplace;
	}

	// ----------------------- Hilfsmethode --------------------------------

	/**
	 * ramPteIndex zirkular hochzählen zwischen 0 .. Listengröße-1
	 */
	private void incrementPteRAMlistIndex() {
		pteRAMlistIndex = (pteRAMlistIndex + 1) % pteRAMlist.size();
	}

}
