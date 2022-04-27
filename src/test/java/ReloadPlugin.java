import com.google.gson.Gson;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.PluginReloadRequest;
import com.molean.isletopia.shared.utils.RedisUtils;

public class ReloadPlugin {

    public static void sendMessage(String target, String channel, Object object) {
        WrappedMessageObject wrappedMessageObject = new WrappedMessageObject();
        wrappedMessageObject.setMessage(new Gson().toJson(object));
        wrappedMessageObject.setFrom("Console");
        wrappedMessageObject.setTo(target);
        wrappedMessageObject.setSubChannel(channel);
        wrappedMessageObject.setTime(System.currentTimeMillis());
        RedisUtils.getCommand().publish("ServerMessage", new Gson().toJson(wrappedMessageObject));

    }

    public static void main(String[] args) {
        sendMessage("server1", "PluginReload", new PluginReloadRequest());
    }
}
