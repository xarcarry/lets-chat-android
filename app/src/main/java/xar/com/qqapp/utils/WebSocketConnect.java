package xar.com.qqapp.utils;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_75;
import org.java_websocket.drafts.Draft_76;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xar.com.qqapp.ChatActivity;
import xar.com.qqapp.bean.Message;
import xar.com.qqapp.bean.MyApplication;
import xar.com.qqapp.utils.code.MsgType;
import xar.com.qqapp.utils.voice.VoicePlayUtil;

public class WebSocketConnect {
    private static WebSocketClient client;// 连接客户端
    private static DraftInfo selectDraft;// 连接协议
    private final ChatActivity activity;
    private RecyclerView recyclerView;

    public WebSocketConnect(ChatActivity activity, RecyclerView recyclerView){
        this.activity = activity;
        this.recyclerView = recyclerView;
    }

    public void connectServer(){
        DraftInfo[] draftInfos = {new DraftInfo("WebSocket协议Draft_17", new Draft_17()), new DraftInfo
                ("WebSocket协议Draft_10", new Draft_10()), new DraftInfo("WebSocket协议Draft_76", new Draft_76()), new
                DraftInfo("WebSocket协议Draft_75", new Draft_75())};// 所有连接协议
        selectDraft = draftInfos[0];// 默认选择第一个连接协议

        try{
        if (selectDraft == null) {
            return;
        }

        //生成随机三位整数
            Random rd = new Random();
            String id = "";
        for (int i=0;i<3;i++)
            id += rd.nextInt(11);

        String url = "ws://192.168.43.22:8080";
        String path = "/websocket/" + id;
        StringBuffer address = new StringBuffer(url);
        address.append(path);
        Log.e("wlf", "连接地址：" + address);
        client = new WebSocketClient(new URI(address.toString()), selectDraft.draft) {
            private int count = 0;
            @Override
            public void onOpen(final ServerHandshake serverHandshakeData) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("wlf", "已经连接到服务器【" + getURI() + "】");

                    }
                });
            }

            @Override
            public void onMessage(final String message) {
                Log.e("wlf", "收到了消息！");
                /* (count == 0){

                }else {*/
                    String backUrl = "chat/getMsg";
                    OkHttpUtil.getObject(backUrl, receivedMsgHandle());
                    /*switch (){

                    }*/
                   /* Message receivedMsg = (Message) JSONObject.parse(message);
                    Log.e("wlf", "消息转换成功！");
                    switch (receivedMsg.getType()) {
                        case MsgType.MSG_TEXT:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("wlf", "获取到服务器信息【" + message + "】");
                                    activity.receiveMsg(new ChatActivity.ChatMessage("webUser"
                                            , new Date().getTime(), message, false));
                                    activity.flushChatView(recyclerView);
                                }
                            });
                            break;
                        case MsgType.MSG_VOICE:
                            Log.e("wlf", "收到了语音消息！");
                            try {
                                byte[] temp = receivedMsg.getContent().getBytes("utf-8");
                                VoicePlayUtil.PlayMar(MyApplication.getContext(), temp);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }*/
               /* }
                count ++;*/
            }

            @Override
            public void onClose(final int code, final String reason, final boolean remote) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("wlf", "断开服务器连接【" + getURI() + "，状态码： " + code + "，断开原因：" + reason + "】");

                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("wlf", "连接发生了异常【异常原因：" + e + "】");

                    }
                });
            }
        };
        client.connect();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public Callback receivedMsgHandle(){
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.e("wlf", response.body().string());
                JSONObject json = (JSONObject) JSONObject.parse(response.body().string());
                Message msg = JSONObject.toJavaObject(json, Message.class);
                switch (msg.getType()){
                    case MsgType.MSG_TEXT:
                        handleTextMsg(msg);
                        break;
                    case MsgType.MSG_VOICE:
                        Log.e("wlf", "收到了语音消息！");
                        handleVoiceMsg(msg);
                        break;
                    default:
                        break;
                }
            }
        };
    }
    //收到文本消息的处理事件
    public void handleTextMsg(Message msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("wlf", "获取到服务器信息【" + msg.getContent() + "】");
                activity.receiveMsg(new ChatActivity.ChatMessage("webUser"
                        , new Date().getTime(), msg.getContent(), false));
                activity.flushChatView(recyclerView);
            }
        });
    }

    //收到语音消息处理事件
    public void handleVoiceMsg(Message msg){
        try {
            Log.e("wlf", msg.getFileByte().length + "");
            VoicePlayUtil.startPlay(VoicePlayUtil.createFile(msg.getFileByte()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private class DraftInfo {

    private final String draftName;
    private final Draft draft;

    public DraftInfo(String draftName, Draft draft) {
        this.draftName = draftName;
        this.draft = draft;
    }

    @Override
    public String toString() {
        return draftName;
    }
}

private class ServerInfo {

    private final String serverName;
    private final String serverAddress;

    public ServerInfo(String serverName, String serverAddress) {
        this.serverName = serverName;
        this.serverAddress = serverAddress;
    }

    @Override
    public String toString() {
        return serverName;
    }
}
}
