package SimRacing;

import java.util.HashMap;
import java.util.Map;

public class Monitor {
    private Map<Integer, Long> times;
    private Integer numberOfExpectedCars;
    private Integer numberOfCars;
    public Monitor(Integer numberOfCars) {
        this.numberOfExpectedCars = numberOfCars;
        this.numberOfCars = 0;
        times = new HashMap<>();
    }

    public void enterTimeOfCar(Integer carNumber, Long time) {
        times.put(carNumber, time);
        numberOfCars++;
        if (numberOfCars.equals(numberOfExpectedCars)) {
            printResults();
        }
    }

    private void printResults() {
        System.out.println("*** Endstand ***");
        for (int i = 0; i < numberOfExpectedCars; i++) {
            int formatedCarNumber = i + 1;
            System.out.printf("%s. Platz: Wagen %s Zeit: %s\n", formatedCarNumber, formatedCarNumber, times.get(i));
        }
    }
}