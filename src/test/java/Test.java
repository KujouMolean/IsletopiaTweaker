import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static int getPoints(int uid) {
        byte[] bytes = new byte[0];
        try {
            URL url = new URL("https://www.mcbbs.net/?" + uid);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.109 Safari/537.36");
            InputStream inputStream = urlConnection.getInputStream();
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String source = new String(bytes, StandardCharsets.UTF_8);
        Pattern compile = Pattern.compile("<li><em>积分</em>(.{1,30})</li><li>");
        Matcher matcher = compile.matcher(source);

        int points = 0;

        while (matcher.find()) {
            try {
                points = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }

        return points;
    }

    public static void main(String[] args) {
        System.out.println(getPoints(2583771));


    }
}
