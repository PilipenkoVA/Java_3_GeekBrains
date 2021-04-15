import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // создаем массив
        Integer[] arr = {0,1,2,3,4,5};
        System.out.println(Arrays.toString(arr));                         // печатаем массив

        // меняем два элемента местами "сам метод написан ниже"
        changeTwoElements(arr,0, 4);
        System.out.println(Arrays.toString(arr));                         // печатаем массив снова

        // создаем две коробки в которые добавляем яблоки
        FruitBox<Apple> appleBox1 =new FruitBox<>(new Apple(),new Apple());
        FruitBox<Apple> appleBox2 =new FruitBox<>(new Apple(),new Apple(), new Apple());

        // создаем две коробки в которые добавляем апельсины
        FruitBox<Orange> orangeBox1 =new FruitBox<>(new Orange(),new Orange());
        FruitBox<Orange> orangeBox2 =new FruitBox<>(new Orange(),new Orange(),new Orange());

        // проверяем количество апельсинов в коробках
        System.out.println(appleBox1.getSize());
        System.out.println(appleBox2.getSize());

        // Сравниваем коробки по весу
        System.out.println(orangeBox1.compare(orangeBox2));      // если сравнить с "appleBox2", то ответ будет "true"

        orangeBox1.transfer(orangeBox2);                         // перекладываем апельсины в одну коробку

        // проверяем снова количество апельсинов в коробках
        System.out.println(orangeBox1.getSize());
        System.out.println(orangeBox2.getSize());

        // дополнительно добавляем одно яблоко в первую коробку
        appleBox1.addFruit(new Apple());
        System.out.println(appleBox1.getSize());                 // проверяем количество я блок в коробке после добавления
    }

    // 1. Метод который меняет два элемента массива местами.(массив может быть любого типа);
    public static <T> void changeTwoElements(T[] arr, int index1, int index2){
        T obj = arr[index1];
        arr[index1]= arr[index2];
        arr[index2]=obj;
    }

    // 2. Метод который преобразует массив в ArrayList;
    public static <T> ArrayList<T> arrayToArrayList(T[] array){
            return new ArrayList<>(Arrays.asList(array));
    }
}
