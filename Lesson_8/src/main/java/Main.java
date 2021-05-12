import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {

    public static void main(String[] args) throws Exception {

        working(TestClass.class);
    }

    public static void working(Class c) throws Exception{
        Object testObj = c.newInstance();                                   // <-- создаем Object
        ArrayList<Method> methods = new ArrayList<>();
        for (Method m : c.getDeclaredMethods()) {                           // <-- запрашиваем у класса все методы
            if (m.isAnnotationPresent(Test.class)) {                        // <-- если метод помечен аннотацией "@Test"
                // если e метода "Test" значение приоритета меньше или больше заданных кидаем "Exception"
                if (m.getAnnotation(Test.class).value() < 1 || m.getAnnotation(Test.class).value() > 10){
                    throw new RuntimeException("Измените значение value");
                }
                methods.add(m);                                             // <-- то, добавляем его в список
            }
        }
        // Сортируем методы с аннотацией "@Test" по приоритету
        methods.sort((o1, o2) -> - (o1.getAnnotation(Test.class).value() - o2.getAnnotation(Test.class).value()));

        // После того как раставили все методы с аннотацией "@Test" в нужной последователььности

        // Запрашиваем у класса все методы для поиска метода "@BeforeSuite"
        for (Method m : c.getDeclaredMethods()) {
            if (m.isAnnotationPresent(BeforeSuite.class)) {
                // Проверяем наличие метода с аннотацией @BeforeSuite на первом месте списка если есть, то кидаем "Exception"
                if (methods.size() > 0 && methods.get(0).isAnnotationPresent(BeforeSuite.class)){
                    throw new RuntimeException("Метод с аннотацией @BeforeSuite должен присутствовать в единственном экземпляре");
                }
                // если его находим, то ставим его в начало нашего списка т.к. он должен выполниться первым
                methods.add(0, m);
            }
            if (m.isAnnotationPresent(AfterSuite.class)) {
                // Проверяем наличие метода с аннотацией @AfterSuite на последнем месте списка если есть, то кидаем "Exception"
                if (methods.size() > 0 && methods.get(methods.size() - 1).isAnnotationPresent(AfterSuite.class)){
                    throw new RuntimeException("Метод с аннотацией @AfterSuite должен присутствовать в единственном экземпляре");
                }
                // если его находим, то ставим его в конец нашего списка т.к. он должен выполниться последним
                methods.add(m);
            }
        }
        // Проходим по всему списку и вызываем наши методы
        for (int i = 0; i < methods.size(); i++) {
            methods.get(i).invoke(testObj, null);
        }
    }
}
