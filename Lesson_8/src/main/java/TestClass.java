public class TestClass {
    @BeforeSuite
    public void startBeforeSuite() {
        System.out.println("Должен выполниться первым т.к. аннотация @BeforeSuite");
    }

    @Test
    public void test_1() {
        System.out.println("test_1 (нет проставлен приоритет - default значение = 1)");
    }

    @Test
    public void test_2() {
        System.out.println("test_2 (нет проставлен приоритет - default значение = 1)");
    }

    @Test(value = 6)
    public void test_3() {
        System.out.println("test_3 (приоритет = 6)");
    }

    @Test(value = 8)
    public void test_4() {
        System.out.println("test_4 (приоритет = 8)");
    }

    @AfterSuite
    public void stopAfterSuite() {
        System.out.println("Должен выполниться последним т.к. аннотация @AfterSuite");
    }

}