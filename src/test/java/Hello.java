import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class Hello {

    //模拟IO读取一些数据, 异步完成, 通过回调函数给出结果
    public static void getNumberFromDisk_simulate(Consumer<Integer> consumer) {
        new Thread(() -> {
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            consumer.accept(new Random().nextInt());
        }).start();
    }

    @SuppressWarnings("all")
    public static void main(String[] args) {
        Random random = new Random();
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //在异步读取数据后保存到列表
            getNumberFromDisk_simulate((theNumber)->{
                synchronized (integers) {
                    integers.add(theNumber);
                }
            });
        }

        // 如何等待这些异步线程完成后 再开始输出?
        for (Integer i : integers) {
            System.out.println(i);
        }

    }
}
