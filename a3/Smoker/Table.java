package Smoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Table {
    static ReentrantLock agentsMutex = new ReentrantLock(true);

    private static final int INGREDIENTS_NUMBER = 2;
    private static final int NEEDED_INGREDIENTS_NUMBER = 2;
    private static final int SMOKER_NUMBER = 3;
    private static final int AGENT_NUMBER = 2;
    static final ArrayList<String> agentIngredients = new ArrayList<>(Arrays.asList("Tobacco", "Paper", "Match"));
    public List<String> ingredients = new ArrayList<>();
    static ArrayList<Agent> agentList = new ArrayList<>();
    static ArrayList<Smoker> smokerList = new ArrayList<>();

    public Table() {
        for (int i = 0; i < AGENT_NUMBER; i++) {
            agentList.add(new Agent(this, String.valueOf(i+1), INGREDIENTS_NUMBER, agentIngredients));
        }

        for (int i = 0; i < SMOKER_NUMBER; i++) {
            smokerList.add(new Smoker(ingredients, agentsMutex, agentIngredients.get(i%agentIngredients.size()), this));
        }
    }

    public static void main(String[] args){

        new Table();
//        System.err.println("-------------------- THE END -------------------");
    }

    public synchronized void addIngredients(ArrayList<String> newIngredients) throws InterruptedException {
        while (this.ingredients.size() >= NEEDED_INGREDIENTS_NUMBER) {
            this.wait();
        }

        System.out.printf("\tAvailable Ingredients (before adding new ones): %s, Size: %d\n", this.ingredients, this.ingredients.size());

        this.ingredients = newIngredients;
        this.notifyAll();
    }

    public synchronized boolean getIngredients(String ownIngredient) throws InterruptedException {
        while (this.ingredients.size() != NEEDED_INGREDIENTS_NUMBER) {
            this.wait();
        }

        ArrayList<String> availableIngredients = new ArrayList<>(ingredients);

        availableIngredients.add(ownIngredient);
        System.out.printf("\tAvailable Ingredients: %s\n", availableIngredients);

        return availableIngredients.containsAll(agentIngredients);
    }

    public synchronized void finishedSmoking() {
        this.ingredients.clear();
        System.out.println("Ingredients have been used");

        this.notifyAll();
    }
}
