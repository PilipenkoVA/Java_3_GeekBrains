package Lesson.Five;

import java.util.concurrent.Semaphore;

import static Lesson.Five.Main.HALF_CARS_COUNT;

public class Tunnel extends Stage {
    Semaphore smp = new Semaphore(Main.HALF_CARS_COUNT);                                                 // <-- дополнил

    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                smp.acquire();                                                                           // <-- дополнил
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(c.getName() + " закончил этап: " + description);
                smp.release();                                                                           // <-- дополнил
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
