import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)

// Набор тестов для метода
public class СheckOneAndFourTest {
    @Parameterized.Parameters
    public static Collection<Object[]>data(){
        return Arrays.asList(new Object[][]{
                {new int[]{1, 4, 1, 1}, true},
                {new int[]{1, 1, 1, 1}, false},
                {new int[]{4, 4, 4, 1}, true},
                {new int[]{1, 2, 4, 1}, false},
        });
    }
    private int[] arr;
    private boolean result;

    public СheckOneAndFourTest(int[] arr, boolean result) {
        this.arr = arr;
        this.result = result;
    }
    @Test
    public void testOneAndFour(){
        Assert.assertEquals(result, HomeWork.checkOneAndFour(arr));
    }
}
