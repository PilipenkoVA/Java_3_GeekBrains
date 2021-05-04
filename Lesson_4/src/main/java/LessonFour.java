public class LessonFour {
    static volatile char c = 'A';
    static Object mon = new Object();

    static class WaitNotifyClass implements Runnable {
        private char currentChar;
        private char nextChar;

        public WaitNotifyClass(char currentChar, char nextChar) {
            this.currentChar = currentChar;
            this.nextChar = nextChar;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                synchronized (mon) {
                    try {
                        while (c != currentChar)
                            mon.wait();
                        System.out.print(currentChar);
                        c = nextChar;
                        mon.notifyAll();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        new Thread(new WaitNotifyClass('A', 'B')).start();
        new Thread(new WaitNotifyClass('B', 'C')).start();
        new Thread(new WaitNotifyClass('C', 'A')).start();
    }
}

