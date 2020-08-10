import com.molean.isletopia.DBUtils;

public class MyTest {
    public static void main(String[] args) {
        String s = DBUtils.get("Yuba", "lastServer2");
        System.out.println(s);
    }
}
