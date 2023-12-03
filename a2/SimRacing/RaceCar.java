package SimRacing;

import java.util.Random;

public class RaceCar extends Thread{
     private Integer carNumber;
     private Integer numberOfLaps;
     private Monitor monitor;
     private Long startTime;
     private Long endTime;

     RaceCar(Integer carNumber, Integer numberOfLaps, Monitor monitor) {
         this.carNumber = carNumber;
         this.numberOfLaps = numberOfLaps;
         this.monitor = monitor;
     }

    @Override
    public void run() {
         startTime = System.currentTimeMillis();
         Random lapTime = new Random();
         for (int i = 0; i < numberOfLaps; i++) {
             long lap = lapTime.nextLong(100);
             try {
                 Thread.sleep(lap);
             } catch (InterruptedException e) {
                 throw new RuntimeException(e);
             }
         }
         endTime = System.currentTimeMillis();
         monitor.enterTimeOfCar(carNumber, endTime-startTime);
     }
}
