package SimRacing;

public class SimRaceing {

    public static void main(String[] args) {
        int numberOfCars = 5;
        Monitor monitor = new Monitor(numberOfCars);
        for (int i = 0; i < numberOfCars; i++) {
            Integer numberOfLaps = 3;
            RaceCar raceCar = new RaceCar(i, numberOfLaps, monitor);
            raceCar.start();
        }
    }
}
