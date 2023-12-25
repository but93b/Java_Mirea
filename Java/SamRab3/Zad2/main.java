import io.reactivex.rxjava3.core.Observable;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.core.Observable;

public class Main1 {
    public static void main(String[] args) {
        // Преобразовать поток из случайного количества (от 0 до 1000)
        // случайных чисел в поток, содержащий количество чисел.

        System.out.println("Поток количества чисел");
        Observable.range(1, (int) Math.round(Math.random() * 1000))
                .count()
                .subscribe(System.out::println);
    }
}

public class Main2 {
    private static boolean isEnd = false;

    public static void main(String[] args) {
        Observable<Integer> observable1 = Observable
                .intervalRange(1, 10, 0, 500, TimeUnit.MILLISECONDS)
                .map(i -> getRandomNumber1());
        Observable<Integer> observable2 = Observable
                .intervalRange(1, 10, 15, 500, TimeUnit.MILLISECONDS)
                .map(i -> getRandomNumber2());

        Observable<Serializable> observableMain = Observable.merge(observable1, observable2);


        CountDownLatch latch = new CountDownLatch(1);

        observableMain.subscribe(System.out::print, System.out::print, latch::countDown);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int getRandomNumber(int min, int max) {
        return (int) Math.round(Math.random() * (max - min) + min);
    }
    private static int getRandomNumber1() {
        return getRandomNumber(0, 0);
    }
    private static int getRandomNumber2() {
        return getRandomNumber(9, 9);
    }
}

public class Main3 {
    public static void main(String[] args) {
        Observable.range(1, 10)
                .map(i -> getRandomNumber(0, 10))
                .takeLast(1)
                .subscribe(System.out::println);
    }

    private static int getRandomNumber(int min, int max) {
        return (int) Math.round(Math.random() * (max - min) + min);
    }
}