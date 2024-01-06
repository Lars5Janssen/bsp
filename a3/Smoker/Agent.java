package Smoker;

import java.util.ArrayList;

public class Agent extends Thread {

    Table table;
    String name;
    int ingredientsNumber;
    ArrayList<String> agentIngredients;

    public Agent(Table table, String name, int ingredientsNumber, ArrayList<String> list) {
        this.table = table;
        this.agentIngredients = list;
        this.name = name;
        this.ingredientsNumber = ingredientsNumber;
        start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.giveThing(table, ingredientsNumber);
                System.err.printf("AGENT %s HAS PUT DOWN THEIR INGREDIENTS\n", name.toUpperCase());
            } catch (InterruptedException ignored) {
                return;
            }
        }
    }

    public synchronized void giveThing(Table table, int ingredientsNumber) throws InterruptedException {
        ArrayList<String> ingredientsCopy = new ArrayList<>(agentIngredients);
        ArrayList<String> ingredientsToGive = new ArrayList<>();
        for (int i = 0; i < ingredientsNumber; i++) {
            int index = (int) (Math.random() * ingredientsCopy.size());
            ingredientsToGive.add(ingredientsCopy.get(index));
            //System.err.printf("\tAgent %s has put down %s\n", name.toUpperCase(), ingredientsCopy.get(index));
            ingredientsCopy.remove(index);
        }
        table.addIngredients(ingredientsToGive);
    }
}
