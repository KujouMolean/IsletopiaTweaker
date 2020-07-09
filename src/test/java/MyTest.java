public class MyTest {
    public static void main(String[] args) {
        System.out.println("[abc]".matches("\\[[a-zA-Z0-9].*\\]"));
        String line = "[abc]";
        String target = line.substring(1,line.length()-1);
        System.out.println(target);
    }
}
