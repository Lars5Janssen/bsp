package SimRacing;

public class SimRaceing {
    private static Integer numberOfCars = 4;
    private static Integer numberOfLaps = 1;

    public static void main(String[] args) {
        Monitor monitor = new Monitor(numberOfCars);
        for (int i = 0; i < numberOfCars; i++) {
            RaceCar raceCar = new RaceCar(i, numberOfLaps, monitor);
            raceCar.start();
        }
    }
}
