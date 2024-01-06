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
                return;
            }
        }
    }

    public void tryIngredients() throws InterruptedException {
        System.out.printf("\tSmoker %s tried ingredients\n\n", name);
        if (this.table.getIngredients(ownIngredient)) {
            System.err.printf("\nSMOKER %s IS SMOKING.\n", name.toUpperCase());
            table.clearItems();
            smoke();
            table.finishedSmoking();
        }
        Thread.sleep(5);
    }

    public void smoke(){
        // Smoke for random time
        try {
            Thread.sleep((long) ((Math.random() * (100 - 1)) + 1000));
            System.err.printf("\nSMOKER %s HAS FINISHED SMOKING.\n", name.toUpperCase());
        } catch (InterruptedException ignored) {}
    }
}
