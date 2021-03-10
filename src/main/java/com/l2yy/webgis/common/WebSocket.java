package com.l2yy.webgis.common;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/7 2:12 下午
 * @description：websocket
 */
@Component
@ServerEndpoint(value = "/websocket/{cityId}")
public class WebSocket {

    private Session session;

    private static CopyOnWriteArraySet<WebSocket> webSockets =new CopyOnWriteArraySet<>();
    private static Map<String,Session> sessionPool = new HashMap<String,Session>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value="cityId")String shopId) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(shopId, session);
        System.out.println("【websocket消息】有新的连接，总数为:"+webSockets.size());
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        System.out.println("【websocket消息】连接断开，总数为:"+webSockets.size());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("【websocket消息】收到客户端消息:"+message);
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
        for(WebSocket webSocket : webSockets) {
            System.out.println("【websocket消息】广播消息:"+message);
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息 (发送文本)
    public void sendMessage(String shopId, String message) {
        Session session = sessionPool.get(shopId);
        if (session != null) {
            try {
                synchronized (session){
                    session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息 (发送对象)
    public void sendObjMessage(String shopId, Object message) {
        Session session = sessionPool.get(shopId);
        if (session != null) {
            try {
                session.getAsyncRemote().sendObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//    /**
//     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
//     */
//    private static int onlineCount = 0;
//    /**
//     * concurrent包的线程安全Set，用来存放每个客户端对应的CumWebSocket对象。
//     */
//    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();
//    /**
//     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
//     */
//    private Session session;
//
//    /**
//     * 连接建立成功调用的方法
//     *
//     * @param session
//     */
//    @OnOpen
//    public void onOpen(Session session) {
//        this.session = session;
//        //加入set中
//        webSocketSet.add(this);
//        //添加在线人数
//        addOnlineCount();
//        System.out.println("新连接接入。当前在线人数为：" + getOnlineCount());
//    }
//
//    /**
//     * 连接关闭调用的方法
//     */
//    @OnClose
//    public void onClose() {
//        //从set中删除
//        webSocketSet.remove(this);
//        //在线数减1
//        subOnlineCount();
//        System.out.println("有连接关闭。当前在线人数为：" + getOnlineCount());
//    }
//
//    /**
//     * 收到客户端消息后调用
//     *
//     * @param message
//     * @param session
//     */
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        System.out.println("客户端发送的消息：" + message);
//    }
//
//    /**
//     * 暴露给外部的群发
//     *
//     * @param message
//     * @throws IOException
//     */
//    public static void sendInfo(String message) throws IOException {
//        sendAll(message);
//    }
//
//    /**
//     * 群发
//     *
//     * @param message
//     */
//    private static void sendAll(String message) {
//        Arrays.asList(webSocketSet.toArray()).forEach(item -> {
//            WebSocket customWebSocket = (WebSocket) item;
//            //群发
//            try {
//                customWebSocket.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    /**
//     * 发生错误时调用
//     *
//     * @param session
//     * @param error
//     */
//    @OnError
//    public void onError(Session session, Throwable error) {
//        System.out.println("----websocket-------有异常啦");
//        error.printStackTrace();
//    }
//
//    /**
//     * 减少在线人数
//     */
//    private void subOnlineCount() {
//        WebSocket.onlineCount--;
//    }
//
//    /**
//     * 添加在线人数
//     */
//    private void addOnlineCount() {
//        WebSocket.onlineCount++;
//
//    }
//
//    /**
//     * 当前在线人数
//     *
//     * @return
//     */
//    public static synchronized int getOnlineCount() {
//        return onlineCount;
//    }
//
//    /**
//     * 发送信息
//     *
//     * @param message
//     * @throws IOException
//     */
//    public void sendMessage(String message) throws IOException {
//        //获取session远程基本连接发送文本消息
//        System.out.println(message + "123"+this.session.getId());
//
//        this.session.getBasicRemote().sendText(message);
//        //this.session.getAsyncRemote().sendText(message);
//    }
}