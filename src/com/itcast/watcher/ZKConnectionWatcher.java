package com.itcast.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhanghao
 * @date 2020/6/7 - 19:08
 */
public class ZKConnectionWatcher implements Watcher{

    // 计数器对象
    static CountDownLatch countDownLatch = new CountDownLatch(1);

    // 连接对象
    static ZooKeeper zooKeeper;

    static String  IP = "192.168.44.139:2181";

    public static void main(String[] args) {
        try {
            zooKeeper = new ZooKeeper(IP, 5000, new ZKConnectionWatcher());
            countDownLatch.await();
            System.out.println(zooKeeper.getSessionId());
            zooKeeper.addAuthInfo("digest","jack:jack".getBytes());
            byte[] data = zooKeeper.getData("/node1", false, null);
            System.out.println(new String(data));
            Thread.sleep(50000);
            zooKeeper.close();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            // 事件类型
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                }else if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("断开连接!");
                }else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("会话超时!");
                }else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败!");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
