public class SongTest {
    public static int getValGreater(int max, int min, int balance) {
        return (int) ((max - min) / (double) 100 * (100 - balance) + min);
    }

    public static int getValLess(int max, int min, int balance) {
        return (int) ((max - min) / (double) 100 * balance + min);
    }
    public static void main(String[] args) {

        int valGreater = getValLess(70, 20, 100);
        System.out.println(valGreater);
    }
}
