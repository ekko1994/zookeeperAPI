package com.itcast.example;

import com.itcast.watcher.ZKConnectionWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanghao
 * @date 2020/6/8 - 10:01
 */
public class MyConfigCenter implements Watcher {

    // zk的IP
    String IP = "192.168.44.139:2181";

    static ZooKeeper zooKeeper;

    CountDownLatch countDownLatch = new CountDownLatch(1);

    private String url;
    private String username;
    private String password;

    public static void main(String[] args) {
        try {
            MyConfigCenter myConfigCenter = new MyConfigCenter();
            for (int i = 0; i < 10; i++) {
                try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
                System.out.println("url = " + myConfigCenter.getUrl());
                System.out.println("username = " + myConfigCenter.getUsername());
                System.out.println("password = " + myConfigCenter.getPassword());
                System.out.println("=============================================");
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            // 捕获事件状态
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                }else if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("断开连接!");
                }else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("会话超时!");
                    // 超时后服务器已经将连接释放，需要冲洗连接服务器
                    Watcher watcher;
                    zooKeeper = new ZooKeeper(IP, 5000, new ZKConnectionWatcher());
                }else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败!");
                }
            }else if (event.getType() == Event.EventType.NodeDataChanged){
                initValue();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 连接zookeeper服务器，读取配置信息
    public void initValue(){
        try {
            // 创建连接对象
            zooKeeper = new ZooKeeper(IP, 5000, this);
            // 等待连接创建成功
            countDownLatch.await();
            // 读取配置信息
            this.url = new String(zooKeeper.getData("/config/url",true,null));
            this.username = new String(zooKeeper.getData("/config/username",true,null));
            this.password = new String(zooKeeper.getData("/config/password",true,null));
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    // 构造方法
    public MyConfigCenter(){
        initValue();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
