import org.junit.Test;

// Tест для RuntimeException
public class RuntimeExceptionTest {
    @Test(expected = RuntimeException.class)
    public void testRuntimeException(){
        int[] arr = {1,2,3,5,6,7};
        HomeWork.getArrayAfterFour(arr);
    }
}
