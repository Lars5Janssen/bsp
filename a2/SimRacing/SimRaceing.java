package SimRacing;

import java.util.ArrayList;

public class SimRaceing {

    public static void main(String[] args) throws InterruptedException {
        int numberOfCars = 5;
        Monitor monitor = new Monitor(numberOfCars);
        ArrayList<RaceCar> raceCars = new ArrayList<>();
        for (int i = 0; i < numberOfCars; i++) {
            Integer numberOfLaps = 3;
            RaceCar raceCar = new RaceCar(i, numberOfLaps, monitor);
            raceCar.start();
            raceCars.add(raceCar);
        }
        for (int i = 0; i < numberOfCars; i++) {
            raceCars.get(i).join();
        }
        monitor.printResults();
    }
}
