package Smoker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Smoker extends Thread {

    ArrayList<String> ingredients;
    String ownIngredient;
    ReentrantLock agentMutex;
    String name;
    Table table;

    public Smoker(List<String> ingredients, ReentrantLock agentMutex, String ingredient, Table table) {
        this.ingredients = (ArrayList<String>) ingredients;
        this.ownIngredient = ingredient;
        this.agentMutex = agentMutex;
        this.name = ingredient.toUpperCase();
        this.table = table;
        start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                tryIngredients();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void tryIngredients() throws InterruptedException {
        System.out.printf("\nSmoker %s tries ingredients\n", name);
        if (this.table.getIngredients(name, ownIngredient)) {
            table.clearIngredients();
            smoke();
        }
        Thread.sleep(5);
    }

    public void smoke(){
        // Smoke for random time
        try {
            Thread.sleep((long) ((Math.random() * (100 - 1)) + 1000));
            System.out.printf("Smoker %s has smoked\n", name.toUpperCase());
        } catch (InterruptedException ignored) {}
    }
}
