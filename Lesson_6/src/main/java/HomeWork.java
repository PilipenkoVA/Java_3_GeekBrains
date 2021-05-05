import java.util.Arrays;

public class HomeWork {

    // Метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив
    public static int[] getArrayAfterFour(int[] arr){
        for (int i = arr.length - 1; i >= 0 ; i--) {
            if (arr[i] == 4){
                // Возвращаем новый массив из элементов, идущих после последней четверки
                return Arrays.copyOfRange(arr,  i + 1, arr.length);
            }
        }
        // Если не будет в массиве четверки, то выдаст RuntimeException "missed is 4"
        throw new RuntimeException("NOT is 4 (Four)");
    }
    // Метод, который проверяет состав массива из чисел 1 и 4.
    public static boolean checkOneAndFour(int[] arr){
        boolean value1 = false, value2 = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1){
                value1 = true;
            }else if (arr[i] == 4){
                value2 = true;
            // Если в нем нет хоть одной четверки или единицы, то метод вернет false
            }else{
                return false;
            }
        }
        return value1 & value2;
    }
}
