import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by 11962 on 2017/5/31.
 */
public class TestWebSocket {

    private static Timer mTimer;
    private static int count = 0;
    private static WebSocket mWebSocket = null;
    public static void main(String[] args) {
        String url ="ws://172.18.69.141:8080/isInvited";
        //新建client
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        //构造request对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        //建立连接
        client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                //打印一些内容
                System.out.println("client onOpen");
                System.out.println("client request header:" + response.request().headers());
                System.out.println("client response header:" + response.headers());
                System.out.println("client response:" + response);
                mWebSocket = webSocket;
                startTask();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                //打印一些内容
                System.out.println("client onMessage");
                System.out.println("message:" + text);
                if (count == 2) {
                    mTimer.cancel();
                    webSocket.close(1000, "close by client");
                    return;
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("client onClosing");
                System.out.println("code:" + code + " reason:" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                //打印一些内容
                System.out.println("client onClosed");
                System.out.println("code:" + code + " reason:" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                //出现异常会进入此回调
                System.out.println("client onFailure");
                System.out.println("throwable:" + t);
                System.out.println("response:" + response);
            }
        });
    }

    //每秒发送一条消息
    private static void startTask() {
        mTimer = new Timer();

        TimerTask timerTask = new TimerTask() {
            String name = "Jack";
            String title = "my";
            String sendJson = "{'friend':" + name + ", 'title':" + title + "}";
            @Override
            public void run() {
                if (mWebSocket == null) return;
                boolean isSuccessed = mWebSocket.send(sendJson);
                count++;
                //除了文本内容外，还可以将如图像，声音，视频等内容转为ByteString发送
                //boolean send(ByteString bytes);
            }
        };
        mTimer.schedule(timerTask, 0, 1000);
    }
}

