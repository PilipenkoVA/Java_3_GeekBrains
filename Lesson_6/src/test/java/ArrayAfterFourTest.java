import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)

// Набор тестов для метода
public class ArrayAfterFourTest {
    @Parameterized.Parameters
    public static Collection<Object[]>data(){
        return Arrays.asList(new Object[][]{
                {new int[]{1, 4, 1, 1}, new int[]{1, 1}},
                {new int[]{4, 5, 6, 9}, new int[]{5, 6, 9}},
                {new int[]{4, 4, 4}, new int[0]},
                {new int[]{1, 2, 4, 1, 2}, new int[]{1, 2}},
        });
    }
    private int[] inArr;
    private int[] outArr;

    public ArrayAfterFourTest(int[] inArr, int[] outArr) {
        this.inArr = inArr;
        this.outArr = outArr;
    }
    @Test
    public void testAfterFour(){
        Assert.assertArrayEquals(outArr, HomeWork.getArrayAfterFour(inArr));
    }
}
