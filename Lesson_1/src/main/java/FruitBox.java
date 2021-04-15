import java.util.ArrayList;
import java.util.Arrays;

public class FruitBox <T extends Fruit> {
    ArrayList <T> fruitsBox;

    public int getSize(){
        return fruitsBox.size();
    }

    public FruitBox(T... fruits){
        this.fruitsBox = new ArrayList(Arrays.asList(fruits));
    }

    // d. Метод для подсчета веса коробки
    public float getWeight() {
        float s = 0.0f;
        for (T fruit: fruitsBox) {
            s += fruit.getWeight();
        }
        return  s;
    }

    // e. Метод сравнения коробок по весу
    public boolean compare(FruitBox<?> another) {
        return Math.abs(this.getWeight() - another.getWeight()) < 0.001;
    }

    // f. Метод позволяющий пересыпать фрукты
    public void transfer(FruitBox<? super T> another) {
        if(another == this) {
            return;
        }
        another.fruitsBox.addAll(this.fruitsBox);
        this.fruitsBox.clear();
    }
    // g. Метод для добавления фруктов в коробку
    public void addFruit(T fruit) {
        fruitsBox.add(fruit);
    }
}