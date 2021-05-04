package Lesson.Five;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Main {

    public static final int CARS_COUNT = 4;
    public static final int HALF_CARS_COUNT = CARS_COUNT/2;                                              // <-- дополнил

    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        CyclicBarrier cb = new CyclicBarrier(5);                                                  // <-- дополнил
        CountDownLatch cdl = new CountDownLatch(CARS_COUNT);                                             // <-- дополнил
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10),cb, cdl);             // <-- дополнил немного
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        try {                                                                                            // <-- дополнил
            cb.await();                                                                                  // <-- дополнил
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
            cb.await();                                                                                  // <-- дополнил
            cb.await();                                                                                  // <-- дополнил
        } catch (Exception e) {                                                                          // <-- дополнил
            e.printStackTrace();                                                                         // <-- дополнил
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}

